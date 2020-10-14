package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    Databasehelper db;
    FirebaseAuth fAuth;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
     private Boolean emailaddresschecker;

    private FirebaseAuth mAuth;
    String currentuserid;
    private DatabaseReference userref;
    private EditText mEmailText;
    ProgressDialog loadingbar;
    ProgressBar progressBar;
    //CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean vp =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db =new Databasehelper(this);
        fAuth = FirebaseAuth.getInstance();
        mTextPassword = (EditText) findViewById(R.id.password);
        mTextCnfPassword = (EditText) findViewById(R.id.cnfrmpassword);
        mEmailText=findViewById(R.id.email);
        mButtonRegister = (Button) findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        loadingbar=new ProgressDialog(this);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pwd = mTextPassword.getText().toString().trim();
                String email = mEmailText.getText().toString().trim();
                String cnfpwd = mTextCnfPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Register.this, "Please write your email id ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(pwd))
                {
                    Toast.makeText(Register.this, "Please write your password...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(cnfpwd))
                {
                    Toast.makeText(Register.this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
                }
                else if(!pwd.equals(cnfpwd))
                {
                    Toast.makeText(Register.this, "your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Creating New Account");
                    loadingbar.setMessage("Please Wait ");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);
                    mAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "your are authentication sucessfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        SendEmailVerifiaction();
                                    }
                                    else
                                    {
                                        String message =task.getException().getMessage();
                                        Toast.makeText(Register.this, "Error Occured"+message, Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                }
                            });

                }

            }
        });

        }

        private  void SendEmailVerifiaction()
        {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user!=null)
            {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful())
                     {
                         mAuth.signOut();
                         Toast.makeText(Register.this,"Your Registration is Sucessfull Please Verify Your Email id...",Toast.LENGTH_SHORT).show();
                         Intent loginintent = new Intent(Register.this,Login.class);
                         startActivity(loginintent);
                     }
                     else
                     {
                         String  meaasge = task.getException().getMessage().toString();
                         Toast.makeText(Register.this,"Error !"+meaasge,Toast.LENGTH_SHORT).show();
                     }
                    }
                });
            }
        }
    }

