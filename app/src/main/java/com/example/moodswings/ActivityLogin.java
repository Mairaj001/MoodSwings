package com.example.moodswings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ActivityLogin extends AppCompatActivity {

     EditText email,password;
     ProgressBar pgBars;
     Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView goTocreate=findViewById(R.id.create_account_text_view_btn);

        goTocreate.setOnClickListener(item->{
            startActivity(new Intent(getApplicationContext(),CreateAccountActivity.class));
            finish();
        });

        email=findViewById(R.id.email_edit_text);
        password=findViewById(R.id.password_edit_text);
        loginBtn=findViewById(R.id.login_btn);
        pgBars=findViewById(R.id.progress_bar);

        loginBtn.setOnClickListener(item->{
            loginUser();
        });

    }

    public void  loginUser(){
        String Email=email.getText().toString();
        String pass=password.getText().toString();

        if(!validateData(Email,pass)){ return;}

        loginuserByFirebaseAuth(Email,pass);
    }

    public void loginuserByFirebaseAuth(String emails, String pass){

        changeInProgress(true);
        // Creating the Auth Instance
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(emails,pass).addOnCompleteListener(ActivityLogin.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ActivityLogin.this,"Email not verified",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ActivityLogin.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            pgBars.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            pgBars.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String emails,String passwords){


        if(!Patterns.EMAIL_ADDRESS.matcher(emails).matches()){
            email.setError("Email is invalid");
            return false;
        }
        if(passwords.length()<6){
            password.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}