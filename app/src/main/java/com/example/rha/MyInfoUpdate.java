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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

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
            private CircularImageView profile;
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
        profile = (CircularImageView)findViewById(R.id.profilepic);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        loadingbar = new ProgressDialog(this);
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              String phoneno = dataSnapshot.child("Phoneno").getValue().toString();
                                              String email = dataSnapshot.child("Email").getValue().toString();
                                              String pic = dataSnapshot.child("Profile").getValue().toString();
                                              //String location=dataSnapshot.child("Location").getValue().toString();
                                              if (phoneno.isEmpty())
                                                  mPhoneText.setText("Edit profile to update phone no");
                                              if (email.isEmpty())
                                                  mEmailText.setText("Edit profile to update email");
                                              mPhoneText.setText(phoneno);
                                              mEmailText.setText(email);
                                              Picasso.get().load(pic).into(profile);
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });


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
