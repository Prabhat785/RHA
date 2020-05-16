package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.*;

public class Registration extends AppCompatActivity {
    private EditText mTextPhno;
    private EditText mname;
    private EditText mUsername,memail;
    private CountryCodePicker codePicker;
    private Button mlocationbtn;
    private Button mButtonRegister;
    String currentuserid;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private Context context;
    private DatabaseReference userref,tokenref;
    String Chapter;
    public String TOPIC_TO_SUBSCRIBE;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;
    final static int Gallery_Pick = 1;
    private CircularImageView profile;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mTextPhno = findViewById(R.id.phno);
        mname = findViewById(R.id.name);
        mUsername = findViewById(R.id.username);
        profile = (CircularImageView) findViewById(R.id.profilepic);
        mlocationbtn = findViewById(R.id.location);
        mButtonRegister = findViewById(R.id.registerButton);
        memail = findViewById(R.id.emailid);
        codePicker = findViewById(R.id.ccp);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        tokenref = FirebaseDatabase.getInstance().getReference().child("Tokens");
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
         mlocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(Registration.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Registration.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    getlocation();
                }
                else{
                    ActivityCompat.requestPermissions(Registration.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                    Toast.makeText(Registration.this, "Click get location button again", Toast.LENGTH_SHORT).show();
                }
            }


        });
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mname.getText().toString();
                final String user = mUsername.getText().toString();
                String phone=mTextPhno.getText().toString();
                String email = memail.getText().toString();
                int  x =0;
                //phone = "+" + codePicker.getSelectedCountryCode() + phone;
                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(Registration.this, "Please write your Name ...", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(user))
                {
                    Toast.makeText(Registration.this, "Please write your Userid...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(Registration.this, "Please confirm your Phoneno....", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    loadingbar.setTitle("Saving User data");
                    loadingbar.setMessage("Please Wait ");
                    loadingbar.show();
                    loadingbar.setCanceledOnTouchOutside(true);
                    final HashMap usermap = new HashMap();


                    usermap.put("Name",name);
                    usermap.put("Username",user);
                    usermap.put("Phoneno",phone);
                    usermap.put("Email",email);
                    usermap.put("drives",String.valueOf(x));
                    usermap.put("Smiles",String.valueOf(x));
                    userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Registration.this, "Registration Completed Sucessfully", Toast.LENGTH_SHORT).show();
                                // mAuth.signOut();

                                Intent movetomain = new Intent(Registration.this,MainActivity.class);
                                startActivity(movetomain);
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Registration.this, "Error! "+message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });


                }
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("Profile"))
                    {
                        String image = dataSnapshot.child("Profile").getValue().toString();
                        Picasso.get().load(image).into(profile);
                    }
                    else
                    {
                        Toast.makeText(Registration.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                        profile.requestFocus();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we updating your profile image...");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentuserid + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap <String,String> hashMap = new HashMap<>();
                                    hashMap.put("Profile",String.valueOf(uri));
                                    userref.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registration.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                           
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }
    private void getlocation() {
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location!=null){
                    try {
                        loadingbar.setTitle("Location");
                        loadingbar.setMessage("Please wait, while we updating your location...");
                        loadingbar.show();
                        loadingbar.setCanceledOnTouchOutside(true);
                        HashMap hashMap=new HashMap();
                        Geocoder geocoder=new Geocoder(Registration.this, Locale.getDefault());
                        List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        hashMap.put("Latitude",addresses.get(0).getLatitude());
                        hashMap.put("Longitude",addresses.get(0).getLongitude());
                        hashMap.put("Address",addresses.get(0).getAddressLine(0));
                        hashMap.put("Chapter",addresses.get(0).getLocality());
                        Chapter=addresses.get(0).getLocality();
                        userref.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Registration.this, "Location Saved Successfully...", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }
        });
    }

}
