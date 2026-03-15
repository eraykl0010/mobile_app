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

public class AdvanceRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_request);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.title_advance_request));

        TabLayout tabLayout = findViewById(R.id.tabAdvance);
        ViewPager2 viewPager = findViewById(R.id.vpAdvance);
        ViewUtils.applyStatusBarPadding(this);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new AdvanceFormFragment() : new AdvanceHistoryFragment();
            }

            @Override
            public int getItemCount() { return 2; }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> {
            tab.setText(pos == 0 ? getString(R.string.tab_new_request) : getString(R.string.tab_history));
        }).attach();
    }
}