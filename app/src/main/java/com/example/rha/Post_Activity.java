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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
public class Post_Activity extends AppCompatActivity {

    private ImageView imagebtn;
    private EditText postdes;
    private Button upload;
    private ProgressDialog loadingbar;
    private  Uri ImageUri;
    private FirebaseAuth mAuth;
    private  String des,postrandomname,downloadurl,currentuserid, savecurrtime,savecurrdate;
    private StorageReference postimgref;
    private DatabaseReference userref,postref;
    private CheckBox cb,cb2;
private static final int Gallery_Pick =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_);
        imagebtn = findViewById(R.id.imgbtn);
        postdes=findViewById(R.id.postdesc);
        upload=findViewById(R.id.Postbtn);
        mAuth=FirebaseAuth.getInstance();
        cb=findViewById(R.id.checkBox);
        cb2=findViewById(R.id.checkBox2);
        currentuserid=mAuth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("User");
        postref=FirebaseDatabase.getInstance().getReference().child("Post");
        postimgref = FirebaseStorage.getInstance().getReference();
        loadingbar = new ProgressDialog(this);
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
              startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatepost();

            }
        });
    }

    private void validatepost() {
         des = postdes.getText().toString();
        if(ImageUri==null)
        {
            Toast.makeText(Post_Activity.this,"Please Select post image....",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(des))
        {
            Toast.makeText(Post_Activity.this,"Please Describe Your Post....",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Saving Your Drive");
            loadingbar.setMessage("Please Wait.... ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            Storeimagetosorage();
        }
    }

    private void Storeimagetosorage() {
        Calendar callFordate = Calendar.getInstance();
        savecurrdate= DateFormat.getDateInstance().format(callFordate.getTime());
        Calendar callFortime = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
         savecurrtime = format.format(callFortime.getTime());
       postrandomname = savecurrdate+savecurrtime;
       final  StorageReference filepath = postimgref.child("Post Images").child(ImageUri.getLastPathSegment()+postrandomname+".jpg");
        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
             if(task.isSuccessful())
             {
                 filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         downloadurl= String.valueOf(uri);
                        Toast.makeText(Post_Activity.this, "Image Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                         savingpostlink();
                     }

                 });
             }
             else
             {
                 String message = task.getException().getMessage();

                 Toast.makeText(Post_Activity.this,"Error Ocurred "+message,Toast.LENGTH_SHORT).show();
             }
            }
        });
    }


    private void savingpostlink()
    {
       userref.child(currentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   String Fullname = dataSnapshot.child("Name").getValue().toString();
                   String profilepic=dataSnapshot.child("Profile").getValue().toString();
                   String Chapter = dataSnapshot.child("Chapter").getValue().toString();
                   HashMap Postmap = new HashMap();
                   if(cb2.isChecked())
                   {
                       Postmap.put("Chapter",Chapter);
                   }
                   else if(cb.isChecked())
                   {
                       Postmap.put("Chapter","ALL");
                   }
                   Postmap.put("Key",currentuserid+postrandomname);
                   Postmap.put("uid",currentuserid);
                   Postmap.put("Name",Fullname);
                   Postmap.put("Date",savecurrdate);
                   Postmap.put("Time",savecurrtime);
                   Postmap.put("Description",des);
                   Postmap.put("Postimg",downloadurl);
                   Postmap.put("Profilepic",profilepic);
                   postref.child(currentuserid+postrandomname).updateChildren(Postmap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           if(task.isSuccessful())
                           {
                               loadingbar.dismiss();
                               Toast.makeText(Post_Activity.this,"Post Uploaded Sucessfully",Toast.LENGTH_SHORT).show();

                               Intent mainintent = new Intent(Post_Activity.this,MainActivity.class);
                              startActivity(mainintent);

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
             ImageUri = data.getData();
            imagebtn.setImageURI(ImageUri);
        }


    }
}
