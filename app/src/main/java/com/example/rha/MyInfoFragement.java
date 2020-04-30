package com.example.rha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyInfoFragement extends Fragment {
    Databasehelper db;
    TextView mTextUsername,mname,madress,mpincode,mphone;
    String currentuserid;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.my_info,container,false);

        mTextUsername =  v.findViewById(R.id.username);
        mname = v.findViewById(R.id.name);
        madress= v.findViewById(R.id.adress);
        mpincode =v.findViewById(R.id.pin);
        mphone=v.findViewById(R.id.phno);
        mAuth = FirebaseAuth.getInstance();
        currentuserid= mAuth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("Name").getValue().toString();
                String username=dataSnapshot.child("Username").getValue().toString();
                String adress=dataSnapshot.child("Adress").getValue().toString();
                String pincode=dataSnapshot.child("Pin").getValue().toString();
                String phoneno = dataSnapshot.child("Phoneno").getValue().toString();
               mname.setText("Name :"+name);
                mTextUsername.setText("Username :"+username);
                madress.setText("Adress :"+adress);
                mpincode.setText("Pin :"+pincode);
                mphone.setText("Phoneno :"+phoneno);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }
}
