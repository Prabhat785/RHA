package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class StartDrive extends AppCompatActivity {
    private EditText mpicuploc,mdriveloc,noofmember,msponsor;
    private ElegantNumberButton mbutton;
    private Button start;
    private FirebaseAuth mAuth;
    private DatabaseReference userref,Driveref;
    private String currentuserid;
    private  String randomname;
    private ProgressDialog loadingbar;
    private PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_drive);
        mpicuploc = (EditText) findViewById(R.id.pickuploc);
        mdriveloc =(EditText) findViewById(R.id.Driveloc);
        mbutton = (ElegantNumberButton ) findViewById(R.id.incbutton);
        String api="AIzaSyDhIgKLXY0mKh1JcomgnDj80zdIMcoeARM";
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),api);
        }
        placesClient=Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragmentstart = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.startlocation);
        AutocompleteSupportFragment autocompleteFragmentend= (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.endlocation);
        autocompleteFragmentstart.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteFragmentstart.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mpicuploc.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(StartDrive.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
        autocompleteFragmentend.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteFragmentend.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mdriveloc.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(StartDrive.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });

        noofmember =(EditText) findViewById(R.id.members);
        msponsor =(EditText) findViewById(R.id.sponsor);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        currentuserid = mAuth.getCurrentUser().getUid();
        start =(Button) findViewById(R.id.startdrive);
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        Driveref =FirebaseDatabase.getInstance().getReference().child("Drives");
        mbutton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = mbutton.getNumber();
                noofmember.setText(num);
            }
        });
        Calendar callFordate = Calendar.getInstance();
       final String savecurrdate= DateFormat.getDateInstance().format(callFordate.getTime());
        Calendar callFortime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final String savecurrtime = format.format(callFortime.getTime());
          randomname = savecurrdate+savecurrtime;
         start.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 final String plocation = mpicuploc.getText().toString();
                 final String dlocation = mdriveloc.getText().toString();
                 final String sponsor = msponsor.getText().toString();
                 final String noofmem = noofmember.getText().toString();
                 loadingbar.setTitle("Saving Your Drive");
                 loadingbar.setMessage("Please Wait.... ");
                 loadingbar.show();
                 loadingbar.setCanceledOnTouchOutside(true);
                 userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists())
                         {
                             String userfullname= dataSnapshot.child("Name").getValue().toString();
                             String profilepic = dataSnapshot.child("Profile").getValue().toString();
                             HashMap Drivemap = new HashMap();
                             Drivemap.put("uid",currentuserid);
                             Drivemap.put("date",savecurrdate);
                             Drivemap.put("time",savecurrtime);
                             Drivemap.put("picuplocation",plocation);
                             Drivemap.put("drivelocation",dlocation);
                             Drivemap.put("sponsor",sponsor);
                             Drivemap.put("Noofmemeber",noofmem);
                             Drivemap.put("Username",userfullname);
                             Drivemap.put("profilepic",profilepic);
                             Driveref.child(currentuserid+randomname).updateChildren(Drivemap).addOnCompleteListener(new OnCompleteListener() {
                                 @Override
                                 public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    Intent mainintent = new Intent(StartDrive.this,MainActivity.class);
                                    startActivity(mainintent);
                                    Toast.makeText(StartDrive.this,"Post is updated Sucessfuly",Toast.LENGTH_SHORT).show();
                                  loadingbar.dismiss();
                                }
                                else
                                {
                                    loadingbar.dismiss();
                                    Toast.makeText(StartDrive.this,"Error!",Toast.LENGTH_SHORT).show();
                                }

                                 }
                             });
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });


             }
         });
    }
}
