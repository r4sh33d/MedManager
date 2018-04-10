package com.r4sh33d.medmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.r4sh33d.medmanager.monthlymedications.MonthlyMedicationsFragment;
import com.r4sh33d.medmanager.R;
import com.r4sh33d.medmanager.activemedications.ActiveMedicationsFragment;
import com.r4sh33d.medmanager.addmedication.AddMedicationFragment;
import com.r4sh33d.medmanager.models.Medication;
import com.r4sh33d.medmanager.utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            setNavigationViewHeaderDetails(user);
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
        Intent openingIntent = getIntent();
        if (openingIntent.getAction() != null &&
                openingIntent.getAction().equalsIgnoreCase(Constants.ACTION_NAVIGATE_TO_ADD_MEDICATION)) {
            //We could open this activity, just to edit Medication, from SearchActivity.
            Medication medication = openingIntent.getParcelableExtra(Constants.KEY_MEDICATION_BUNDLE);
            navigateToFragment(AddMedicationFragment.newInstance(medication));
        }else {
            //coming from the launcher
            navigateToFragment(new ActiveMedicationsFragment());
        }
    }

    void setNavigationViewHeaderDetails(FirebaseUser user) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView userProfilePic = headerView.findViewById(R.id.user_profile_pic);
        TextView userDisplayName = headerView.findViewById(R.id.user_display_name);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        userDisplayName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(userProfilePic);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_acitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this , SearchableActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.nav_active_med_list:
                navigateToFragment(new ActiveMedicationsFragment());
                break;
            case R.id.nav_medications_by_month:
                navigateToFragment(new MonthlyMedicationsFragment());
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}