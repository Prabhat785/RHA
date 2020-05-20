package com.example.rha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Mydrivehistory extends AppCompatActivity {
    private RecyclerView drivelist;
    private Drivehistoryadapter drivehistoryadapter;
    private List<Drivelist1> mList = new ArrayList<>();
    String currentuserid;
    Boolean Memchecker;
    DatabaseReference driveref,db1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydrivehistory);
        drivelist=(RecyclerView)findViewById(R.id.alluserpost1);
        drivelist.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();
        driveref = FirebaseDatabase.getInstance().getReference().child("Drives");
        currentuserid = mAuth.getCurrentUser().getUid();
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
         drivelist.setLayoutManager(linearLayoutManager);
         drivehistoryadapter = new Drivehistoryadapter(mList,this);
         driveref.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Drivelist1 dr = dataSnapshot.getValue(Drivelist1.class);
               drivehistoryadapter.notifyDataSetChanged();

           if(dr.getUid().equals(currentuserid)&&dr.getStatus1()=="true")
           {
               mList.add(dr);
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

        drivehistoryadapter.notifyDataSetChanged();
        drivelist.setAdapter(drivehistoryadapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
