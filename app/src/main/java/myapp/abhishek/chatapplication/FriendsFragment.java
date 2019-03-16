package myapp.abhishek.chatapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_User_Id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_User_Id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_User_Id);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> FriendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());

                final String list_User_Id = getRef(position).getKey();

                mUsersDatabase.child(list_User_Id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String Username = dataSnapshot.child("name").getValue().toString();
                        final String Thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                        String Online = dataSnapshot.child("online").getValue().toString();


                        viewHolder.setName(Username);
                        viewHolder.setDisplayImage(Thumbnail);
                        viewHolder.setUserOnline(Online);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new  CharSequence[]{"Open Profile","Send a message"};

                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        if(which==0)
                                        {
                                            Intent ProfileIntent = new Intent(getContext(),UserProfileActivity.class);
                                            ProfileIntent.putExtra("User_Key",list_User_Id);
                                            startActivity(ProfileIntent);
                                        }

                                        if(which==1)
                                        {
                                            Intent ChatIntent = new Intent(getContext(),ChatActivity.class);
                                            ChatIntent.putExtra("User_Key",list_User_Id);
                                            ChatIntent.putExtra("User_Name",Username);
                                            ChatIntent.putExtra("User_Thumbnail",Thumbnail);
                                            startActivity(ChatIntent);
                                        }


                                    }
                                });

                                alert.show();

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(FriendsRecyclerViewAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_SingleStatus);
            userStatusView.setText("Became friends on " + date);
        }

        public void setName(String name) {
            TextView UserNameView = (TextView) mView.findViewById(R.id.user_SignalName);
            UserNameView.setText(name);
        }

        public void setDisplayImage(final String image) {
            final CircleImageView UserDisplayPicture = (CircleImageView) mView.findViewById(R.id.user_SingleImage);

            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image).into(UserDisplayPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    Picasso.get().load(image).placeholder(R.drawable.no_image).into(UserDisplayPicture);
                }
            });


        }

        public void setUserOnline(String online) {
            CircleImageView UserOnline = (CircleImageView) mView.findViewById(R.id.users_SingleOnline);
            if (online.equals("true"))
            {
                UserOnline.setVisibility(View.VISIBLE);

            }

            else
            {
                UserOnline.setVisibility(View.INVISIBLE);
            }
        }
    }
}

