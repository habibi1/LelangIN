package com.tugasmobile.lelangin;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tugasmobile.lelangin.fragment.AddFragment;
import com.tugasmobile.lelangin.fragment.DaftarLelangFragment;
import com.tugasmobile.lelangin.fragment.FavoriteFragment;
import com.tugasmobile.lelangin.fragment.MoneyFragment;
import com.tugasmobile.lelangin.fragment.ProfileFragment;
import com.tugasmobile.lelangin.model.User;

public class HomeActivity extends AppCompatActivity implements
        DaftarLelangFragment.OnFragmentInteractionListener,
        FavoriteFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        AddFragment.OnFragmentInteractionListener,
        MoneyFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private ActionBar toolbar;
    public String TAG = "" ,nameOwner="Loading name...", emailOwner="Loading email...", imageOwner, saldoOwner="Loading saldo...";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle("Daftar Lelang");
                    fragment = new DaftarLelangFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    toolbar.setTitle("Favorite");
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_add:
                    toolbar.setTitle("Add");
                    fragment = new AddFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_money:
                    toolbar.setTitle("E-Money");
                    fragment = new MoneyFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = getSupportActionBar();

        if (savedInstanceState == null) {
            toolbar.setTitle("Daftar Lelang");
            DaftarLelangFragment daftarLelangFragment = new DaftarLelangFragment();
            loadFragment(daftarLelangFragment);
        }

        FirebaseUser key_user = FirebaseAuth.getInstance().getCurrentUser();
        String tes = key_user.getUid();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                nameOwner = user.getName();
                emailOwner = user.getEmail();
                imageOwner = user.getPhoto();
                saldoOwner = user.getSaldo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        FirebaseDatabase.getInstance().getReference("users").child(tes).addValueEventListener(postListener);

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
