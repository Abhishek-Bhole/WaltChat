package myapp.abhishek.chatapplication;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;

    private DatabaseReference mChatsDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_User_ID;

    private View mMainView;

    private DatabaseReference mUsersDatabase;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_chats, container, false);

        mChatList = (RecyclerView) mMainView.findViewById(R.id.chat_list);
        mAuth= FirebaseAuth.getInstance();

        mCurrent_User_ID = mAuth.getCurrentUser().getUid();

        mChatsDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_User_ID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats,ChatsViewHolder> ChatsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(

                Chats.class,
                R.layout.users_single_layout,
                ChatsViewHolder.class,
                mChatsDatabase

        ) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {



                final String Chat_User_ID = getRef(position).getKey();

                mUsersDatabase.child(Chat_User_ID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String DisplayName = dataSnapshot.child("name").getValue().toString();
                        String DisplayStatus = dataSnapshot.child("status").getValue().toString();
                        final String DisplayImage = dataSnapshot.child("thumbnail").getValue().toString();

                        viewHolder.setDisplayName(DisplayName);
                        viewHolder.setDisplayImage(DisplayImage);
                        viewHolder.setDisplayStatus(DisplayStatus);


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ChatIntent = new Intent(getContext(),ChatActivity.class);
                                ChatIntent.putExtra("User_Key",Chat_User_ID);
                                ChatIntent.putExtra("User_Name",DisplayName);
                                ChatIntent.putExtra("User_Thumbnail",DisplayImage);
                                startActivity(ChatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mChatList.setAdapter(ChatsRecyclerViewAdapter);
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public ChatsViewHolder(View itemView) {
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
            mDisplayStatus.setText(status);
        }
    }
}
