package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.jar.Attributes;

//import afu.org.checkerframework.checker.igj.qual.I;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prf;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView textView;
    private NavigationView navigationView;
    private DatabaseReference userref,Driveref,Memref;
    private RecyclerView drivelist;
    private CircularImageView profile;
    String currentuserid;
    Boolean Memchecker;
    private FirebaseAuth mAuth;
    private  Boolean mf = false;

    private  Button Drive;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        drivelist=(RecyclerView)findViewById(R.id.alluserpost);
        drivelist.setHasFixedSize(true);
        currentuserid = mAuth.getCurrentUser().getUid();
        Driveref= FirebaseDatabase.getInstance().getReference().child("Drives");
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        Memref = FirebaseDatabase.getInstance().getReference().child("Members");
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
      DisplayAlldrives();

    }

    private void DisplayAlldrives()
    {
        FirebaseRecyclerAdapter<Drivelist,DriveViewHolder> firebaseRecyclerAdapter =
        new FirebaseRecyclerAdapter<Drivelist, DriveViewHolder>
                (
                        Drivelist.class,
                        R.layout.all_driveslayout,
                        DriveViewHolder.class,
                        Driveref
                ) {
            @Override
            protected void populateViewHolder(DriveViewHolder driveViewHolder, Drivelist drivelist, int i) {
                final String Postkey  = getRef(i).getKey();

                driveViewHolder.setProfilepic(getApplicationContext(),drivelist.getProfilepic());
                driveViewHolder.setDate(drivelist.getDate());
                driveViewHolder.setUsername1(drivelist.getUsername1());
                driveViewHolder.setTime(drivelist.getTime());
                driveViewHolder.setDrivelocation(drivelist.getDrivelocation());
                driveViewHolder.setPicuplocation(drivelist.getPicuplocation());
                driveViewHolder.setSponsor(drivelist.getSponsor());
                driveViewHolder.setNoofmemeber1(drivelist.getNoofmemeber1());
                driveViewHolder.setButton(Postkey);
                 driveViewHolder.setabc(Postkey);
                driveViewHolder.setdrivestatus(Postkey);
                 driveViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Intent driveintent = new Intent(MainActivity.this,Driveview.class);
                         driveintent.putExtra("Postkey",Postkey);
                         startActivity(driveintent);
                     }
                 });

                driveViewHolder.Joindrive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                         Memchecker = true;
                         Memref.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(Memchecker.equals(true))
                               {
                                   if(dataSnapshot.child(Postkey).hasChild(currentuserid))
                                   {
                                       Memref.child(Postkey).child(currentuserid).removeValue();
                                       Memchecker =false;
                                   }
                                   else
                                   {
                                       userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               String name = dataSnapshot.child("Name").getValue().toString();
                                               String phno = dataSnapshot.child("Phoneno").getValue().toString();
                                               String add = dataSnapshot.child("Address").getValue().toString();
                                               String username =dataSnapshot.child("Username").getValue().toString();
                                               String profilepic =dataSnapshot.child("Profile").getValue().toString();
                                               HashMap memberinfo = new HashMap();
                                               memberinfo.put("Name",name);
                                               memberinfo.put("Phoneno",phno);
                                               memberinfo.put("Address",add);
                                               memberinfo.put("Username",username);
                                               memberinfo.put("Profile",profilepic);
                                               Memref.child(Postkey).child(currentuserid).setValue(memberinfo);
                                               Memchecker = false;
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });

                                   }
                               }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                         });
                    }
                });
            }
        };
        drivelist.setAdapter(firebaseRecyclerAdapter);
    }
     public static class  DriveViewHolder extends RecyclerView.ViewHolder
     {
         View mview;
          Button Joindrive;
          TextView Memrequied,Drivestats,Drivestats2;
          int coutmem;
          String currentUserId;
          DatabaseReference Memref;
          String x;
         private DatabaseReference Driveref;

         public DriveViewHolder(@NonNull View itemView) {
             super(itemView);
             mview = itemView;
             Joindrive = (Button) mview.findViewById(R.id.join);
             Memrequied =(TextView ) mview.findViewById(R.id.memreq);
             Drivestats = (TextView) mview.findViewById(R.id.drivestatus);
             Drivestats2=mview.findViewById(R.id.drivestatus2);
             Memref = FirebaseDatabase.getInstance().getReference().child("Members");
             Driveref=FirebaseDatabase.getInstance().getReference().child("Drives");
             currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
         }

         public void setButton(final String PostKey)
         {
             Memref.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if(dataSnapshot.child(PostKey).hasChild(currentUserId))
                     {
                         coutmem =(int )dataSnapshot.child(PostKey).getChildrenCount();
                         Joindrive.setText("Joined");
                         Memrequied.setText(Integer.toString(coutmem)+" Members Joined");


                     }
                     else if (!dataSnapshot.child(PostKey).hasChild(currentUserId))
                     {
                         coutmem =(int )dataSnapshot.child(PostKey).getChildrenCount();
                         Joindrive.setText("Join");
                         Memrequied.setText(Integer.toString(coutmem)+" Members Joined");
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
         }
         public void setabc( final String PostKey)
         {

             Memref.addValueEventListener(new ValueEventListener() {
                 public DatabaseReference userref;

                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                         coutmem = (int) dataSnapshot.child(PostKey).getChildrenCount();
                         if(coutmem>=Integer.parseInt(x))
                         {
                             Drivestats.setVisibility(View.VISIBLE);
                             Joindrive.setVisibility(View.INVISIBLE);

                         }

                     } else if (!dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                         coutmem = (int) dataSnapshot.child(PostKey).getChildrenCount();
                         if(coutmem<Integer.parseInt(x))
                         {
                             Drivestats.setVisibility(View.INVISIBLE);
                             Joindrive.setVisibility(View.VISIBLE);

                         }
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
         }


         public void setUsername1(String Username) {
             TextView username = (TextView) mview.findViewById(R.id.user_name);
             username.setText(Username);
         }
         public void setDate(String date) {
             TextView date1= (TextView) mview.findViewById(R.id.drive_date);
             date1.setText(" "+date+" ");
         }
         public void setProfilepic(Context applicationContext, String profilepic) {
             CircularImageView image = (CircularImageView) mview.findViewById(R.id.profile_image);
             Picasso.get().load(profilepic).into(image);
         }
         public void setNoofmemeber1(String noofmemeber) {
             TextView mem = mview.findViewById(R.id.memrequred);
             mem.setText("Mem Required :" + noofmemeber);
              x = noofmemeber;

         }
         public void setDrivelocation(String drivelocation) {
             TextView dl = mview.findViewById(R.id.driveloc);
             dl.setText("Drive Location :"+drivelocation);
         }
        public void setPicuplocation(String picuplocation) {
             TextView pl = mview.findViewById(R.id.picuploc);
             pl.setText("Pickup location :"+picuplocation);
         }
         public void setSponsor(String sponsor) {
             TextView sp = mview.findViewById(R.id.sponsor);
             sp.setText("Sponsor :"+sponsor);
         }
         public void setTime(String time) {
             TextView t = mview.findViewById(R.id.drive_time);
             t.setText(time);
         }

         public void setdrivestatus(final String PostKey) {

             Driveref.addValueEventListener(new ValueEventListener() {

                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.child(PostKey).hasChild("Status")) {

                             Drivestats2.setVisibility(View.VISIBLE);
                             Drivestats.setVisibility(View.INVISIBLE);
                             Joindrive.setVisibility(View.INVISIBLE);


                     } else if (!dataSnapshot.child(PostKey).hasChild(currentUserId)) {

                             Drivestats2.setVisibility(View.INVISIBLE);
                             Joindrive.setVisibility(View.VISIBLE);

                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
         }
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
            case R.id.my_info:
                Intent myprofile =new Intent(MainActivity.this,MyProfile.class);
                startActivity(myprofile);
                break;
            case R.id.drives_history:
                Toast.makeText(MainActivity.this,"This activity is under development",Toast.LENGTH_SHORT).show();
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
        final Intent intent = new Intent(MainActivity.this,Login.class);
        mAuth.signOut();
        startActivity(intent);
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
    }
    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        //Toast.makeText(MainActivity.this,current_user_id,Toast.LENGTH_SHORT).show();
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(current_user_id))
                {
                    //Toast.makeText(MainActivity.this,"Welcome to Robbinhood Army",Toast.LENGTH_SHORT).show();

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

}
