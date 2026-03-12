package com.pdks.mobile.personel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pdks.mobile.R;
import com.pdks.mobile.util.ViewUtils;

public class LeaveRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);

        // Toolbar
        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText("İzin Talebi");

        TabLayout tabLayout = findViewById(R.id.tabLeave);
        ViewPager2 viewPager = findViewById(R.id.vpLeave);
        ViewUtils.applyStatusBarPadding(this);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new LeaveFormFragment() : new LeaveHistoryFragment();
            }

            @Override
            public int getItemCount() { return 2; }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> {
            tab.setText(pos == 0 ? "Yeni Talep" : "Geçmiş Talepler");
        }).attach();
    }
}