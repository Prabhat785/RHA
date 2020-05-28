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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Post_Activity extends AppCompatActivity {

    private ImageView imagebtn;
    private EditText postdes;
    private Button upload;
    private ProgressDialog loadingbar;
    private  Uri ImageUri;
    private FirebaseAuth mAuth;
    private  String des,postrandomname,downloadurl,currentuserid, savecurrtime,savecurrdate;
    private StorageReference postimgref;
    private DatabaseReference userref,postref,postcount;
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
        postcount=FirebaseDatabase.getInstance().getReference();
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
                   final String Fullname = dataSnapshot.child("Name").getValue().toString();
                   final String profilepic=dataSnapshot.child("Profile").getValue().toString();
                   final String Chapter = dataSnapshot.child("Chapter").getValue().toString();
                   final HashMap Postmap = new HashMap();
                   if(cb2.isChecked())
                   {
                       Postmap.put("Chapter",Chapter);
                   }
                   else if(cb.isChecked())
                   {
                       Postmap.put("Chapter","ALL");
                   }
                   postcount.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final int count=Integer.parseInt(dataSnapshot.child("PostCount").getValue().toString());
                           Postmap.put("Key",currentuserid+postrandomname);
                           Postmap.put("uid",currentuserid);
                           Postmap.put("Name",Fullname);
                           Postmap.put("Date",savecurrdate);
                           Postmap.put("Time",savecurrtime);
                           Postmap.put("Description",des);
                           Postmap.put("Postimg",downloadurl);
                           Postmap.put("Profilepic",profilepic);
                           Postmap.put("Postid","Id"+String.valueOf(count+1));
                           postref.child(currentuserid+postrandomname).updateChildren(Postmap).addOnCompleteListener(new OnCompleteListener() {
                               @Override
                               public void onComplete(@NonNull Task task) {
                                   if(task.isSuccessful())
                                   {
                                       loadingbar.dismiss();
                                       Toast.makeText(Post_Activity.this,"Post Uploaded Sucessfully",Toast.LENGTH_SHORT).show();
                                        postcount.child("PostCount").setValue(String.valueOf(count+1));
                                       try {
                                           prepareNotifiaction(Fullname,Fullname+" posted in your chapter ","Click here to see post","Post"+Chapter,"PostNotification");
                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }
                                       Intent mainintent = new Intent(Post_Activity.this,MainActivity.class);
                                       startActivity(mainintent);

                                   }
                               }
                           });

                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });               }

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
    private  void prepareNotifiaction(String pid,String Title,String Description,String notificationTopic,String notificationtype) throws JSONException {
        // Toast.makeText(StartDrive.this,"Notification prepared",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"response"+response.toString(),Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
