package com.example.lr1_1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FragmentManager manager;
    public static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), REQUEST_CODE);
        }
        else {
            launchMenu();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    private void launchMenu() {
        manager = getSupportFragmentManager();
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new TabAdapter(manager));
        pager.setPageTransformer(false, new ZoomOutPageTransformer());
        pager.setOffscreenPageLimit(1);

        TabLayout tab = findViewById(R.id.tabs);
        tab.setupWithViewPager(pager);
    }

}