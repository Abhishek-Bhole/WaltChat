package myapp.abhishek.chatapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Requests extends Fragment {

    private RecyclerView mRequestList;

    private DatabaseReference mRequestDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_User_ID;

    private View mMainView;

    private DatabaseReference mUsersDatabase;


    public Requests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView= inflater.inflate(R.layout.fragment_chats, container, false);

        mRequestList = (RecyclerView) mMainView.findViewById(R.id.chat_list);
        mAuth= FirebaseAuth.getInstance();

        mCurrent_User_ID = mAuth.getCurrentUser().getUid();

        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(mCurrent_User_ID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<RequestsClass,RequestsViewHolder> RequestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<RequestsClass, RequestsViewHolder>(

                RequestsClass.class,
                R.layout.users_single_layout,
                RequestsViewHolder.class,
                mRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, RequestsClass model, int position) {

                viewHolder.setDisplayStatus(model.getRequest_Type());
                final String Chat_User_ID = getRef(position).getKey();

                mUsersDatabase.child(Chat_User_ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String DisplayName = dataSnapshot.child("name").getValue().toString();
                        String DisplayStatus = dataSnapshot.child("status").getValue().toString();
                        final String DisplayImage = dataSnapshot.child("thumbnail").getValue().toString();

                        viewHolder.setDisplayName(DisplayName);
                        viewHolder.setDisplayImage(DisplayImage);



                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ProfileIntent = new Intent(getContext(),UserProfileActivity.class);
                                ProfileIntent.putExtra("User_Key",Chat_User_ID);
                                startActivity(ProfileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mRequestList.setAdapter(RequestsRecyclerViewAdapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public RequestsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDisplayName(String name)
        {
            TextView mDisplayName = (TextView)mView.findViewById(R.id.user_SignalName);
            mDisplayName.setText(name);

        }

        public void setDisplayImage(final String image)
        {
            final CircleImageView mDisplayImage = (CircleImageView) mView.findViewById(R.id.user_SingleImage);
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image).into(mDisplayImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(image).placeholder(R.drawable.no_image).into(mDisplayImage);
                }
            });

        }

        public void setDisplayStatus(String status)
        {
            TextView mDisplayStatus = (TextView) mView.findViewById(R.id.user_SingleStatus);
            if(status.equals("Received"))
            {
                mDisplayStatus.setText("Accept or Decline Request");
            }

            if(status.equals("Sent"))
            {
                mDisplayStatus.setText("Request Sent");
            }

        }
    }
}
