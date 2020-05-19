package com.example.rha;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Drivesadapter  extends RecyclerView.Adapter<Drivesadapter.Drivevewholder>  {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param //options
     *
     */
    private List <Drivelist> drivelists = new ArrayList<>();
     private Context mtx;
    String currentuser_id;
    Boolean Memchecker;
    String TOPIC_TO_SUBSCRIBE;
    private FirebaseAuth mAuth;
    private DatabaseReference userref,Driveref,Memref;

    public Drivesadapter(List <Drivelist> list1,Context context,String cuid) {
        drivelists = list1;
        mtx =context;

        currentuser_id = cuid;
        //mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Drivevewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mtx)
                .inflate(R.layout.all_driveslayout, parent, false);
        return new Drivevewholder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull Drivevewholder   driveViewHolder, int position) {
        final String Postkey  = drivelists.get(position).getKey1();
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        Memref = FirebaseDatabase.getInstance().getReference().child("Members");
        Driveref=FirebaseDatabase.getInstance().getReference().child("Drives");
        driveViewHolder.setDate(drivelists.get(position).getDate());
        driveViewHolder.setUsername1(drivelists.get(position).getUsername1());
        driveViewHolder.setTime(drivelists.get(position).getTime());
        driveViewHolder.setDrivelocation(drivelists.get(position).getDrivelocation());
        driveViewHolder.setPicuplocation(drivelists.get(position).getPicuplocation());
        driveViewHolder.setSponsor(drivelists.get(position).getSponsor());
        driveViewHolder.setNoofmemeber1(drivelists.get(position).getNoofmemeber1());
        driveViewHolder.setProfilepic(drivelists.get(position).getProfilepic());
        driveViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Class<MainActivity> mtx= MainActivity.class;
                startActivity(mtx,Postkey);
            }
        });
        driveViewHolder.setabc(Postkey);
        driveViewHolder.setButton(Postkey);
        driveViewHolder.setdrivestatus(Postkey);
        driveViewHolder.Joindrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(mtx,Postkey,Toast.LENGTH_LONG).show();
                Memchecker = true;
                Memref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(Memchecker.equals(true))
                        {
                            if(dataSnapshot.child(Postkey).hasChild(currentuser_id))
                            {
                                Memref.child(Postkey).child(currentuser_id).removeValue();
                                Memchecker =false;
                            }
                            else
                            {
                                userref.child(currentuser_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.child("Name").getValue().toString();
                                        final String phno = dataSnapshot.child("Phoneno").getValue().toString();
                                        final String add = dataSnapshot.child("Address").getValue().toString();
                                        final String username =dataSnapshot.child("Username").getValue().toString();
                                        String profilepic =dataSnapshot.child("Profile").getValue().toString();
                                        HashMap memberinfo = new HashMap();
                                        memberinfo.put("Name",name);
                                        memberinfo.put("Phoneno",phno);
                                        memberinfo.put("Address",add);
                                        memberinfo.put("Username",username);
                                        memberinfo.put("Profile",profilepic);
                                        Memref.child(Postkey).child(currentuser_id).setValue(memberinfo);
                                        Memchecker = false;
                                        Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String dlocation=dataSnapshot.child(Postkey).child("drivelocation").getValue().toString();
                                                if(dataSnapshot.child(Postkey).child("Status").getValue().toString()=="false"){
                                                try {
                                                    prepareNotifiaction(username,username+" has also joined drive","His Phone- "+phno+" ","Join"+dlocation,"JoinNotification",Postkey);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                                subscribetonotification(dlocation);
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() { return drivelists.size(); }
    class Drivevewholder extends RecyclerView.ViewHolder{
        View mview;
        Button Joindrive;
        TextView Memrequied,Drivestats,Drivestats2;
        int coutmem;
        String currentUserId;
        DatabaseReference Memref;
        String x;
         DatabaseReference Driveref;
        public Drivevewholder(@NonNull View itemView) {
            super(itemView);
            mview= itemView;
            Joindrive = (Button) mview.findViewById(R.id.join);
            Memrequied =(TextView ) mview.findViewById(R.id.memreq);
            Drivestats = (TextView) mview.findViewById(R.id.drivestatus);
            Drivestats2=mview.findViewById(R.id.drivestatus2);
            Memref = FirebaseDatabase.getInstance().getReference().child("Members");
            Driveref=FirebaseDatabase.getInstance().getReference().child("Drives");
            currentUserId = currentuser_id;
        }
        public void setButton(final String PostKey)
        {
            String abc = PostKey;

            Memref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        coutmem =(int )dataSnapshot.child(PostKey).getChildrenCount();
                        Joindrive.setText("Joined");
                        Memrequied.setText(Integer.toString(coutmem)+" Members Joined");


                    }
                    else if (!dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        coutmem =(int )dataSnapshot.child(PostKey).getChildrenCount();
                        Joindrive.setText("Join");
                        Memrequied.setText(Integer.toString(coutmem)+" Members Joined");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setabc( final String PostKey)
        {

            Memref.addValueEventListener(new ValueEventListener() {
                public DatabaseReference userref;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                        coutmem = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        if(coutmem>=Integer.parseInt(x))
                        {
                            Drivestats.setVisibility(View.VISIBLE);
                            Joindrive.setVisibility(View.INVISIBLE);

                        }

                    } else if (!dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                        coutmem = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        if(coutmem<Integer.parseInt(x))
                        {
                            Drivestats.setVisibility(View.INVISIBLE);
                            Joindrive.setVisibility(View.VISIBLE);

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        public void setUsername1(String Username) {
            TextView username = (TextView)itemView.findViewById(R.id.user_name);
            username.setText(Username);
        }
        public void setDate(String date) {
            TextView date1= (TextView) itemView.findViewById(R.id.drive_date);
            date1.setText(" "+date+" ");
        }
        public void setProfilepic( String profilepic) {
            CircularImageView image = (CircularImageView) itemView.findViewById(R.id.profile_image);
            Picasso.get().load(profilepic).into(image);
        }
        public void setNoofmemeber1(String noofmemeber) {
            TextView mem = itemView.findViewById(R.id.memrequred);
            mem.setText("Mem Required :" + noofmemeber);
            x = noofmemeber;

        }
        public void setDrivelocation(String drivelocation) {
            TextView dl = itemView.findViewById(R.id.driveloc);
            dl.setText("Drive Location :"+drivelocation);
        }
        public void setPicuplocation(String picuplocation) {
            TextView pl = itemView.findViewById(R.id.picuploc);
            pl.setText("Pickup location :"+picuplocation);
        }
        public void setSponsor(String sponsor) {
            TextView sp = itemView.findViewById(R.id.sponsor);
            sp.setText("Sponsor :"+sponsor);
        }
        public void setTime(String time) {
            TextView t = itemView.findViewById(R.id.drive_time);
            t.setText(time);
        }
        public void setdrivestatus(final String PostKey) {

            Driveref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).child("Status").getValue().toString()=="true") {

                        Drivestats2.setVisibility(View.VISIBLE);
                       // Drivestats.setVisibility(View.INVISIBLE);



                    } else if (dataSnapshot.child(PostKey).child("Status").getValue().toString()=="false") {

                        Drivestats2.setVisibility(View.INVISIBLE);
                       // Drivestats.setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void subscribetonotification(String dlocation){
        TOPIC_TO_SUBSCRIBE="Join"+dlocation;
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_TO_SUBSCRIBE).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
    private  void prepareNotifiaction(String pid,String Title,String Description,String notificationTopic,String notificationtype,String PostKey) throws JSONException {
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
        notificationbody.put("Postkey",PostKey);
        notification.put("to",NOTIFICATION_TOPIC);
        notification.put("data",notificationbody);

        sendpostNotification(notification);
    }

    private void sendpostNotification(JSONObject notification) {
        //Toast.makeText(StartDrive.this,"Notification prepared",Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mtx.getApplicationContext(),""+response.toString(),Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(mtx).add(jsonObjectRequest);
    }
    public static void startActivity(Context context,final  String Postkey)
    {
        Intent intent = new Intent(context,Driveview.class);
        intent.putExtra("Postkey",Postkey);
        context.startActivity(intent);
    }
}

