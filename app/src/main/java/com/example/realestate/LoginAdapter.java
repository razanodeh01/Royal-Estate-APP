/**
 * Description:
 * The LoginAdapter class is a custom adapter used with ViewPager2 to manage the login interface
 * for both users and admins in a swipeable tab layout.
 */

package com.example.realestate;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class LoginAdapter extends FragmentStateAdapter {

    public LoginAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new UserFragment() : new AdminFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}