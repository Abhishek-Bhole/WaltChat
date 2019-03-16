package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button RegisterUser;

    private DatabaseReference database;

    private ProgressDialog mRegProgress;


    private FirebaseAuth mAuth;

    private android.support.v7.widget.Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mToolBar = (android.support.v7.widget.Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayName = (TextInputLayout)findViewById(R.id.reg_DisplayName);
        mEmail = (TextInputLayout)findViewById(R.id.reg_Email);
        mPassword = (TextInputLayout)findViewById(R.id.reg_Password);
        RegisterUser = (Button)findViewById(R.id.reg_RegisterUser);

        RegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DisplayName = mDisplayName.getEditText().getText().toString();
                String Email = mEmail.getEditText().getText().toString();
                String Password = mPassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(DisplayName) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password))
                {

                    Toast.makeText(RegisterActivity.this,"Can't Leave Any Fields Empty!",Toast.LENGTH_LONG).show();
                }

                else
                {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account...");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    RegisterUser(DisplayName,Email,Password);
                }

            }
        });

    }

    private void RegisterUser(final String displayName, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String U_ID = current_user.getUid();
                    String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                    database = FirebaseDatabase.getInstance().getReference().child("Users").child(U_ID);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name", displayName);
                    userMap.put("status","Hi There,I'm Using The Walt Chat.");
                    userMap.put("image",U_ID);
                    userMap.put("thumbnail",U_ID);
                    userMap.put("device_token",DeviceToken);

                    database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mRegProgress.dismiss();

                                Intent MainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(MainIntent);
                                finish();
                            }
                        }
                    });



                }

                else
                {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
        }

    });

    }
}
