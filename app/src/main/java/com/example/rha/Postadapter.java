package com.example.rha;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Postadapter extends RecyclerView.Adapter<Postadapter.Postvewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param
     */
    private List<Postlist> postlist = new ArrayList<>();
    private Context mContext;
    private DatabaseReference Likeref,usseref,postref;
    Boolean likecheck =false;
    private  String currentuserid;
    public Postadapter(List<Postlist> lists, Context context,String cuid) {

        postlist = lists;
        mContext = context;
        currentuserid =cuid;
    }
    @Override
    public Postvewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.post_layout,null,false);

        return new Postadapter.Postvewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postvewholder driveViewHolder, int position)
    {
        Likeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        usseref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        final String Postkey  = postlist.get(position).getKey1();
        final String name=postlist.get(position).getName1();
        driveViewHolder.setDate1(postlist.get(position).getDate1());
        driveViewHolder.setName1(postlist.get(position).getName1());
        driveViewHolder.setTime1(postlist.get(position).getTime1());
        driveViewHolder.setDescription1(postlist.get(position).getDescription1());
        driveViewHolder.setPostimg1(postlist.get(position).getPostimg1());
        driveViewHolder.setProfilepic1(postlist.get(position).getProfilepic1());
        driveViewHolder.setabc(Postkey);
        postref= FirebaseDatabase.getInstance().getReference().child("Post").child(Postkey);
         driveViewHolder.Likebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view)
             {
               likecheck =true;
               Likeref.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(likecheck.equals(true)) {
                        if (dataSnapshot.child(Postkey).hasChild(currentuserid)) {
                            Likeref.child(Postkey).child(currentuserid).removeValue();
                            likecheck = false;
                        } else {
                            Toast.makeText(mContext,"Like",Toast.LENGTH_SHORT).show();
                            Likeref.child(Postkey).child(currentuserid).setValue(true);
                            usseref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    final String chapter=dataSnapshot.child("Chapter").getValue().toString();
                                    final String userfullname=dataSnapshot.child("Username").getValue().toString();
                                    subscribetonotificationlike("Likes"+chapter);
                                    postref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            String chapter=dataSnapshot.child("Chapter").getValue().toString();
                                            String userfullname=dataSnapshot.child("Name").getValue().toString();
                                            if(name.equals(userfullname)){
                                                try {
                                                    prepareNotifiaction(userfullname,userfullname+" liked your post","Click to see post","Likes"+chapter,"LikeNotification");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                try {
                                                    prepareNotifiaction(userfullname,userfullname+" liked "+name+"'s"+" Post ","Click to see post","Likes"+chapter,"LikeNotification");
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

                            likecheck = true;
                        }
                       // startActivity(mContext,Postkey);
                    }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
             }
         });
         driveViewHolder.Cmntbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 startActivity(mContext,Postkey);
             }
         });
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    class Postvewholder extends RecyclerView.ViewHolder{
        ImageButton Likebtn,Cmntbtn;
        View mview;
        int countlike;
        TextView Likecount;
        DatabaseReference Likeref;
        public Postvewholder(@NonNull View itemView) {
            super(itemView);
            mview= itemView;
            Likebtn = mview.findViewById(R.id.likebtn);
            Cmntbtn =mview.findViewById(R.id.cmntbtn);
           Likecount = mview.findViewById(R.id.likecnt);
            Likeref =FirebaseDatabase.getInstance().getReference().child("Likes");
        }
        public  void setabc(final String Postkey)
        {
            Likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(Postkey).hasChild(currentuserid))
                    {
                        countlike = (int) dataSnapshot.child(Postkey).getChildrenCount();
                        Likebtn.setImageResource(R.drawable.like);
                         Likecount.setText(Integer.toString(countlike)+" Likes");
                    }
                    else
                    {
                       countlike = (int) dataSnapshot.child(Postkey).getChildrenCount();
                        Likebtn.setImageResource(R.drawable.dislike);
                        Likecount.setText(Integer.toString(countlike)+" Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setName1(String name) {
            TextView username = (TextView)itemView.findViewById(R.id.user_name);
            username.setText(name);
        }

        public void setPostimg1(String postimg) {
            ImageView image2;
            image2 = (ImageView) itemView.findViewById(R.id.postimg);
            Picasso.get().load(postimg).into(image2);
        }
        public void setDate1(String date) {
            TextView date1= (TextView) itemView.findViewById(R.id.drive_date);
            date1.setText(" "+date+" ");
        }
        public void setProfilepic1( String profilepic) {
            CircularImageView image = (CircularImageView) itemView.findViewById(R.id.profile_image);
            Picasso.get().load(profilepic).into(image);
        }

        public void setDescription1(String description) {
            TextView postdes = itemView.findViewById(R.id.postdescription);
            postdes.setText(description);
        }
        public void setTime1(String time) {
            TextView t = itemView.findViewById(R.id.drive_time);
            t.setText(time);
        }
    }
    private void subscribetonotificationlike(String TOPIC_TO_SUBSCRIBE ){
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TO_SUBSCRIBE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

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
                Toast.makeText(mContext,""+response.toString(),Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(mContext).add(jsonObjectRequest);
    }
    public static void startActivity(Context context,final  String Postkey)
    {
        Intent intent = new Intent(context,CommentActivity.class);
        intent.putExtra("Postkey",Postkey);
        context.startActivity(intent);
    }
}



