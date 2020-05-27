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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {
    private ImageButton postcommment;
    private EditText commentinput;
    private RecyclerView Commentlist1;
    private DatabaseReference userref,postref;
    private String currenuserid,PostKey;
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
        userref= FirebaseDatabase.getInstance().getReference().child("User");
        postref=FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey).child("Comments");
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

    private void Validatecomment(String name) {
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
                  }
                  else
                  {
                      Toast.makeText(CommentActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                  }
              }
          });
      }

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
