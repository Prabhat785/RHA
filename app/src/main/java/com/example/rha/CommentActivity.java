package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private ImageButton postcommment;
    private EditText commentinput;
    private RecyclerView Commentlist1;
    private DatabaseReference userref,postref,postref1;
    private String currenuserid,PostKey,Name;
    private commentadapter cmntadapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postcommment = findViewById(R.id.Postcmntbtn);
        commentinput =findViewById(R.id.commentinput);
        Commentlist1=findViewById(R.id.cmntlist);
        Commentlist1.setHasFixedSize(true);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        Name = getIntent().getExtras().get("Postkey").toString();
        userref= FirebaseDatabase.getInstance().getReference().child("User");
        postref=FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey).child("Comments");
        postref1=FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);
        mAuth=FirebaseAuth.getInstance();
        currenuserid=mAuth.getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Commentlist1.setLayoutManager(linearLayoutManager);
         postcommment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            userref.child(currenuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if(dataSnapshot.exists())
                {
                    String name=dataSnapshot.child("Name").getValue().toString();
                    Validatecomment(name);
                        commentinput.setText(" ");
                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    });
        FirebaseRecyclerOptions<Commentlist> options =
                new  FirebaseRecyclerOptions.Builder<Commentlist>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey).child("Comments"),Commentlist.class)
                .build();
        cmntadapter = new commentadapter(options);
        Commentlist1.setAdapter(cmntadapter);
    }

    private void Validatecomment(final String name) {

        userref= FirebaseDatabase.getInstance().getReference().child("User").child(currenuserid);
      String comment = commentinput.getText().toString();
      if(TextUtils.isEmpty(comment))
      {
          Toast.makeText(CommentActivity.this,"Please write something to post",Toast.LENGTH_SHORT).show();
      }
      else
      {
          Calendar callFordate = Calendar.getInstance();
          final String savecurrdate= DateFormat.getDateInstance().format(callFordate.getTime());
          Calendar callFortime = Calendar.getInstance();
          SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
          final String savecurrtime = format.format(callFortime.getTime());
          final  String randomkey = savecurrdate+savecurrtime;
          HashMap commentmap = new HashMap();
          commentmap.put("uid",currenuserid);
          commentmap.put("Comment",comment);
          commentmap.put("Date",savecurrdate);
          commentmap.put("Time",savecurrtime);
          commentmap.put("Name",name);
          postref.child(randomkey).updateChildren(commentmap).addOnCompleteListener(new OnCompleteListener() {
              @Override
              public void onComplete(@NonNull Task task) {
                  if(task.isSuccessful())
                  {
                      Toast.makeText(CommentActivity.this,"You have commented sucessfully",Toast.LENGTH_SHORT).show();
                      userref.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                              final String chapter=dataSnapshot.child("Chapter").getValue().toString();
                              subscribetonotificationcomment("Comment"+chapter);
                              postref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                      String chapter=dataSnapshot.child("Chapter").getValue().toString();
                                      Name=dataSnapshot1.child("Name").getValue().toString();
                                      if(name.equals(Name)){
                                          try {
                                              prepareNotifiaction(name,name+" commented your post","Click to see post","Comment"+chapter,"CommentNotification");
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }
                                      }
                                      else{
                                          try {
                                              prepareNotifiaction(name,name+" also commented on "+Name+"'s"+" Post ","Click to see post","Comment"+chapter,"CommentNotification");
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }
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
                  }
                  else
                  {
                      Toast.makeText(CommentActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                  }
              }
          });
      }

    }
    private void subscribetonotificationcomment(String TOPIC_TO_SUBSCRIBE ){
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TO_SUBSCRIBE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

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
                Toast.makeText(CommentActivity.this,""+response.toString(),Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(CommentActivity.this).add(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cmntadapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cmntadapter.startListening();
    }
}
