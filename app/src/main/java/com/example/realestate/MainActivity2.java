/**
 * Description:
 * MainActivity2 is the login gateway that offers users and admins a tabbed interface to access their respective login screens.
 * It acts as a switchboard for authentication between the two roles.
 */

package com.example.realestate;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        LoginAdapter adapter = new LoginAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TextView customTab = new TextView(this);
            customTab.setText(position == 0 ? "User" : "Admin");
            customTab.setTextSize(18);
            customTab.setTypeface(ResourcesCompat.getFont(this, R.font.volkhov_bold));
            customTab.setTextColor(ContextCompat.getColor(this, R.color.black));
            customTab.setGravity(Gravity.CENTER);
            customTab.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            tab.setCustomView(customTab);
        }).attach();
    }
}