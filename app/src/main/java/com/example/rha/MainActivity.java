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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    private DatabaseReference userref;
    String currentuserid;
    private FirebaseAuth mAuth;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer);
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
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
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("Username").getValue().toString();

                //String location=dataSnapshot.child("Location").getValue().toString();
                textView.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //textView=(TextView)findViewById(R.layout.nav_header_layout);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.frame,new DrivesFragement()).commit();
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
}
