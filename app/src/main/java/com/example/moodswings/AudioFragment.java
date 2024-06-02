package com.example.moodswings;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.moodswings.Sentiment.SentimentAnalyzer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.firebase.storage.StorageMetadata;

public class AudioFragment extends Fragment {
    LottieAnimationView recordingAnimation, PlayPauseButton, emoji;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private MediaPlayer mediaPlayer;
    private Boolean isPlaying = false;

    private boolean isRecording = false;

    public String path;

    LinearLayout SentimentContainer;
    ProgressBar progressBar;

    TextView tvText, tvSentiment, tvConfidence, tvTimeStamp;
    Button transcribeButton;


    private StorageReference storageReference;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    ExecutorService sentimentExecutor = Executors.newSingleThreadExecutor();



    public final String TAG = "AudioFragment";

    SentimentAnalyzer sentimentAnalyzer;

    ConstraintLayout emojiContainer;

    Button suggestSongs;

    String sentiment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_fragment, container, false);
        recordingAnimation = view.findViewById(R.id.animationView);
        PlayPauseButton = view.findViewById(R.id.animationViewPlaypause);
        SentimentContainer = view.findViewById(R.id.linearLayoutSentimentResult);
        progressBar = view.findViewById(R.id.pgSentiment);
        transcribeButton = view.findViewById(R.id.transcirbe_btn);

        tvText = view.findViewById(R.id.tvText);
        tvSentiment = view.findViewById(R.id.tvSentiment);
        tvConfidence = view.findViewById(R.id.tvConfidence);
        tvTimeStamp = view.findViewById(R.id.tvTimeStamp);
        emojiContainer=view.findViewById(R.id.emojiContainer);
        emoji=view.findViewById(R.id.ShowemojiAnim);
        suggestSongs=view.findViewById(R.id.SuggestSongs);


        suggestSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path!=null){
                    Intent intent = new Intent(getActivity(), ShowSongs.class);
                    intent.putExtra("Sentiment",sentiment);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"Please record the audio First",Toast.LENGTH_SHORT).show();
                }

            }
        });
        sentimentAnalyzer = new SentimentAnalyzer(); // Initialize your sentiment analyzer here
        storageReference = FirebaseStorage.getInstance().getReference();
        transcribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {

//                    transcribeAndAnalyzeAudio(path);
                      uploadAudioToFirebaseInBackground(path);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Record the Audio First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recordingAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRecordingPermission()) {
                    toggleRecording();
                } else {
                    requestRecordingPermission();
                }
            }
        });

        PlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        return view;
    }

    private void uploadAudioToFirebaseInBackground(String filePath) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                uploadAudioToFirebase(filePath);
            }
        });
    }
    private void uploadAudioToFirebase(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            Log.e(TAG, "File not found: " + filePath);
            return;
        }

        Uri fileUri = Uri.fromFile(audioFile);
        String fileName = audioFile.getName();

        // Explicitly specify the MIME type for audio files
        StorageReference audioRef = storageReference.child("audio/" + fileName);
        UploadTask uploadTask = audioRef.putFile(fileUri, new StorageMetadata.Builder()
                .setContentType("audio/3gpp") // Set the MIME type explicitly
                .build());

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        Log.d(TAG, "Audio uploaded successfully. Download URL: " + downloadUrl);
                        // Now you can pass this downloadUrl to your sentiment analyzer or use it as needed
                        transcribeAndAnalyzeAudio(downloadUrl);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get download URL: " + e.getMessage());

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to upload audio file: " + e.getMessage());

            }
        });
    }

    private void transcribeAndAnalyzeAudio(String audioPath) {
        progressBar.setVisibility(View.VISIBLE);
        SentimentContainer.setVisibility(View.GONE);

        Callable<String> transcribeTask = () -> {
            try {
                String transcriptId = sentimentAnalyzer.transcribeAudio(audioPath);
                return sentimentAnalyzer.pollTranscription(transcriptId);
            } catch (Exception e) {
                Log.e(TAG, "Transcription failed", e);
                return null;
            }
        };

        Future<String> future = sentimentExecutor.submit(transcribeTask);

        sentimentExecutor.execute(() -> {
            try {
                String result = future.get();
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    SentimentContainer.setVisibility(View.VISIBLE);
                    emojiContainer.setVisibility(View.VISIBLE);

                    if (result != null) {
                        String lastSentiment = extractLastSentiment(result);
                        if (lastSentiment != null) {
                            updateUIWithSentiment(lastSentiment);
                        } else {
                            Log.e(TAG, "No sentiment analysis results found");
                        }
                    } else {
                        Log.e(TAG, "Transcription failed, no results");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error processing transcription result", e);
            }
        });
    }

    private void updateUIWithSentiment(String sentimentData) {
        JsonObject sentimentJson = JsonParser.parseString(sentimentData).getAsJsonObject();

        String text = sentimentJson.get("text").getAsString();
        String sentiment = sentimentJson.get("sentiment").getAsString();
        double confidence = sentimentJson.get("confidence").getAsDouble();
        int start = sentimentJson.get("start").getAsInt();
        int end = sentimentJson.get("end").getAsInt();

        tvText.setText(text);
        tvSentiment.setText(sentiment);
        tvConfidence.setText(String.valueOf(confidence));
        tvTimeStamp.setText(String.format("%d - %d", start, end));

        ChangeEmojiSentiment(sentiment);
    }


    private  void ChangeEmojiSentiment(@NonNull String sentiment){

         this.sentiment=sentiment.toLowerCase();
        if(sentiment.toLowerCase().equals("neutral")){
            emoji.setAnimation(R.raw.neutral_emoji);
            Toast.makeText(getActivity().getApplicationContext(),sentiment,Toast.LENGTH_SHORT).show();
            emoji.playAnimation();
        } else if(sentiment.toLowerCase().equals("positive")){
            emoji.setAnimation(R.raw.happy_emoji);
            Toast.makeText(getActivity().getApplicationContext(),sentiment,Toast.LENGTH_SHORT).show();
            emoji.playAnimation();

        } else if(sentiment.toLowerCase().equals("negative")){
            emoji.setAnimation(R.raw.angry_emoji);
            Toast.makeText(getActivity().getApplicationContext(),sentiment,Toast.LENGTH_SHORT).show();
            emoji.playAnimation();
        } else {

        }
    }
    private String extractLastSentiment(String result) {
        if (result != null) {
            JsonObject resultObject = JsonParser.parseString(result).getAsJsonObject();
            if (resultObject.has("sentiment_analysis_results")) {
                JsonArray sentimentResults = resultObject.get("sentiment_analysis_results").getAsJsonArray();
                if (sentimentResults.size() > 0) {
                    return sentimentResults.get(sentimentResults.size() - 1).toString();
                } else {
                    Log.e(TAG, "No sentiment analysis results found");
                }
            } else {
                Log.e(TAG, "No sentiment analysis results found");
            }
        }
        return null;
    }

    private void togglePlayPause() {
        if (path != null) {
            if (!isPlaying) {
                if (isRecording) {
                    stopRecording();
                }
                startPlaying();
            } else {
                pausePlaying();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Please Record the Audio First", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPlaying() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            PlayPauseButton.playAnimation();
            PlayPauseButton.setRepeatCount(0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "startPlaying: failed to play media", e);
            Toast.makeText(getActivity(), "Failed to play audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            PlayPauseButton.cancelAnimation();
            PlayPauseButton.setProgress(0);
        }
    }

    private void pausePlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            PlayPauseButton.pauseAnimation();
        }
    }

    private void toggleRecording() {
        if (!isRecording) {
            isRecording = true;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        startRecording();
                    } catch (IOException e) {
                        Log.e(TAG, "toggleRecording: failed to start recording", e);
                    }
                }
            });
        } else {
            stopRecording();
        }
    }

    public void startRecording() throws IOException {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getRecordingFilePath());
        path = getRecordingFilePath();
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.prepare();
        mediaRecorder.start();

        requireActivity().runOnUiThread(() -> {
            recordingAnimation.playAnimation();
            Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
        });
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            isRecording = false;
            requireActivity().runOnUiThread(() -> {
                recordingAnimation.cancelAnimation();
                recordingAnimation.setProgress(0);
                Toast.makeText(getActivity().getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());
        File music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music, "testFile" + ".mp3");
        return file.getPath();
    }

    public void requestRecordingPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    public Boolean checkRecordingPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestRecordingPermission();
            Toast.makeText(getActivity().getApplicationContext(), "permission false", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(getActivity().getApplicationContext(), "permission true", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Done", Toast.LENGTH_SHORT).show();
                toggleRecording();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        executorService.shutdown();
        sentimentExecutor.shutdown();
    }
}
