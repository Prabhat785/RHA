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


public class Postadapter extends RecyclerView.Adapter<Postadapter.Postvewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param
     */
    private List<Postlist> postlist = new ArrayList<>();
    private Context mContext;
    private DatabaseReference Likeref;
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
        final String Postkey  = postlist.get(position).getKey1();
        driveViewHolder.setDate1(postlist.get(position).getDate1());
        driveViewHolder.setName1(postlist.get(position).getName1());
        driveViewHolder.setTime1(postlist.get(position).getTime1());
        driveViewHolder.setDescription1(postlist.get(position).getDescription1());
        driveViewHolder.setPostimg1(postlist.get(position).getPostimg1());
        driveViewHolder.setProfilepic1(postlist.get(position).getProfilepic1());
        driveViewHolder.setabc(Postkey);
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
    public static void startActivity(Context context,final  String Postkey)
    {
        Intent intent = new Intent(context,CommentActivity.class);
        intent.putExtra("Postkey",Postkey);
        context.startActivity(intent);
    }
}



