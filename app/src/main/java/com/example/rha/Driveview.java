package com.example.rha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Thread.sleep;

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
    Map<String ,String > uidmap ;
int m=0;
    private DatabaseReference Driveref2,userref,memref1,use;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveview);
        memberlist = (RecyclerView) findViewById(R.id.memberslist) ;
        memberlist.setHasFixedSize(true);
        endbtn=findViewById(R.id.enddrive);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        Driveref2= FirebaseDatabase.getInstance().getReference().child("Drives").child(PostKey);
        mAuth=FirebaseAuth.getInstance();
        cancelbtn=findViewById(R.id.canceldrive);
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        memberlist.setLayoutManager(linearLayoutManager);

        Driveref2.addListenerForSingleValueEvent(new ValueEventListener() {
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
                   // Toast.makeText(Driveview.this,"Your Id is"+uid,Toast.LENGTH_SHORT).show();
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

   private void updatedrives( String hostid){

        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        memref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String x= ds.getKey();
                    Log.d("DriveView", "UserID inside getData: "+x);

                    //Toast.makeText(Driveview.this, "Your id" + x, Toast.LENGTH_SHORT).show();
                    userref= FirebaseDatabase.getInstance().getReference().child("User").child(x);
                    userref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int m =Integer.parseInt(dataSnapshot.child("drives").getValue().toString());
                            int n =Integer.parseInt(dataSnapshot.child("Smiles").getValue().toString());
                           m++;
                           n+=Integer.parseInt(smiles);
                            Log.d("DriveView", "UserID inside Drives: "+String.valueOf(m));
                            HashMap usermap = new HashMap();
                            usermap.put("Smiles",String.valueOf(n));
                            usermap.put("drives",String.valueOf(m));
                            final int finalM = m;
                            memref.child(x).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                 if(task.isSuccessful())
                                 {
                                     Intent abcintent = new Intent(Driveview.this,MainActivity.class);
                                     startActivity(abcintent);
                                 }

                                }
                            });
                            //memref.removeEventListener(this);
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
                Boolean Status=true;
                Driveref2.child("Status").setValue(Status);
                Driveref2.child("Smiles").setValue(smiles);

              updatedrives(hostid);

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


    private void updatedrives2(String hostid) {
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        memref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String x= ds.getKey();
                    final String y = ds.child("drives").getValue().toString();
                   final String z = ds.child("Smiles").getValue().toString();
                    HashMap usmap = new HashMap();
                    usmap.put("Smiles",z);
                    usmap.put("drives",y);
                    userref= FirebaseDatabase.getInstance().getReference().child("User").child(x);
                    userref.updateChildren(usmap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        memberadapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updatedrives2(hostid);
        memberadapter.stopListening();
    }


}
