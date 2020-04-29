package com.example.rha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfile extends AppCompatActivity {
    private TextView textView;
    private Button button;
    private TextView mPhoneText;
    private TextView mEmailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prf = getSharedPreferences("user_details",MODE_PRIVATE);
        setContentView(R.layout.activity_my_profile);
        textView=findViewById(R.id.u);
        mPhoneText =findViewById(R.id.phnumber);
        mEmailText=findViewById(R.id.email);
        button=findViewById(R.id.updatebtn);
        final String phone=mPhoneText.getText().toString();
        final String email=mEmailText.getText().toString();
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
        textView.setText(prf.getString("username",null));
        if(aBoolean[0]){
        Bundle bundle=getIntent().getExtras();
        String phoneupdate=bundle.getString("phoneupdate");
        Toast.makeText(MyProfile.this,phoneupdate+"phone",Toast.LENGTH_SHORT).show();
        String emailupdate=bundle.getString("emailupdate");
        mPhoneText.setText(phoneupdate);
        mEmailText.setText(emailupdate);}
    }
}
