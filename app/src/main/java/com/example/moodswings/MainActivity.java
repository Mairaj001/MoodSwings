package com.example.moodswings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements AudioFragment.AudioFragmentListener {

    MeowBottomNavigation bnv;

    protected final int Audio=1;
    protected final int Playlist=2;
    protected final int userProfile=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bnv=findViewById(R.id.bnv);

        if (savedInstanceState == null) {
           loadFragment(new AudioFragment());
        }

        //adding icons to the custom Bottom navigation bar
        bnv.add( new MeowBottomNavigation.Model(Audio,R.drawable.audio_frag_new));
        bnv.add( new MeowBottomNavigation.Model(Playlist,R.drawable.playlist_frag));
        bnv.add( new MeowBottomNavigation.Model(userProfile,R.drawable.profle_frag));
        bnv.show(Audio,true);
        loadFragment(new AudioFragment());

        bnv.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
               Fragment selectedFragment = null;

                switch (model.getId()) {
                    case Audio:
                        selectedFragment = new AudioFragment();
                        break;
                    case Playlist:
                        selectedFragment = new PlaylistFragment();
                        break;
                    case userProfile:
                        selectedFragment = new UserProflieFragment();
                        break;
                }

                loadFragment(selectedFragment);
                return null;
            }
        });


        bnv.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                String name;

                switch (model.getId()){
                    case Audio:name="Record Audio"; break;
                    case Playlist:name="Playlists"; break;
                    case userProfile:name="User Profile"; break;
                }


                return null;
            }
        });

    }

    @Override
    public void onNetworkAvailable() {
        // Callback when network is available
        AudioFragment audioFragment = (AudioFragment) getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (audioFragment != null) {
            audioFragment.onNetworkAvailable();
        }
    }
    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}