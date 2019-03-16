package myapp.abhishek.chatapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

    private FirebaseAuth mAuth;

    private String mThumbnail;

    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;
        public RelativeLayout MyLayout;
        public ImageView MessageImage;

        View mView;

        public MessageViewHolder(View view)
        {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            MyLayout = (RelativeLayout) view.findViewById(R.id.message_layout);
            MessageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            mView = view;


        };

        public void setDisplayImage(String image) {

            final CircleImageView UserDisplayPicture = (CircleImageView) mView.findViewById(R.id.message_profile_layout);
            Picasso.get().load(image).placeholder(R.drawable.no_image).into(UserDisplayPicture);

        }

    }

    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();
        DatabaseReference UserDatabase;
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        UserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String Thumnail = dataSnapshot.child("thumbnail").getValue().toString();
            mThumbnail = Thumnail;
            viewHolder.setDisplayImage(Thumnail);
            Log.d("CHAT_THUMBNAIL",mThumbnail);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
        Picasso.get().load(c.getMessage()).into(viewHolder.MessageImage);
        if(message_type.equals("image"))
        {
            viewHolder.MessageImage.setVisibility(View.VISIBLE);
            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.MessageImage.setMaxHeight(10);
        }

        else
        {
            viewHolder.MessageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageText.setVisibility(View.VISIBLE);

        }
        if(from_user.equals(current_user_id))

        {
            viewHolder.MessageImage.layout(59,0,0,0);
            viewHolder.MyLayout.setGravity(Gravity.END);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background_white);
            viewHolder.messageText.setTextColor(Color.BLACK);
            viewHolder.profileImage.setVisibility(View.INVISIBLE);






        }

        else
        {
            viewHolder.MessageImage.layout(0,0,59,0);
            viewHolder.MyLayout.setHorizontalGravity(Gravity.START);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);
            Picasso.get().load(mThumbnail).placeholder(R.drawable.no_image).into(viewHolder.profileImage);
            viewHolder.profileImage.setVisibility(View.VISIBLE);



        }

        viewHolder.messageText.setText(c.getMessage());

    }

    public int getItemCount()
    {
        return mMessageList.size();
    }
}
