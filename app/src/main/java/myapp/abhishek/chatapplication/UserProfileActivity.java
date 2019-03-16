package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView mDisplayName;
    private TextView mStatus;
    private TextView mTotalFriends;
    private CircleImageView mDisplayPicture;
    private Button mSendFriendRequest,mDeclineFriendRequest;

    private DatabaseReference mDatabse;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabse;
    private FirebaseUser mCurrent_User;

    private ProgressDialog mUserProfileProgress;

    String FriendsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FriendsStatus="not_friends";
        mUserProfileProgress = new ProgressDialog(UserProfileActivity.this);
        mUserProfileProgress.setTitle("Loading User Profile");
        mUserProfileProgress.setMessage("Please Wait While We Load The User Profile...");
        mUserProfileProgress.setCanceledOnTouchOutside(false);
        mUserProfileProgress.show();

        setContentView(R.layout.activity_user_profile);

        mDisplayName = (TextView)findViewById(R.id.profile_DisplayName);
        mStatus = (TextView)findViewById(R.id.profile_Status);
        mTotalFriends = (TextView)findViewById(R.id.profile_TotalFriends);
        mDisplayPicture =(CircleImageView) findViewById(R.id.profile_Image);
        mSendFriendRequest = (Button)findViewById(R.id.profile_SendFriendRequest);
        mDeclineFriendRequest = (Button)findViewById(R.id.profile_DeclineFriendRequest);
        mDeclineFriendRequest.setVisibility(View.INVISIBLE);
        mDeclineFriendRequest.setEnabled(false);




        final String User_ID = getIntent().getStringExtra("User_Key");
        mDeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendRequestDatabase.child(mCurrent_User.getUid()).child(User_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mFriendRequestDatabase.child(User_ID).child(mCurrent_User.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(UserProfileActivity.this,"Request Declined",Toast.LENGTH_SHORT).show();
                                        FriendsStatus = "not_friends";
                                        mSendFriendRequest.setText("SEND FRIEND REQUEST");
                                        mDeclineFriendRequest.setEnabled(true);
                                        mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                        mDeclineFriendRequest.setEnabled(false);

                                    }

                                    else
                                    {
                                        Toast.makeText(UserProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        mDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(User_ID);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabse = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mFriendsDatabase.child(User_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long TotalFriends = dataSnapshot.getChildrenCount();
                String total = Long.toString(TotalFriends);
                mTotalFriends.setText(total+" Friends");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayName.setText(name);
                mStatus.setText(status);

                if(image.equals(User_ID))
                {
                    mUserProfileProgress.dismiss();
                }
                else
                {

                    Picasso.get().load(image).placeholder(R.drawable.no_image).into(mDisplayPicture);

                    mFriendRequestDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(User_ID))
                            {
                                String RequestType = dataSnapshot.child(User_ID).child("Request_Type").getValue().toString();

                                if(RequestType.equals("Received"))
                                {
                                    FriendsStatus = "req_received";
                                    mSendFriendRequest.setText("ACCEPT FRIEND REQUEST");
                                    mDeclineFriendRequest.setEnabled(true);
                                    mDeclineFriendRequest.setVisibility(View.VISIBLE);
                                }

                                else if(RequestType.equals("Sent"))
                                {
                                    FriendsStatus="req_sent";



                                    mSendFriendRequest.setText("CANCEL FRIEND REQUEST");
                                    mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    mDeclineFriendRequest.setEnabled(false);
                                }

                                else
                                {
                                    mSendFriendRequest.setText("UNFRIEND");
                                    mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    mDeclineFriendRequest.setEnabled(false);
                                    FriendsStatus="friends";
                                }
                                mUserProfileProgress.dismiss();
                            }

                            else
                            {
                                mFriendsDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(User_ID))
                                        {
                                            FriendsStatus = "friends";
                                            mSendFriendRequest.setText("UNFRIEND");
                                            mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                            mDeclineFriendRequest.setEnabled(false);
                                        }
                                        mUserProfileProgress.dismiss();


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                        mUserProfileProgress.dismiss();

                                    }
                                });
                            }

                            mUserProfileProgress.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSendFriendRequest.setEnabled(false);

                if(FriendsStatus.equals("not_friends"))
                {
                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(User_ID).child("Request_Type").setValue("Sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        mFriendRequestDatabase.child(User_ID).child(mCurrent_User.getUid()).child("Request_Type").setValue("Received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                HashMap<String,String> NotificationHashMap = new HashMap<>();
                                                NotificationHashMap.put("From",mCurrent_User.getUid());
                                                NotificationHashMap.put("Type","Request");



                                                mNotificationDatabse.child(User_ID).push().setValue(NotificationHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mSendFriendRequest.setEnabled(true);
                                                        FriendsStatus = "req_sent";
                                                        mSendFriendRequest.setText("CANCEL FRIEND REQUEST");
                                                        Toast.makeText(UserProfileActivity.this,"Friend Request Sent",Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                            }
                                        });

                                    }

                                    else
                                    {
                                        Toast.makeText(UserProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

                if(FriendsStatus.equals("req_sent"))
                {
                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(User_ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(User_ID).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mSendFriendRequest.setEnabled(true);
                                    FriendsStatus = "not_friends";
                                    mSendFriendRequest.setText("SEND FRIEND REQUEST");

                                }
                            });
                        }
                    });
                }


                if(FriendsStatus.equals("req_received"))
                {
                    final String CurrentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDatabase.child(mCurrent_User.getUid()).child(User_ID).child("date").setValue(CurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendsDatabase.child(User_ID).child(mCurrent_User.getUid()).child("date").setValue(CurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(mCurrent_User.getUid()).child(User_ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(User_ID).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mSendFriendRequest.setEnabled(true);
                                                    FriendsStatus = "friends";
                                                    mSendFriendRequest.setText("UNFRIEND");
                                                    mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                    mDeclineFriendRequest.setEnabled(false);

                                                }
                                            });
                                        }
                                    });

                                }
                            });

                        }
                    });
                }

                if(FriendsStatus.equals("friends"))
                {
                    mFriendsDatabase.child(mCurrent_User.getUid()).child(User_ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(User_ID).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mSendFriendRequest.setEnabled(true);
                                    FriendsStatus = "not_friends";
                                    mSendFriendRequest.setText("SEND FRIEND REQUEST");
                                }
                            });
                        }
                    });
                }



            }
        });

    }
}
