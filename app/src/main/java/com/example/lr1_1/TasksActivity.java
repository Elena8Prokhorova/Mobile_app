package com.example.lr1_1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class TasksActivity extends AppCompatActivity {
    private FragmentManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        manager = getSupportFragmentManager();

        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new TabAdapter(manager));
        pager.setPageTransformer(false, new ZoomOutPageTransformer());

        TabLayout tab = findViewById(R.id.tabs);
        tab.setupWithViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);
    }

}
