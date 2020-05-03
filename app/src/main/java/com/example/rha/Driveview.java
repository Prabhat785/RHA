package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Driveview extends AppCompatActivity {

    private RecyclerView memberlist;
    private DatabaseReference memref;
    String PostKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveview);
        memberlist = (RecyclerView) findViewById(R.id.memberslist) ;
        memberlist.setHasFixedSize(true);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        memberlist.setLayoutManager(linearLayoutManager);

        Displaymembers();

    }

    private void Displaymembers()
    {
        FirebaseRecyclerAdapter<Memberlist , MembersviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Memberlist, MembersviewHolder>
                (
                   Memberlist.class,
                    R.layout.memberslist,
                    MembersviewHolder.class,
                    memref
                ) {
            @Override
            protected void populateViewHolder(MembersviewHolder membersviewHolder, Memberlist memberlist, int i)
            {
            membersviewHolder.setProfile1(getApplicationContext(),memberlist.getProfile1());
            membersviewHolder.setName1(memberlist.getName1());
            membersviewHolder.setUsername1(memberlist.getUsername1());
            membersviewHolder.setAddress1(memberlist.getAddress1());
            membersviewHolder.setPhoneno1(memberlist.getPhoneno1());
            }
        };
        memberlist.setAdapter(firebaseRecyclerAdapter);
    }
    public  static class MembersviewHolder extends RecyclerView.ViewHolder
    {
        View  mview;
        public MembersviewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }
        public void setName1(String name) {
            TextView nm = mview.findViewById(R.id.member_name);
            nm.setText("NAME : "+name);
        }
        public void setUsername1(String username) {
            TextView un = mview.findViewById(R.id.memberuser_name);
            un.setText(username);
        }
        public void setAddress1(String address) {
            TextView add = mview.findViewById(R.id.member_adress);
            add.setText("ADDRESS : "+address);
        }
        public void setPhoneno1(String phoneno) {
            TextView ph = mview.findViewById(R.id.member_phoneno);
            ph.setText("PHONENO : "+phoneno);
        }
        public void setProfile1(Context applicationContext, String profile) {
            CircularImageView pi = mview.findViewById(R.id.memberprofile_image);
            Picasso.get().load(profile).into(pi);
        }

    }
}
