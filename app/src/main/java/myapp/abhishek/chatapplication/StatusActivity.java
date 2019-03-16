package myapp.abhishek.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolBar;

    private DatabaseReference database;
    private FirebaseAuth mAuth;

    private TextInputLayout mStatus;
    private Button SaveStatusButton;

    private ProgressDialog mStatusProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String CurrentStatus = getIntent().getStringExtra("CurrentStatus");

        mAuth = FirebaseAuth.getInstance();


        mStatus=(TextInputLayout)findViewById(R.id.status_Input);
        SaveStatusButton = (Button)findViewById(R.id.status_SaveStatus);

        mStatus.getEditText().setText(CurrentStatus);

        SaveStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStatusProgress = new ProgressDialog(StatusActivity.this);
                mStatusProgress.setTitle("Saving Status");
                mStatusProgress.setMessage("Please Wait While We're Saving Your Status");
                mStatusProgress.show();

                String status = mStatus.getEditText().getText().toString();
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String U_ID = current_user.getUid();
                database = FirebaseDatabase.getInstance().getReference().child("Users").child(U_ID);
                database.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            mStatusProgress.dismiss();

                        }

                        else
                        {
                            mStatusProgress.hide();
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        mToolBar = (android.support.v7.widget.Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
