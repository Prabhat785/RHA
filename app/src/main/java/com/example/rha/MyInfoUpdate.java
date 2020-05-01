package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MyInfoUpdate extends AppCompatActivity {
    private EditText mPhoneText;
    private EditText mEmailText;
    private EditText mLocationText;
    private Button button;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private String currentuserid;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_update);
        Bundle bundle=getIntent().getExtras();
        mPhoneText=findViewById(R.id.phno_update);
        mEmailText=findViewById(R.id.email_update);
        mLocationText=findViewById(R.id.location_update);
        button=findViewById(R.id.btnupdate);
        mEmailText.requestFocus();
        mPhoneText.requestFocus();
        mPhoneText.requestFocus();
        mPhoneText.setText(bundle.getString("phone"));
        mEmailText.setText(bundle.getString("Email"));
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        loadingbar = new ProgressDialog(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent movetoprofile=new Intent(MyInfoUpdate.this,MyProfile.class);
                final String phone=mPhoneText.getText().toString();
                final String email=mEmailText.getText().toString();
                final String location=mLocationText.getText().toString();
                HashMap usermap = new HashMap();
                usermap.put("Email",email);
                usermap.put("Phoneno",phone);
                usermap.put("Location",location);
                userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MyInfoUpdate.this, "Profile updated ", Toast.LENGTH_SHORT).show();
                            // mAuth.signOut();
                            Intent movetoprofile= new Intent(MyInfoUpdate.this,MyProfile.class);
                            movetoprofile.putExtra("phoneupdate",phone);
                            movetoprofile.putExtra("emailupdate",email);
                            startActivity(movetoprofile);
                            finish();
                            loadingbar.dismiss();
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(MyInfoUpdate.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });

            }
        });
    }
}
