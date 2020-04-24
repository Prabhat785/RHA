package com.example.rha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    Databasehelper db;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db =new Databasehelper(this);
        mTextUsername = (EditText) findViewById(R.id.username);
        mTextPassword = (EditText) findViewById(R.id.password);
        mTextCnfPassword = (EditText) findViewById(R.id.cnfrmpassword);
        mButtonRegister = (Button) findViewById(R.id.registerButton);
        mTextViewLogin = (TextView) findViewById(R.id.login);
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
                String cnfpwd=mTextCnfPassword.getText().toString().trim();
                if(pwd.equals(cnfpwd))
                {
                   long val= db.addUser(user,pwd);
                   if(val>0) {
                       Toast.makeText(Register.this, "You are Registered Successfully", Toast.LENGTH_SHORT).show();
                       Intent movetoLogin =new Intent(Register.this,Login.class);
                       startActivity(movetoLogin);
                   }
                   else
                   {
                       Toast.makeText(Register.this,"Username not Available",Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(Register.this,"Password Doesnt Match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}