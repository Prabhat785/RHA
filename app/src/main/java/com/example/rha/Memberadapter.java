package com.example.rha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Memberadapter extends FirebaseRecyclerAdapter<Memberlist, Memberadapter.Memeberviewholder>
{
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Memberadapter(@NonNull FirebaseRecyclerOptions<Memberlist> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Memeberviewholder membersviewHolder, int position, @NonNull Memberlist memberlist) {
        membersviewHolder.setProfile1(memberlist.getProfile1());
        membersviewHolder.setName1(memberlist.getName1());
        membersviewHolder.setUsername1(memberlist.getUsername1());
        membersviewHolder.setAddress1(memberlist.getAddress1());
        membersviewHolder.setPhoneno1(memberlist.getPhoneno1());

    }

    @NonNull
    @Override
    public Memeberviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memberslist, parent, false);

        return new Memeberviewholder(view);
    }

    class Memeberviewholder extends RecyclerView.ViewHolder{

        public Memeberviewholder(@NonNull View itemView) {
            super(itemView);
        }
        public void setName1(String name) {
            TextView nm = itemView.findViewById(R.id.member_name);
            nm.setText("NAME : "+name);
        }
        public void setUsername1(String username) {
            TextView un = itemView.findViewById(R.id.memberuser_name);
            un.setText(username);
        }
        public void setAddress1(String address) {
            TextView add = itemView.findViewById(R.id.member_adress);
            add.setText("ADDRESS : "+address);
        }
        public void setPhoneno1(String phoneno) {
            TextView ph = itemView.findViewById(R.id.member_phoneno);
            ph.setText("PHONENO : "+phoneno);
        }
        public void setProfile1(String profile) {
            CircularImageView pi = itemView.findViewById(R.id.memberprofile_image);
            Picasso.get().load(profile).into(pi);
        }
    }
}
