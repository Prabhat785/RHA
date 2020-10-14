package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyInfoUpdate extends AppCompatActivity {
    private EditText mPhoneText;
    private EditText mEmailText;
    private TextView mLocationText;
    private Button button;
    private Button locationbutton;
    private TextView mChapterText;
    private DatabaseReference userref;
    private LocationCallback locationCallback;
    private FirebaseAuth mAuth;
    String Chapter;
    private String currentuserid;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ProgressDialog loadingbar;
            private CircularImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_update);
        Bundle bundle=getIntent().getExtras();
        mPhoneText=findViewById(R.id.phno_update);
        mEmailText=findViewById(R.id.email_update);
        mLocationText=findViewById(R.id.location_update);
        button=findViewById(R.id.btnupdate);
        mChapterText=findViewById(R.id.chapter_update);
        locationbutton=findViewById(R.id.locationupdate);
        mEmailText.requestFocus();
        mPhoneText.requestFocus();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        mPhoneText.requestFocus();
        profile = (CircularImageView)findViewById(R.id.profilepic);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        loadingbar = new ProgressDialog(this);
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              String phoneno = dataSnapshot.child("Phoneno").getValue().toString();
                                              String email = dataSnapshot.child("Email").getValue().toString();
                                              String pic;
                                              if(dataSnapshot.hasChild("Profile"))
                                                  pic = dataSnapshot.child("Profile").getValue().toString();
                                              else pic=null;String location = dataSnapshot.child("Address").getValue().toString();
                                              String Chapter = dataSnapshot.child("Chapter").getValue().toString();
                                              //String location=dataSnapshot.child("Location").getValue().toString();
                                              mPhoneText.setText(phoneno);
                                              mEmailText.setText(email);
                                              mLocationText.setText(location);
                                              mChapterText.setText(Chapter);
                                              if(pic!=null)
                                              Picasso.get().load(pic).into(profile);
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });


        locationbutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1000);
        int permission= ActivityCompat.checkSelfPermission(
                MyInfoUpdate.this, Manifest.permission.ACCESS_FINE_LOCATION) ;
        Toast.makeText(MyInfoUpdate.this, "Error! "+permission, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(MyInfoUpdate.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            getlocation();
        else{
             //ActivityCompat.requestPermissions(MyInfoUpdate.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(
                    MyInfoUpdate.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }
});

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent movetoprofile=new Intent(MyInfoUpdate.this,MyProfile.class);
                final String phone=mPhoneText.getText().toString();
                final String email=mEmailText.getText().toString();
                final String location=mLocationText.getText().toString();
                HashMap usermap = new HashMap();
                usermap.put("Email",email);
                usermap.put("Phoneno",phone);
                usermap.put("Address",location);
                userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MyInfoUpdate.this, "Profile updated ", Toast.LENGTH_SHORT).show();
                            // mAuth.signOut();
                            Intent movetoprofile= new Intent(MyInfoUpdate.this,MyProfile.class);
                            movetoprofile.putExtra("phoneupdate",phone);
                            movetoprofile.putExtra("emailupdate",email);
                            startActivity(movetoprofile);
                            finish();
                            loadingbar.dismiss();
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(MyInfoUpdate.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });

            }
        });
    }
    private void getlocation() {
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
            @Override
            public void onSuccess(LocationAvailability locationAvailability) {
                Toast.makeText(MyInfoUpdate.this, "location available" +locationAvailability.isLocationAvailable() , Toast.LENGTH_SHORT).show();
               // Log.d(TAG, "onSuccess: locationAvailability.isLocationAvailable " + locationAvailability.isLocationAvailable());
                if (locationAvailability.isLocationAvailable()) {
                    if (ActivityCompat.checkSelfPermission(MyInfoUpdate.this.getApplicationContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                loadingbar.setTitle("Location");
                                loadingbar.setMessage("Please wait, while we updating your location...");
                                loadingbar.show();
                                loadingbar.setCanceledOnTouchOutside(true);
                                HashMap hashMap=new HashMap();
                                Geocoder geocoder=new Geocoder(MyInfoUpdate.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                hashMap.put("Latitude",addresses.get(0).getLatitude());
                                hashMap.put("Longitude",addresses.get(0).getLongitude());
                                hashMap.put("Address",addresses.get(0).getAddressLine(0));
                                hashMap.put("Chapter",addresses.get(0).getLocality());
                                userref.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MyInfoUpdate.this, "Location Saved Successfully...", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                    }
                                });
                                mLocationText.setText(addresses.get(0).getAddressLine(0));
                                mChapterText.setText(addresses.get(0).getLocality());
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(MyInfoUpdate.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }

                } else {
                   // ActivityCompat.requestPermissions(MyInfoUpdate.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(MyInfoUpdate.this, "Turn your location on!", Toast.LENGTH_SHORT).show();
                    LocationRequest locationRequest= LocationRequest.create();
                    locationRequest.setInterval(30 * 1000);
                    locationRequest.setNumUpdates(1);
                    locationRequest.setExpirationDuration(20000);
                    locationRequest.setFastestInterval(500);
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }
                            for (Location location : locationResult.getLocations()) {
                                loadingbar.setTitle("Location");
                                loadingbar.setMessage("Please wait, while we updating your location...");
                                loadingbar.show();
                                loadingbar.setCanceledOnTouchOutside(true);
                                HashMap hashMap=new HashMap();
                                Geocoder geocoder=new Geocoder(MyInfoUpdate.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                hashMap.put("Latitude",addresses.get(0).getLatitude());
                                hashMap.put("Longitude",addresses.get(0).getLongitude());
                                hashMap.put("Address",addresses.get(0).getAddressLine(0));
                                hashMap.put("Chapter",addresses.get(0).getLocality());
                                userref.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MyInfoUpdate.this, "Location Saved Successfully...", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                    }
                                });
                                mLocationText.setText(addresses.get(0).getAddressLine(0));
                                mChapterText.setText(addresses.get(0).getLocality());
                            }
                        }
                    };
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

                }

            }
        });
    }
}
