package com.example.moodswings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread t = new Thread(){
            @Override
            public  void run()
            {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Intent
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    if(user==null){
                        startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                        finish();
                    } else {
                    startActivity( new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    }
                }
            }

        };
        t.start();
    }
}