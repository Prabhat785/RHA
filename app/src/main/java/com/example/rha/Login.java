package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    private FirebaseAuth mAuth;
    Databasehelper db;
    private ProgressDialog loadingBar;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db =new Databasehelper(this);
        mTextUsername = (EditText)findViewById(R.id.email);
        mTextPassword = (EditText)findViewById(R.id.password);
        mButtonLogin = (Button)findViewById(R.id.LoginButton);
        mAuth=FirebaseAuth.getInstance();
        mTextViewRegister = (TextView)findViewById(R.id.Register);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        loadingBar=new ProgressDialog(this);
       Intent intent = new Intent(Login.this,MainActivity.class);
        if(pref.contains("username") && pref.contains("password")){
            startActivity(intent);
        }
      mTextViewRegister.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent newregisterintent = new Intent(Login.this,Register.class);
              startActivity(newregisterintent);
          }
      });
      mButtonLogin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              final String user = mTextUsername.getText().toString().trim();
              String pwd = mTextPassword.getText().toString().trim();
              if (TextUtils.isEmpty(user)) {
                  Toast.makeText(Login.this, "Please write your Email id...", Toast.LENGTH_SHORT).show();
              } else if (TextUtils.isEmpty(pwd)) {
                  Toast.makeText(Login.this, "Please write your password...", Toast.LENGTH_SHORT).show();
              } else {
                  loadingBar.setTitle("Login in : ");
                  loadingBar.setMessage("Please Wait ");
                  loadingBar.show();
                  loadingBar.setCanceledOnTouchOutside(true);

                  mAuth.signInWithEmailAndPassword(user, pwd)
                          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task) {
                                  if (task.isSuccessful()) {
                                      Intent mainIntent = new Intent(Login.this, MainActivity.class);
                                      startActivity(mainIntent);
                                      Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
                                      Intent movetomain = new Intent(Login.this,MainActivity.class);
                                      movetomain.putExtra("Email",user);
                                      startActivity(movetomain);
                                      loadingBar.dismiss();
                                  } else {
                                      String message = task.getException().getMessage();
                                      Toast.makeText(Login.this, "Error Occured " + message, Toast.LENGTH_SHORT).show();
                                  }
                              }
                          });

              }
          }
      });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Intent mainIntent = new Intent( Login.this,MainActivity.class);
            startActivity(mainIntent);
        }
    }
    }