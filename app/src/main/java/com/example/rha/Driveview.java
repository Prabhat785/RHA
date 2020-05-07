package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class Driveview extends AppCompatActivity {

    private RecyclerView memberlist;
    private DatabaseReference memref;
    private Button endbtn;
    private Button cancelbtn;
    private FirebaseAuth mAuth;
    String PostKey;
    String smiles;
    public static String hostid;
    Memberadapter memberadapter;
    private String currentuserid;

    private DatabaseReference Driveref,userref,userref2,memref2;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveview);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        memberlist = (RecyclerView) findViewById(R.id.memberslist) ;
        memberlist.setHasFixedSize(true);
        endbtn=findViewById(R.id.enddrive);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        Driveref= FirebaseDatabase.getInstance().getReference().child("Drives").child(PostKey);
        mAuth=FirebaseAuth.getInstance();
        cancelbtn=findViewById(R.id.canceldrive);
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        memberlist.setLayoutManager(linearLayoutManager);


        FirebaseRecyclerOptions<Memberlist> options =
                new FirebaseRecyclerOptions.Builder<Memberlist>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey),Memberlist.class)
                        .build();
        memberadapter= new Memberadapter(options);
        memberlist.setAdapter(memberadapter);
        Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Status")){
                    update();
                    endbtn.setVisibility(View.INVISIBLE);}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void update(){
        Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {
                if (dataSnapshot1.hasChild("Status")) {
                    memref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot Snapshot) {
                            if (Snapshot.hasChild(currentuserid)&& !Snapshot.child(currentuserid).hasChild("Flag")) {

                                memref2 = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey).child(currentuserid);
                                HashMap hashMap=new HashMap();
                                hashMap.put("Flag",1);
                                memref2.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        Toast.makeText(Driveview.this,"Flag set ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
                                userref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        String smiles1 = dataSnapshot2.child("Smiles").getValue().toString();
                                        String drives=dataSnapshot2.child("drives").getValue().toString();
                                        int x;
                                        if(dataSnapshot1.hasChild("Smiles")) {
                                            String smiles=dataSnapshot1.child("Smiles").getValue().toString();
                                            x = Integer.parseInt(smiles1) + Integer.parseInt(smiles);
                                        }
                                        else {
                                            x = Integer.parseInt(smiles1) + Integer.parseInt(smiles);
                                        }
                                        int y=Integer.parseInt(drives)+1;
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("Smiles", String.valueOf(x));
                                        hashMap.put("drives", String.valueOf(y));
                                        userref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(Driveview.this, "Congratulations you have earned"+smiles+"smiles", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(Driveview.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });
    }
    public void btn_dialog(View view) {
        final EditText mSmilesText;;
        final AlertDialog.Builder alert= new AlertDialog.Builder(Driveview.this);
        View view1=getLayoutInflater().inflate(R.layout.custom_dialog,null);
        mSmilesText=(EditText)view1.findViewById(R.id.smiles);
        final Button mcancelbtn=view1.findViewById(R.id.cancelbtn);
        final Button mokbtn=view1.findViewById(R.id.Okbtn);
        alert.setView(view1);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        mokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smiles=mSmilesText.getText().toString();
                boolean status=true;
                Driveref.child("Status").setValue(status);
                Driveref.child("Smiles").setValue(smiles);
                update();
                alertDialog.dismiss();

            }
        });
        mcancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        memberadapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        memberadapter.stopListening();
    }


}
