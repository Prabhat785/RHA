package com.example.rha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyInfoUpdate extends AppCompatActivity {
    private EditText mPhoneText;
    private EditText mEmailText;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_update);
        Bundle bundle=getIntent().getExtras();
        mPhoneText=findViewById(R.id.phno_update);
        mEmailText=findViewById(R.id.email_update);
        button=findViewById(R.id.btnupdate);
        mEmailText.requestFocus();
        mPhoneText.requestFocus();
        mPhoneText.setText(bundle.getString("phone"));
        mEmailText.setText(bundle.getString("Email"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movetoprofile=new Intent(MyInfoUpdate.this,MyProfile.class);
                String phone=mPhoneText.getText().toString();

                String email=mEmailText.getText().toString();
                movetoprofile.putExtra("phoneupdate",phone);
                movetoprofile.putExtra("emailupdate",email);
                startActivity(movetoprofile);
                finish();
            }
        });
    }
}
