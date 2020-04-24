package com.example.rha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    Databasehelper db;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db =new Databasehelper(this);
        mTextUsername = (EditText)findViewById(R.id.username);
        mTextPassword = (EditText)findViewById(R.id.password);
        mButtonLogin = (Button)findViewById(R.id.LoginButton);
        mTextViewRegister = (TextView)findViewById(R.id.Register);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
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
              String user = mTextUsername.getText().toString().trim();
              String pwd = mTextPassword.getText().toString().trim();
              Boolean res =db.checkUser(user,pwd);
              if(res==true)
              {
                  Toast.makeText(Login.this,"Successfully Loggedin",Toast.LENGTH_SHORT).show();
                  SharedPreferences.Editor editor = pref.edit();
                  editor.putString("username",user);
                  editor.putString("password",pwd);
                  editor.commit();
                  Intent main = new Intent(Login.this,MainActivity.class);

                  startActivity(main);
              }
              else
              {
                  Toast.makeText(Login.this,"Wrong Password",Toast.LENGTH_SHORT).show();
              }
          }
      });
    }
}
