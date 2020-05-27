package com.example.rha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class commentadapter extends FirebaseRecyclerAdapter <Commentlist,commentadapter.Commentviewholder>
{
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public commentadapter(@NonNull FirebaseRecyclerOptions <Commentlist> options) {
        super(options);
    }


    @NonNull
    @Override
    public Commentviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.all_comments_layout,parent,false);

        return new Commentviewholder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull Commentviewholder commentviewholder, int position, @NonNull Commentlist commentlist) {
        commentviewholder.setName1(commentlist.getName1());
        commentviewholder.setComment1(commentlist.getComment1());
        commentviewholder.setDate1(commentlist.getDate1());
        commentviewholder.setTime1(commentlist.getTime1());
    }

    class Commentviewholder extends RecyclerView.ViewHolder{

        public Commentviewholder(@NonNull View itemView) {
            super(itemView);
        }
        public void setName1(String name) {
            TextView n =itemView.findViewById(R.id.cmntusername);
            n.setText(name);
        }
        public void setTime1(String time) {
            TextView tm=itemView.findViewById(R.id.cmnttime);
                    tm.setText(time);
        }
        public void setDate1(String date) {
            TextView dt=itemView.findViewById(R.id.cmntdate);
            dt.setText(date);
        }
        public void setComment1(String comment) {
            TextView cmt=itemView.findViewById(R.id.comment);
            cmt.setText(comment);
        }
    }
}
