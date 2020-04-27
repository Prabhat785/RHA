package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    Databasehelper db;
    FirebaseAuth fAuth;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;
    EditText mTextPhno;
    EditText mname;
    EditText mCode;
    String codesent;
    Button mCodeVerify;
    ProgressBar progressBar;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean vp =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db =new Databasehelper(this);
        fAuth = FirebaseAuth.getInstance();
        mTextUsername = (EditText) findViewById(R.id.username);
        mTextPassword = (EditText) findViewById(R.id.password);
        mTextCnfPassword = (EditText) findViewById(R.id.cnfrmpassword);
        mname =(EditText) findViewById(R.id.name);
        mButtonRegister = (Button) findViewById(R.id.registerButton);
        mTextViewLogin = (TextView) findViewById(R.id.login);
        mTextPhno = (EditText) findViewById(R.id.phno);
        mCode = (EditText) findViewById(R.id.code);
        codePicker =findViewById(R.id.ccp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCodeVerify = (Button) findViewById(R.id.codeBtn);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(Register.this, Login.class);
                startActivity(LoginIntent);
            }
        });
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String pwd= mTextPassword.getText().toString().trim();
                String name=mname.getText().toString().trim();
                String cnfpwd=mTextCnfPassword.getText().toString().trim();
                if(pwd.equals(cnfpwd))
                {
                   long val= db.addUser(user,pwd,name);
                   if(val>0) {
                       Toast.makeText(Register.this, "You are Registered Successfully", Toast.LENGTH_SHORT).show();
                       Intent movetoLogin =new Intent(Register.this,Login.class);
                       startActivity(movetoLogin);
                   }
                   else
                   {
                       Toast.makeText(Register.this,"Username already exists",Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(Register.this,"Password Doesn't Match",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCodeVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mTextPhno.getText().toString().trim();
                if(!vp) {
                    if (phoneNumber.length() < 10 && phoneNumber.isEmpty()) {
                        Toast.makeText(Register.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        phoneNumber = "+" + codePicker.getSelectedCountryCode() + phoneNumber;
                        progressBar.setVisibility(View.VISIBLE);
                        requestOTP(phoneNumber);


                    }
                }
                else
                {

                       String userotp =mCode.getText().toString();
                       if(!userotp.isEmpty()&&userotp.length()==6)
                       {
                        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(verificationId,userotp);
                       verifyAuth(credential);
                       }
                       else
                       {
                           mCode.setError("Invalid OTP");
                       }
                }

            }
        });
    }

    private void verifyAuth(PhoneAuthCredential credential)
    {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Authentication successfull", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Register.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        {

        }
    }

    private void requestOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                 super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                mCode.setVisibility(View.VISIBLE);
                 verificationId =s;
                 token=forceResendingToken;
                 mCodeVerify.setText("Verify");
                 vp =true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Register.this,"Error 404!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
