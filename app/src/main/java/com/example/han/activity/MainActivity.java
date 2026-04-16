package com.example.han.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.han.R;
import com.example.han.fragment.HomeFragment;
import com.example.han.fragment.ProfileFragment;
import com.example.han.network.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = TokenManager.getInstance(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelect);

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, homeFragment, "home")
                    .commit();
            currentFragment = homeFragment;
        }
    }

    private boolean onNavItemSelect(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();
        Fragment target = null;
        String tag = null;

        if (itemId == R.id.nav_home) {
            if (homeFragment == null) homeFragment = new HomeFragment();
            target = homeFragment;
            tag = "home";
        } else if (itemId == R.id.nav_publish) {
            startActivity(new Intent(this, CreatePostActivity.class));
            return true;
        } else if (itemId == R.id.nav_profile) {
            if (profileFragment == null) profileFragment = new ProfileFragment();
            target = profileFragment;
            tag = "profile";
        }

        if (target != null && target != currentFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, target, tag)
                    .commit();
            currentFragment = target;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // Exit app on back press from home
        moveTaskToBack(true);
    }
}
