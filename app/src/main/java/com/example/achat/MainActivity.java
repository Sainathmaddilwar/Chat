package com.example.achat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText phoneNo,Code;
    Button Verify;
    String VerificationCode;
    FirebaseAuth mauth;
   private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        CheackUserIsloggedIn();
        phoneNo=(EditText)findViewById(R.id.phoneNo);
        Code=(EditText)findViewById(R.id.Code);
        Verify=(Button)findViewById(R.id.button);
        mauth=FirebaseAuth.getInstance();
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartVerification();
            }
        });
        mcallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                SignInwithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                VerificationCode=s;
                Verify.setText("verify Code");
                VerifyPhoneWithCode();
            }
        };
    }

    private void VerifyPhoneWithCode(){
        PhoneAuthCredential Credintial=PhoneAuthProvider.getCredential(VerificationCode,Code.getText().toString());
        SignInwithPhone(Credintial);
    }
    private void SignInwithPhone(PhoneAuthCredential phoneAuthCredential) {
       mauth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    CheackUserIsloggedIn();
                }
            }
        });
    }

    private void CheackUserIsloggedIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            startActivity(new Intent(MainActivity.this, MainPage.class));
           finish();
           return;
        }
    }

    private void StartVerification() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mcallbacks);

    }
}