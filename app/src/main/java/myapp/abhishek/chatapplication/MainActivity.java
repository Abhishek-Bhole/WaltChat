package myapp.abhishek.chatapplication;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolBar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(currentUser!=null) {


        mViewPager = (ViewPager)findViewById(R.id.main_TabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_Tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mToolBar = (android.support.v7.widget.Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Walt Chat");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
            SendToStart();
        }

        else
        {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {

            //mUserRef.child("online").setValue(false);
            mUserRef.child("LastSeen").setValue(ServerValue.TIMESTAMP);

        }
    }

    

    @Override
    protected void onRestart() {
        super.onRestart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {

            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {

            mUserRef.child("online").setValue(true);
        }
    }

    private void SendToStart() {

        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_LogOutButton)
        {
            FirebaseAuth.getInstance().signOut();
            SendToStart();
        }

        if(item.getItemId() == R.id.main_SettingsButton)
        {
            Intent SettingsIntent = new Intent(MainActivity.this,AccountSettingActivity.class);
            startActivity(SettingsIntent);
        }

        if(item.getItemId()==R.id.main_AllUsersButton)
        {
            Intent AllUsersIntent = new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(AllUsersIntent);
        }

        return true;


    }
}
