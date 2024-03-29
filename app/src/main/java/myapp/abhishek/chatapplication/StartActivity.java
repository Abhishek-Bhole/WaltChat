package myapp.abhishek.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button RegisterButton;
    private Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RegisterButton = (Button) findViewById(R.id.start_reg_btn);
        LoginButton = (Button) findViewById(R.id.start_LoginButton);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });
    }
}
