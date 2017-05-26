package com.example.anne.otp_android_client_v3;

import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by Anne on 5/24/2017.
 */

public class MyOnNavigationItemSelectedListener
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavView;

    public MyOnNavigationItemSelectedListener(DrawerLayout dl, NavigationView navView) {
        mDrawerLayout = dl;
        mNavView = navView;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_planner) {

        } else if (id == R.id.nav_settings) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}