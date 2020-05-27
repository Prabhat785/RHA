package com.example.rha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prf;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView textView,mchapter,mdrive;
    private NavigationView navigationView;
    private DatabaseReference userref,Driveref,Memref,tokenref,userref2,postref;
    private RecyclerView drivelist;
    private RecyclerView postlist;
    private CircularImageView profile;
    String currentuserid;
    Boolean Memchecker;

    private FirebaseAuth mAuth;
    private List<Drivelist> mList1 = new ArrayList<>();
    private List<Postlist> mList2 = new ArrayList<>();
    private  Boolean mf = false;
    private   Drivesadapter drivesadapter;
    private  Postadapter postadapter;
    private  Button Drive;
    String PostKey;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        mchapter = findViewById(R.id.chap);
        drivelist=(RecyclerView)findViewById(R.id.alluserpost);
        drivelist.setHasFixedSize(true);
        mdrive = findViewById(R.id.drive1);
        currentuserid = mAuth.getCurrentUser().getUid();
        Driveref= FirebaseDatabase.getInstance().getReference().child("Drives");
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        Memref = FirebaseDatabase.getInstance().getReference().child("Members");
        postref=FirebaseDatabase.getInstance().getReference().child("Post");
        userref2 = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                final String chapter=dataSnapshot1.child("Chapter").getValue().toString();
                tokenref=FirebaseDatabase.getInstance().getReference().child("Tokens");
                tokenref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        String TOPIC_TO_SUBSCRIBE="Drive" + chapter;;
                        if(dataSnapshot2.hasChild(chapter)){
                            subscribetonotification(TOPIC_TO_SUBSCRIBE);
                        if (!dataSnapshot2.child(chapter).hasChild(currentuserid)) {
                            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (task.isSuccessful()) {
                                        tokenref.child(chapter).child(currentuserid).setValue(task.getResult().getToken());
                                        Toast.makeText(MainActivity.this, "token " + task.getResult().getToken(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            subscribetonotification(TOPIC_TO_SUBSCRIBE);
                        }

                    }
                        else
                        {
                            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (task.isSuccessful()) {
                                        tokenref.child(chapter).child(currentuserid).setValue(task.getResult().getToken());
                                        Toast.makeText(MainActivity.this, "token " + task.getResult().getToken(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            subscribetonotification(TOPIC_TO_SUBSCRIBE);
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.opennavigationdrawer,
                R.string.closennavigationdrawer
                );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        textView=header.findViewById(R.id.user_text);
        profile=header.findViewById(R.id.imageView);
        Drive = header.findViewById(R.id.drives);
        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Username"))
                {
                    String username = dataSnapshot.child("Username").getValue().toString();

                    textView.setText(username);

                }
                if(dataSnapshot.hasChild("Profile"))
                {
                    String profilepic = dataSnapshot.child("Profile").getValue().toString();
                    Picasso.get().load(profilepic).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Drive = new Intent(MainActivity.this , StartDrive.class);
                startActivity(Drive);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
       drivelist.setLayoutManager(linearLayoutManager);
       userref.child(currentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.child("Chapter").exists()) {
                   String chapter = dataSnapshot.child("Chapter").getValue().toString();
                 //  Log.d("DriveView", "UserID inside getData: " + chapter);
                   mchapter.setText(chapter);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        drivesadapter = new Drivesadapter(mList1,this,currentuserid);
        postadapter=new Postadapter(mList2,this,currentuserid);

        postref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Postlist pr = dataSnapshot.getValue(Postlist.class);
                final String chap =  mchapter.getText().toString();
                postadapter.notifyDataSetChanged();
               if(pr.getChapter1().equals("ALL")||pr.getChapter1().equals(chap)) {
                   mList2.add(pr);
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
      /* Driveref.addChildEventListener(new ChildEventListener() {
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
       });/*
       Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });*/

       //drivesadapter.notifyDataSetChanged();
        postadapter.notifyDataSetChanged();
        drivelist.setAdapter(postadapter);
        mdrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driveintent = new Intent(MainActivity.this,Driveactivity.class);
                startActivity(driveintent);
            }
        });
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_post:
                Intent postact =new Intent(MainActivity.this,Post_Activity.class);
                startActivity(postact);
                break;
            case R.id.my_info:
                Intent myprofile =new Intent(MainActivity.this,MyProfile.class);
                startActivity(myprofile);
                break;
            case R.id.drives_history:
                //Toast.makeText(MainActivity.this,"This activity is under development",Toast.LENGTH_SHORT).show();
                Intent mydrives = new Intent(MainActivity.this,Mydrivehistory.class);
                startActivity(mydrives);
                break;
            case R.id.about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,new AboutUsFragement()).commit();
                break;
            case R.id.log:
                logout();


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logout(){
        Intent mintent = new Intent(MainActivity.this,Login.class);
        mAuth.signOut();
        startActivity(mintent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            Intent login = new Intent(MainActivity.this,Login.class);
            startActivity(login);
        }
        else
        {
            CheckUserExistence();
        }
       // drivesadapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // drivesadapter.stopListening();
    }

    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();
      //  Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(current_user_id))
                {
                 //   Toast.makeText(MainActivity.this,"Welcome to Robbinhood Army",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this,"Enter Your Data",Toast.LENGTH_SHORT).show();
                    Intent Registration = new Intent(MainActivity.this,Registration.class);
                    startActivity(Registration);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Error ",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void subscribetonotification(String TOPIC_TO_SUBSCRIBE){
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TO_SUBSCRIBE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
}
