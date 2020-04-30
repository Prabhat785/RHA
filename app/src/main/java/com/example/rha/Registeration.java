package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registeration extends AppCompatActivity {
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        UserEmail = (EditText) findViewById(R.id.username);
        UserPassword = (EditText) findViewById(R.id.password);
        UserConfirmPassword = (EditText) findViewById(R.id.cnfrmpassword);
        CreateAccountButton = (Button) findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = UserEmail.getText().toString();
                String password = UserPassword.getText().toString();
                String confirmPassword = UserConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Registeration.this, "Please write your email id ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(Registeration.this, "Please write your password...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(confirmPassword))
                {
                    Toast.makeText(Registeration.this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword))
                {
                    Toast.makeText(Registeration.this, "your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please Wait ");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Registeration.this, "your are authentication sucessfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(Registeration.this,Register.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        String message =task.getException().getMessage();
                                        Toast.makeText(Registeration.this, "Error Occured"+message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                }
            }
        });
    }

}
