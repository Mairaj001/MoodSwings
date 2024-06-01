package com.example.moodswings;


import android.content.ContextWrapper;
import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioFragment extends Fragment {
    LottieAnimationView recordingAnimation,PlayPauseButton;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private MediaPlayer mediaPlayer;
    private Boolean isPlaying=false;

    private boolean isRecording = false;

    public String path;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.audio_fragment, container, false);
        recordingAnimation = view.findViewById(R.id.animationView);
        PlayPauseButton= view.findViewById(R.id.animationViewPlaypause);
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

    private void togglePlayPause() {
        if(path!=null){
        if (!isPlaying) {
            if(isRecording){ stopRecording();}
            startPlaying();
        } else {
            pausePlaying();
        }
        } else {
            Toast.makeText(getActivity().getApplicationContext(),"Please Record the Audio First",Toast.LENGTH_SHORT).show();
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
            e.printStackTrace();
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
                        throw new RuntimeException(e);
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

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordingAnimation.playAnimation();
                Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            isRecording = false;
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordingAnimation.cancelAnimation();
                    recordingAnimation.setProgress(0);
                    Toast.makeText(getActivity().getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
                }
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
            Toast.makeText(getActivity().getApplicationContext(),"permissin false",Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(getActivity().getApplicationContext(),"permissin true",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start recording immediately if permission is granted
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
    }
}
