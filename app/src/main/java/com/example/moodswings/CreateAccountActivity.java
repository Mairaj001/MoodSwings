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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText email,pass,confirmPass;
    Button createAccountBtn;
    ProgressBar pgBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email=findViewById(R.id.email_edit_text);
        pass=findViewById(R.id.password_edit_text);
        confirmPass=findViewById(R.id.confirm_password_edit_text);
        pgBar=findViewById(R.id.progress_bar);
        createAccountBtn=findViewById(R.id.create_account_btn);

        TextView goToLogin=findViewById(R.id.login_text_view_btn_login); // navigate the user to the login screen

        goToLogin.setOnClickListener(item->{
            startActivity(new Intent(getApplicationContext(),ActivityLogin.class));
            finish();
        });

        createAccountBtn.setOnClickListener(v->{
            createAccount();
        });
    }

    public void createAccount(){
        String emails=email.getText().toString();
        String passcode=pass.getText().toString();
        String confrimPasscode=confirmPass.getText().toString();

        if(!validateData(emails,passcode,confrimPasscode)){
            return;
        }
        createAccountInFirebase(emails,passcode);
    }

    public void createAccountInFirebase(String emails,String passcode){
        handleProgressBar(true);

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emails,passcode).addOnCompleteListener(CreateAccountActivity.this
                , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleProgressBar(false);
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        } else {
                            Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void handleProgressBar(Boolean progress){
        if(progress){
            pgBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.INVISIBLE);
        } else {
            pgBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.GONE);
        }
    }

    boolean validateData(String emails,String password,String confirmPassword){


        if(!Patterns.EMAIL_ADDRESS.matcher(emails).matches()){
            email.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            pass.setError("Password length is invalid");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPass.setError("Password not matched");
            return false;
        }
        return true;
    }
}