package com.example.rha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Drivehistoryadapter extends RecyclerView.Adapter<Drivehistoryadapter.Drivevewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param
     */
    private List<Drivelist1> drivelist = new ArrayList<>();
    private Context mContext;
    private DatabaseReference Memref;
    public Drivehistoryadapter(List<Drivelist1> lists, Context context) {

        drivelist = lists;
        mContext = context;
    }
    @Override
    public Drivevewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.all_drivehistroy,null,false);

        return new Drivehistoryadapter.Drivevewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Drivevewholder driveViewHolder, int position)
    {
        final String Postkey  = drivelist.get(position).getKey1();
        driveViewHolder.setDate(drivelist.get(position).getDate());
        driveViewHolder.setUsername1(drivelist.get(position).getUsername1());
        driveViewHolder.setTime(drivelist.get(position).getTime());
        driveViewHolder.setDrivelocation(drivelist.get(position).getDrivelocation());
        driveViewHolder.setPicuplocation(drivelist.get(position).getPicuplocation());
        driveViewHolder.setSponsor(drivelist.get(position).getSponsor());
        driveViewHolder.setNoofmemeber1(drivelist.get(position).getNoofmemeber1());
        driveViewHolder.setProfilepic(drivelist.get(position).getProfilepic());
        driveViewHolder.setabc(Postkey);
    }

    @Override
    public int getItemCount() {
        return drivelist.size();
    }

    class Drivevewholder extends RecyclerView.ViewHolder{
        Button Joindrive;
        TextView Memrequied,Drivestats2;
        public Drivevewholder(@NonNull View itemView) {
            super(itemView);
            Memrequied = (TextView) itemView.findViewById(R.id.memreq);
        }
        public  void setabc(final String PostKey)
        {
            Memref = FirebaseDatabase.getInstance().getReference().child("Members");
            Memref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).exists()) {
                       int  coutmem = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        Memrequied.setText(Integer.toString(coutmem) + " Members Joined");


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
          //  x = noofmemeber;

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
    }

}


