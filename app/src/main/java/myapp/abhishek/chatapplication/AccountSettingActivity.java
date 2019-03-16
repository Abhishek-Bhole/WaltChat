package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettingActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseUser mCurrent_User;

    private CircleImageView mImage;
    private TextView mDisplayName;
    private TextView mStatus;
    private Button ChangeStatusButton;
    private Button ChangeImageButton;

    private static final int GALLERY_PICK=1;

    private StorageReference mImageStorage;

    private ProgressDialog mUploadProgress;

    private ProgressDialog mShowSettingsProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowSettingsProgress = new ProgressDialog(AccountSettingActivity.this);
        mShowSettingsProgress.setTitle("Loading Account Settings");
        mShowSettingsProgress.setMessage("Please Wait While We Load Your Settings");
        mShowSettingsProgress.setCanceledOnTouchOutside(false);
        mShowSettingsProgress.show();
        setContentView(R.layout.activity_account_setting);
        mImage = (CircleImageView) findViewById(R.id.settings_Image);
        mDisplayName= (TextView) findViewById(R.id.setting_DisplayName);
        mStatus =(TextView) findViewById(R.id.setting_Status);
        ChangeStatusButton = (Button) findViewById(R.id.setting_ChangeStatusButton);
        ChangeImageButton = (Button) findViewById(R.id.setting_ChangeImageButton);

        mImageStorage = FirebaseStorage.getInstance().getReference();



        ChangeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CurrentStatus = mStatus.getText().toString();

                Intent ChangeStatusIntent = new Intent(AccountSettingActivity.this,StatusActivity.class);
                ChangeStatusIntent.putExtra("CurrentStatus",CurrentStatus);
                startActivity(ChangeStatusIntent);

            }
        });

        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        final String U_Id = mCurrent_User.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(U_Id);
        database.keepSynced(true);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                mDisplayName.setText(name);
                mStatus.setText(status);

                if(image.equals(U_Id))
                {
                    mShowSettingsProgress.dismiss();
                }
                else
                {

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
                    mShowSettingsProgress.dismiss();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ChangeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent = new Intent();
                GalleryIntent.setType("image/*");
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(GalleryIntent,"SELECT IMAGE"),GALLERY_PICK);
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(AccountSettingActivity.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {

            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mUploadProgress = new ProgressDialog(AccountSettingActivity.this);
                mUploadProgress.setTitle("Uploading Image");
                mUploadProgress.setMessage("Please Wait While We Update Your Profile Picture...");
                mUploadProgress.setCanceledOnTouchOutside(false);
                mUploadProgress.show();

                Uri resultUri = result.getUri();

                File thumbFilePath = new File(resultUri.getPath());

                String U_ID = mCurrent_User.getUid();

                Bitmap ThumbBitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumbFilePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ThumbBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] ThumbByte = baos.toByteArray();

                StorageReference FilePath = mImageStorage.child("Profile_Images").child(U_ID+".jpeg");
                final StorageReference ThumbFilePath = mImageStorage.child("Profile_Images").child("Thumbnails").child(U_ID+".jpeg");

                FilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {

                            final String DownloadURL = task.getResult().getDownloadUrl().toString();

                            UploadTask Upload_Task = ThumbFilePath.putBytes(ThumbByte);
                            Upload_Task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_DownloadURL = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful())
                                    {

                                        Map updateHashMap = new HashMap();
                                        updateHashMap.put("image",DownloadURL);
                                        updateHashMap.put("thumbnail",thumb_DownloadURL);

                                        database.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    mUploadProgress.dismiss();
                                                    Toast.makeText(AccountSettingActivity.this,"Upload Successful!",Toast.LENGTH_LONG).show();

                                                }

                                                else
                                                {
                                                    Toast.makeText(AccountSettingActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                    mUploadProgress.dismiss();
                                                }
                                            }
                                        });
                                    }

                                    else
                                    {
                                        Toast.makeText(AccountSettingActivity.this,thumb_task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            });



                        }


                    }
                });

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
