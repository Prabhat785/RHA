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

    private DatabaseReference Driveref,userref,userref2;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveview);
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

        Driveref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Status").getValue().toString()=="true")
                    endbtn.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<Memberlist> options =
                new FirebaseRecyclerOptions.Builder<Memberlist>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey),Memberlist.class)
                        .build();
        memberadapter= new Memberadapter(options);
        memberlist.setAdapter(memberadapter);
    }


    private void updatesmiles(){
        memref=FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        memref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid=snapshot.getKey();
                    Toast.makeText(Driveview.this,"Your Id is"+uid,Toast.LENGTH_SHORT).show();
                    userref = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                    // userref2 = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Smiles");
                    userref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("Smiles")){
                                String smiles1=dataSnapshot.child("Smiles").getValue().toString();
                                int x =Integer.parseInt(smiles1)+Integer.parseInt(smiles);
                                HashMap hashMap=new HashMap();
                                hashMap.put("Smiles",String.valueOf(x));
                                userref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(Driveview.this, "Congratulations you have earned"+smiles+"smiles", Toast.LENGTH_SHORT).show();
                                            endbtn.setVisibility(View.INVISIBLE);
                                        }
                                        else
                                            Toast.makeText(Driveview.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

   private void updatedrives( String hostid1){

      Toast.makeText(Driveview.this,"Your Id is"+hostid1,Toast.LENGTH_SHORT).show();
     userref = FirebaseDatabase.getInstance().getReference().child("User").child(hostid);
       userref2 = FirebaseDatabase.getInstance().getReference().child("User").child(hostid).child("drives");
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("drives")){
                    String drives=dataSnapshot.child("drives").getValue().toString();
                    int x =Integer.parseInt(drives)+1;
                    userref2.setValue(String.valueOf(x)).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                 Toast.makeText(Driveview.this, "Congratulations on your have earned a smile", Toast.LENGTH_SHORT).show();
                                endbtn.setVisibility(View.INVISIBLE);

                            }
                            else
                                Toast.makeText(Driveview.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                HashMap hashMap=new HashMap();
                String Status="ended";
                hashMap.put("Status",Status);
                hashMap.put("Smiles",smiles);
                // Toast.makeText(Driveview.this,"Smiles"+smiles,Toast.LENGTH_SHORT).show();
                Driveref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        //Toast.makeText(Driveview.this,"Drive ended",Toast.LENGTH_SHORT).show();
                    }
                });
                Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("uid"))
                        {
                            hostid = dataSnapshot.child("uid").getValue().toString();
                            // Toast.makeText(Driveview.this, "hostid  " + hostid, Toast.LENGTH_SHORT).show();
                            updatedrives(hostid);
                            updatesmiles();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {



                    }
                });
                Intent intent = new Intent(Driveview.this,MainActivity.class);
                startActivity(intent);
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
