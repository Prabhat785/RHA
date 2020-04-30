package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prf;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TextView textView;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private DatabaseReference userref,ur;
    String currid;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        currid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        ur = FirebaseDatabase.getInstance().getReference().child("User").child(currid);
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
        ur.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("Username").getValue().toString();
                textView.setText("Hello "+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView result = (TextView)findViewById(R.id.resultView);
        Button btnLogOut = (Button)findViewById(R.id.LogoutButton);

       final Intent intent = new Intent(MainActivity.this,Login.class);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prf.edit();
                editor.clear();
                editor.commit();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser==null)
        {
            SendusertologinActivity();
        }
        else
        {

            Checkuserexistance();
        }

    }

    private void Checkuserexistance() {
        final  String currentuser_id =mAuth.getCurrentUser().getUid();
        //Toast.makeText(MainActivity.this,"User id"+currentuser_id, Toast.LENGTH_SHORT).show();
           userref.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentuser_id))
                {
                    Toast.makeText(MainActivity.this,"Enter User Data", Toast.LENGTH_SHORT).show();
                    Intent reg = new Intent(MainActivity.this,Register.class);
                    startActivity(reg);
                }

               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   String message=databaseError.getMessage();
                   Toast.makeText(MainActivity.this,"User Data "+message, Toast.LENGTH_SHORT).show();
               }
           });
    }

    private void SendusertologinActivity()
    {
        Intent loginintent = new Intent(MainActivity.this,Login.class);
        startActivity(loginintent);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,new MyInfoFragement()).commit();
                break;
            case R.id.drives_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,new DrivesFragement()).commit();
                break;
            case R.id.log:
                mAuth.signOut();
                SendusertologinActivity();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
