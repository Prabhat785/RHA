package com.example.rha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Driveactivity extends AppCompatActivity {
    private   Drivesadapter drivesadapter;
    private List<Drivelist> mList1 = new ArrayList<>();
    private FirebaseAuth mAuth;
    private RecyclerView drivelist;
    private TextView mchapter;
    private  String currentuserid;
    private DatabaseReference Driveref,useref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveactivity);
        drivelist=(RecyclerView)findViewById(R.id.drvlist);
        drivelist.setHasFixedSize(true);
        mAuth =FirebaseAuth.getInstance();
        mchapter=findViewById(R.id.cp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        drivelist.setLayoutManager(linearLayoutManager);
        Driveref = FirebaseDatabase.getInstance().getReference().child("Drives");
        currentuserid=mAuth.getCurrentUser().getUid();
        useref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);

        drivesadapter = new Drivesadapter(mList1,this,currentuserid);
        useref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Chapter").exists())
                {
                    mchapter.setText(dataSnapshot.child("Chapter").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Driveref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String chap =  mchapter.getText().toString();
                Drivelist dr =dataSnapshot.getValue(Drivelist.class);
                drivesadapter.notifyDataSetChanged();
                Log.d("Main", "UserID inside Drives123: "+chap);
                if(dr.getChapter1().equals(chap))
                {
                    mList1.add(dr);
                    Log.d("Main", "UserID inside Drives12: "+chap+" "+dr.getChapter1());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        drivesadapter.notifyDataSetChanged();
        drivelist.setAdapter(drivesadapter);
    }

}
