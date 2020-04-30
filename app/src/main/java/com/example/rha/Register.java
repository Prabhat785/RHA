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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    Databasehelper db;
    FirebaseAuth fAuth;
    EditText mTextUsername,mname,madress,mpincode,mphone;
    Button mButtonRegister;
    TextView mTextViewLogin;
    Button mCodeVerify;
    ProgressBar progressBar;
    CountryCodePicker codePicker;
    String currentuserid;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db =new Databasehelper(this);
        fAuth = FirebaseAuth.getInstance();
        mTextUsername = (EditText) findViewById(R.id.username);
         madress= (EditText) findViewById(R.id.adress);
        mname = (EditText) findViewById(R.id.name);
        mpincode =(EditText) findViewById(R.id.pincode);
        mButtonRegister = (Button) findViewById(R.id.registerButton);
        mTextViewLogin = (TextView) findViewById(R.id.login);
        mphone = findViewById(R.id.phno);
        codePicker =findViewById(R.id.ccp);
         mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
         userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);

        loadingbar = new ProgressDialog(this);
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
                String name=mname.getText().toString();
                String user = mTextUsername.getText().toString();
                String add= madress.getText().toString();
                String pin=mpincode.getText().toString();
                String phone=mphone.getText().toString();
                phone = "+" + codePicker.getSelectedCountryCode() + phone;
                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(Register.this, "Please write your Name ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(user))
                {
                    Toast.makeText(Register.this, "Please write your Userid...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(add))
                {
                    Toast.makeText(Register.this, "Please confirm your Adress...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(pin))
                {
                    Toast.makeText(Register.this, "Please confirm your Pincode...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(Register.this, "Please confirm your Phoneno....", Toast.LENGTH_SHORT).show();
                }

                else
                    {
                        loadingbar.setTitle("Saving User data");
                        loadingbar.setMessage("Please Wait ");
                        loadingbar.show();
                        loadingbar.setCanceledOnTouchOutside(true);
                        HashMap usermap = new HashMap();
                        usermap.put("Name",name);
                        usermap.put("Username",user);
                        usermap.put("Adress",add);
                        usermap.put("Pin",pin);
                        usermap.put("Phoneno",phone);
                        userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                              if(task.isSuccessful())
                              {
                                  Toast.makeText(Register.this, "Registration Completed Sucessfully", Toast.LENGTH_SHORT).show();
                                 // mAuth.signOut();
                                  Intent movetologin = new Intent(Register.this,MainActivity.class);
                                  startActivity(movetologin);
                                  loadingbar.dismiss();
                              }
                              else
                              {
                                  String message = task.getException().getMessage();
                                  Toast.makeText(Register.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                                  loadingbar.dismiss();
                              }
                            }
                        });


                }
            }
        });

    }




}
