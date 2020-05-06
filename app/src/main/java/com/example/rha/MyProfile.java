package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.connection.util.StringListReader;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MyProfile extends AppCompatActivity {
    private TextView textView;
    private Button button;
    String currentuserid;
    private TextView mPhoneText;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private TextView mEmailText;
    private TextView mLocationText;
    private TextView mSmilesText;
    private TextView mDrivesText;
    private TextView mchapterText;
    private CircularImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prf = getSharedPreferences("user_details",MODE_PRIVATE);
        setContentView(R.layout.activity_my_profile);
        textView=findViewById(R.id.u);
        mPhoneText =findViewById(R.id.phnumber);
        mEmailText=findViewById(R.id.email);
        mLocationText=findViewById(R.id.location);
        mDrivesText=findViewById(R.id.drives);
        mSmilesText=findViewById(R.id.smiles);
        button=findViewById(R.id.updatebtn);
        mchapterText=findViewById(R.id.chapter);
        profile = findViewById(R.id.profilepic);
        final String phone=mPhoneText.getText().toString();
        final String email=mEmailText.getText().toString();
        String location=mLocationText.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        Toast.makeText(MyProfile.this,currentuserid+"uid",Toast.LENGTH_SHORT).show();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        final boolean[] aBoolean = {false};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MyProfile.this,MyInfoUpdate.class);
                intent.putExtra("phone",phone);
                intent.putExtra("Email",email);
                startActivity(intent);
                finish();
            }
        });
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phoneno = dataSnapshot.child("Phoneno").getValue().toString();
                String email = dataSnapshot.child("Email").getValue().toString();
                String pic = dataSnapshot.child("Profile").getValue().toString();
                String location=dataSnapshot.child("Address").getValue().toString();
                String drives=dataSnapshot.child("drives").getValue().toString();
                String smiles=dataSnapshot.child("Smiles").getValue().toString();
                String chapter=dataSnapshot.child("Chapter").getValue().toString();
                //String location=dataSnapshot.child("Location").getValue().toString();

                mPhoneText.setText(phoneno);
                mEmailText.setText(email);
                mLocationText.setText(location);
                mDrivesText.setText(drives);
                mSmilesText.setText(smiles);
                mchapterText.setText(chapter);
                Picasso.get().load(pic).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
