package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolBar;

    private ProgressDialog mLoginProgress;

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mLoginButton;

    private FirebaseAuth mAuth;

    private DatabaseReference mTokenIdDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mTokenIdDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmail = (TextInputLayout) findViewById(R.id.login_Email);
        mPassword = (TextInputLayout) findViewById(R.id.login_Password);
        mLoginButton = (Button) findViewById(R.id.login_Button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = mEmail.getEditText().getText().toString();
                String Password = mPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {

                    Toast.makeText(LoginActivity.this, "Can't Leave Any Fields Empty!", Toast.LENGTH_LONG).show();
                } else {
                    mLoginProgress.setTitle("Loging In");
                    mLoginProgress.setMessage("Please wait while we Log you In...");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    LoginUser(Email, Password);
                }
            }
        });


    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mLoginProgress.dismiss();

                    String Current_User_Id = FirebaseAuth.getInstance().getUid();
                    String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                    mTokenIdDatabase.child(Current_User_Id).child("device_token").setValue(DeviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(MainIntent);
                            finish();
                        }
                    });


                } else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }

        });
    }
}

