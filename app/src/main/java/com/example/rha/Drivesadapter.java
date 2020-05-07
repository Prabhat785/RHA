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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Drivesadapter  extends FirebaseRecyclerAdapter<Drivelist, Drivesadapter.Drivevewholder>  {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     *
     */
     private Context mtx;
    String currentuser_id;
    Boolean Memchecker;
    private FirebaseAuth mAuth;
    private DatabaseReference userref,Driveref,Memref;

    public Drivesadapter(@NonNull FirebaseRecyclerOptions<Drivelist> options,Context context,String cuid) {
        super(options);
        this.mtx =context;

        currentuser_id = cuid;
        //mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull Drivevewholder driveViewHolder, int position, @NonNull Drivelist drivelist) {
        final String Postkey  = getRef(position).getKey();
        userref = FirebaseDatabase.getInstance().getReference().child("User");
        Memref = FirebaseDatabase.getInstance().getReference().child("Members");
        driveViewHolder.setDate(drivelist.getDate());
        driveViewHolder.setUsername1(drivelist.getUsername1());
        driveViewHolder.setTime(drivelist.getTime());
        driveViewHolder.setDrivelocation(drivelist.getDrivelocation());
        driveViewHolder.setPicuplocation(drivelist.getPicuplocation());
        driveViewHolder.setSponsor(drivelist.getSponsor());
        driveViewHolder.setNoofmemeber1(drivelist.getNoofmemeber1());
        driveViewHolder.setProfilepic(drivelist.getProfilepic());
        driveViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //Class<MainActivity> mtx= MainActivity.class;
                startActivity(mtx,Postkey);
            }
        });

        driveViewHolder.setButton(Postkey);
        driveViewHolder.setdrivestatus(Postkey);
        driveViewHolder.setabc(Postkey);
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
                                        String phno = dataSnapshot.child("Phoneno").getValue().toString();
                                        String add = dataSnapshot.child("Address").getValue().toString();
                                        String username =dataSnapshot.child("Username").getValue().toString();
                                        String profilepic =dataSnapshot.child("Profile").getValue().toString();
                                        HashMap memberinfo = new HashMap();
                                        memberinfo.put("Name",name);
                                        memberinfo.put("Phoneno",phno);
                                        memberinfo.put("Address",add);
                                        memberinfo.put("Username",username);
                                        memberinfo.put("Profile",profilepic);
                                        Memref.child(Postkey).child(currentuser_id).setValue(memberinfo);
                                        Memchecker = false;
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

    @NonNull
    @Override
    public Drivevewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_driveslayout, parent, false);

        return new Drivevewholder(view);
    }


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

            Driveref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild("Status")) {

                        Drivestats2.setVisibility(View.VISIBLE);
                        Drivestats.setVisibility(View.INVISIBLE);



                    } else  {

                        Drivestats2.setVisibility(View.INVISIBLE);
                        //Drivestats.setVisibility(View.VISIBLE);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void startActivity(Context context,final  String Postkey)
    {
        Intent intent = new Intent(context,Driveview.class);
        intent.putExtra("Postkey",Postkey);
        context.startActivity(intent);
    }
}

