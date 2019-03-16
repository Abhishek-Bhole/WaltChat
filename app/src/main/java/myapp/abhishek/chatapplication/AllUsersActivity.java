package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mAllUsersProgress;

    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue(true);



        mAllUsersProgress = new ProgressDialog(AllUsersActivity.this);
        mAllUsersProgress.setTitle("Refreshing All Users");
        mAllUsersProgress.setMessage("Please Wait A While....");
        mAllUsersProgress.setCanceledOnTouchOutside(false);
        mAllUsersProgress.show();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);



        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.user_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mUsersList = (RecyclerView) findViewById(R.id.users_List);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {


        super.onStart();
        mUserRef.child("online").setValue(true);



        FirebaseRecyclerAdapter<AllUsers, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, UsersViewHolder>(
                AllUsers.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {


            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, AllUsers model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getThumbnail());
                mAllUsersProgress.dismiss();

                final String User_Key = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ProfileIntent = new Intent(AllUsersActivity.this,UserProfileActivity.class);
                        ProfileIntent.putExtra("User_Key",User_Key);
                        startActivity(ProfileIntent);

                    }
                });

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserRef.child("online").setValue(true);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        FirebaseUser CurrentUser;


        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        }


        public void setName(String name)
        {

            TextView mUserName = (TextView) mView.findViewById(R.id.user_SignalName);
            mUserName.setText(name);


        }

        public  void setStatus(String status)
        {
            TextView mStatus = (TextView) mView.findViewById(R.id.user_SingleStatus);
            mStatus.setText(status);
        }

        public void setImage(final String image)
        {
            final CircleImageView mImage = (CircleImageView) mView.findViewById(R.id.user_SingleImage);

            String U_ID = CurrentUser.getUid();

            if(U_ID.equals(image))
            {

            }
            else {

                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.no_image).into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(image).placeholder(R.drawable.no_image).into(mImage);
                    }
                });
            }
        }


    }
}
