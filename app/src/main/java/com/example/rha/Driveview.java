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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

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
    private androidx.appcompat.widget.Toolbar toolbar;
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

       // cancelbtn=findViewById(R.id.canceldrive);
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
         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        Toast.makeText(Driveview.this,"Welcome",Toast.LENGTH_SHORT).show();
        getMenuInflater().inflate(R.menu.choice,menu2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.enddrive)
        {

        }
        if(item.getItemId()==R.id.cancelbtn)
        {

        }

        return super.onOptionsItemSelected(item);
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
    public void btn_dialog(MenuItem item) {
        final EditText mSmilesText;;
        final AlertDialog.Builder alert1= new AlertDialog.Builder(Driveview.this);
        final AlertDialog.Builder alert2= new AlertDialog.Builder(Driveview.this);

        final View view1=getLayoutInflater().inflate(R.layout.custom_dialog,null);
        final View view2=getLayoutInflater().inflate(R.layout.custom_dialog2,null);
        mSmilesText=(EditText)view1.findViewById(R.id.smiles);
        final Button mcancelbtn=view1.findViewById(R.id.cancelbtn);
        final Button mokbtn=view1.findViewById(R.id.Okbtn);
        final Button mokbtn2=view2.findViewById(R.id.Okbtn2);
        alert2.setView(view2);
        alert1.setView(view1);
        final AlertDialog alertDialog2 = alert2.create();
        alertDialog2.setCanceledOnTouchOutside(true);
        final AlertDialog alertDialog1 = alert1.create();
        alertDialog1.setCanceledOnTouchOutside(true);

        Driveref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Status").getValue().toString().equals("true") || dataSnapshot.child("Status").getValue().toString().equals("Cancelled"))
                {
                    alertDialog2.show();
                }
                else
                {
                    alertDialog1.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smiles=mSmilesText.getText().toString();
                HashMap hashMap=new HashMap();
                String Status="true";
                Driveref2.child("Status").setValue(Status);
                Driveref2.child("Smiles").setValue(smiles);

              updatedrives(hostid);
              Driveref2.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      String dlocation=dataSnapshot.child("drivelocation").getValue().toString();
                      try {
                          prepareNotifiaction("Congratulation","Congratulations on completion on drive","Smiles earned- "+smiles+" ","EndDrive"+dlocation,"EndDriveNotification");
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

            }
        });


        mcancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        mokbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog2.dismiss();
            }
        });

    }

    public void btn_dialog2(MenuItem item){
        final EditText mReasonText;;
        final AlertDialog.Builder alert1= new AlertDialog.Builder(Driveview.this);
        final AlertDialog.Builder alert2= new AlertDialog.Builder(Driveview.this);

        final View view=getLayoutInflater().inflate(R.layout.custom_dialog3,null);
        final View view1=getLayoutInflater().inflate(R.layout.custom_dialog4,null);
        mReasonText=(EditText)view.findViewById(R.id.reason);
        final Button mcancelbtn=view.findViewById(R.id.cancelbtn);
        final Button mcancelbtn2=view1.findViewById(R.id.Okbtn2);
        final Button mokbtn=view.findViewById(R.id.Okbtn);
        alert2.setView(view1);
        alert1.setView(view);
        final AlertDialog alertDialog2 = alert2.create();
        alertDialog2.setCanceledOnTouchOutside(true);
        final AlertDialog alertDialog1 = alert1.create();
        alertDialog1.setCanceledOnTouchOutside(true);
        Driveref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Status").getValue().toString().equals("Cancelled") || dataSnapshot.child("Status").getValue().equals("true" ))
                {

                    alertDialog2.show();
                }
                else
                {
                    alertDialog1.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String reason=mReasonText.getText().toString();
                HashMap hashMap=new HashMap();
                String Status="Cancelled";
                Driveref2.child("Status").setValue(Status);
                alertDialog1.dismiss();
                Driveref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String dlocation=dataSnapshot.child("drivelocation").getValue().toString();
                        try {
                            prepareNotifiaction("Drive","Drive has been cancelled ","Reason-"+reason+" ","EndDrive"+dlocation,"EndDriveNotification");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        mcancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        mcancelbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog2.dismiss();
            }
        });

    }
    private void updatedrives2(String hostid) {
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        memref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String x = ds.getKey();
                    if (ds.child("drives").exists() && ds.child("Smiles").exists()) {
                        final String y = ds.child("drives").getValue().toString();
                        final String z = ds.child("Smiles").getValue().toString();

                        HashMap usmap = new HashMap();
                        usmap.put("Smiles", z);
                        usmap.put("drives", y);
                        userref = FirebaseDatabase.getInstance().getReference().child("User").child(x);
                        userref.updateChildren(usmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private  void prepareNotifiaction(String pid,String Title,String Description,String notificationTopic,String notificationtype) throws JSONException {
        String NOTIFICATION_TOPIC = "/topics/"+notificationTopic;
        String NOTIFICATION_TITLE=Title;
        String NOTIFICATION_MESSAGE = Description;
        String NOTIFICATION_TYPE=notificationtype;

        JSONObject notification  = new JSONObject();
        JSONObject notificationbody  = new JSONObject();
        notificationbody.put("notificationType",NOTIFICATION_TYPE);
        notificationbody.put("Sender",pid);
        notificationbody.put("pTitle",NOTIFICATION_TITLE);
        notificationbody.put("pDescription",NOTIFICATION_MESSAGE);
        notification.put("to",NOTIFICATION_TOPIC);
        notification.put("data",notificationbody);

        sendpostNotification(notification);
    }
    private void sendpostNotification(JSONObject notification) {
        //Toast.makeText(StartDrive.this,"Notification prepared",Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(Driveview.this,""+response.toString(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof AuthFailureError) {
                    //Toast.makeText(getApplicationContext(),"Cannot connect to Internet...Please check your connection!",Toast.LENGTH_SHORT).show();
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                // headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAACE_4ois:APA91bFd9Pk7IcKSXZNqqHIHFa4HqdAvlrVovTjtmrSNmCpYm4L3aF6ZHq9rDrU_qPubcZnxxoD8fDNYNNDrtCRRUmdkRNyVQ3QiatgRKDeXGx-Xq-VAxQawzKvGa8XuRdfZZQ5979W_");
                return headers;
            }
        };
        Volley.newRequestQueue(Driveview.this).add(jsonObjectRequest);
    }
    public void onBackPressed(){
        Intent intent=new Intent(Driveview.this,MainActivity.class);
        startActivity(intent);
        finish();
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
