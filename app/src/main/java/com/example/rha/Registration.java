package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class Registration extends AppCompatActivity {
    private  EditText mTextPhno;
    private EditText mname;
    private  EditText mUsername;
    private CountryCodePicker codePicker;
    private Button mlocationbtn;
    private Button mButtonRegister;
    String currentuserid;
    private FirebaseAuth mAuth;
    private DatabaseReference userref;
    private ProgressDialog loadingbar;
    private StorageReference UserProfileImageRef;
    final static int Gallery_Pick = 1;
    private CircularImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mTextPhno=findViewById(R.id.phno);
        mname=findViewById(R.id.name);
        mUsername=findViewById(R.id.username);
        profile = (CircularImageView) findViewById(R.id.profilepic);
        mlocationbtn=findViewById(R.id.location);
        mButtonRegister=findViewById(R.id.registerButton);
        codePicker =findViewById(R.id.ccp);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        Bundle bundle=getIntent().getExtras();
        final String email=bundle.getString("email");
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar = new ProgressDialog(this);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=mname.getText().toString();
                final String user = mUsername.getText().toString();
                String phone=mTextPhno.getText().toString();
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
                    HashMap usermap = new HashMap();
                    usermap.put("Name",name);
                    usermap.put("Username",user);
                    usermap.put("Phoneno",phone);
                    usermap.put("Email",email);
                    userref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Registration.this, "Registration Completed Sucessfully", Toast.LENGTH_SHORT).show();
                                // mAuth.signOut();
                                Intent movetomain = new Intent(Registration.this,MainActivity.class);
                                movetomain.putExtra("Email",email);
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
                            Toast.makeText(Registration.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

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
}
