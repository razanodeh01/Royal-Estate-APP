/**
 * Description:
 * This activity serves as the main dashboard for admin users after successful login.
 * It features a navigation drawer that allows the admin to switch between various
 * management functionalities such as deleting customers, viewing statistics, managing
 * reservations, offering promotions, and registering new admins.
 */

package com.example.realestate;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;


public class AdminHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        drawerLayout = findViewById(R.id.drawer_layout2);
        toolbar = findViewById(R.id.toolbar2);
        navigationView = findViewById(R.id.nav_view2);
        setSupportActionBar(toolbar);

        TextView toolbarTitle = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                break;
            }
        }

        if (toolbarTitle != null) {
            Typeface customFont = ResourcesCompat.getFont(this, R.font.volkhov_bold);
            toolbarTitle.setTypeface(customFont);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setTitle("Royal Estate");
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_delete_customers) {
                fragment = new DeleteCustomersFragment();
            } else if (itemId == R.id.nav_statistics) {
                fragment = new StatisticsFragment();
            } else if (itemId == R.id.nav_add_admin) {
                Intent intent = new Intent(this, AdminRegisterActivity.class);
                intent.putExtra("user_type", "admin");
                startActivity(intent);
            } else if (itemId == R.id.nav_view_reservations) {
                fragment = new ViewReservationsFragment();
            } else if (itemId == R.id.nav_special_offers) {
                fragment = new SpecialOffersFragment();
            } else if (itemId == R.id.nav_logout) {
                startActivity(new Intent(this, MainActivity2.class));
                finish();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DeleteCustomersFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_delete_customers);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}