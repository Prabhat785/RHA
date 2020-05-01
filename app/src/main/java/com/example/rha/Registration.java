package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private  EditText mTextPhno;
    private EditText mname;
    private  EditText mUsername;
    private CountryCodePicker codePicker;
    private Button mlocationbtn;
    private Button mButtonRegister;
    String currentuserid;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mTextPhno=findViewById(R.id.phno);
        mname=findViewById(R.id.name);
        mUsername=findViewById(R.id.username);
        mlocationbtn=findViewById(R.id.location);
        mButtonRegister=findViewById(R.id.registerButton);
        codePicker =findViewById(R.id.ccp);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        Bundle bundle=getIntent().getExtras();
        final String email=bundle.getString("email");
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);

        loadingbar = new ProgressDialog(this);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mname.getText().toString();
                final String user = mUsername.getText().toString();
                String phone=mTextPhno.getText().toString();
                //phone = "+" + codePicker.getSelectedCountryCode() + phone;
                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(Registration.this, "Please write your Name ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(user))
                {
                    Toast.makeText(Registration.this, "Please write your Userid...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(Registration.this, "Please confirm your Phoneno....", Toast.LENGTH_SHORT).show();
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
                    usermap.put("Phoneno",phone);
                    usermap.put("Email",email);
                    userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Registration.this, "Registration Completed Sucessfully", Toast.LENGTH_SHORT).show();
                                // mAuth.signOut();
                                Intent movetomain = new Intent(Registration.this,MainActivity.class);
                                movetomain.putExtra("Email",email);
                                startActivity(movetomain);
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Registration.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });


                }
            }
        });
    }
}
