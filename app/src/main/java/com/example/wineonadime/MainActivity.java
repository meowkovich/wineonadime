package com.example.wineonadime;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    SharedPreferences userLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userLog = getSharedPreferences("wineOnADimeLogin", Context.MODE_PRIVATE);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
      //  hideBottomBar(false);

        openFragment(LoginFragment.newInstance("",""));
    }

    public void hideBottomBar(boolean isHidden){
        bottomNavigation.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_guide:
                            openFragment(GuideFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_map:
                            openFragment(MapFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_search:
                            openFragment(SearchFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_profile:
                            openFragment(ProfileFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

    public void signUp(View view) {
        openFragment(RegisterFragment.newInstance("",""));

    }

    public void login(View view) {
        EditText username_text = findViewById(R.id.username);
        EditText password_text = findViewById(R.id.password);
        TextView error_message = findViewById(R.id.loginInfoText);

        String username = username_text.getText().toString();
        String password = password_text.getText().toString();

        if(userLog.contains(username)) {
            if(userLog.getString(username,"").equals(password)) {
                openFragment(HomeFragment.newInstance("", ""));
            }
            else {
                error_message.setText(getResources().getString(R.string.incorrectPassword));
                error_message.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        else{
            error_message.setText(getResources().getString(R.string.usernameDoesNotExist));
            error_message.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void continueAsGuest(View view) {

        openFragment(MapFragment.newInstance("", ""));
    }

    public void register(View view) {
        EditText username_text = findViewById(R.id.username_register);
        EditText email_text = findViewById(R.id.email_register);
        EditText password_text = findViewById(R.id.password_register);
        TextView error_message = findViewById(R.id.registerInfoText);

        String username = username_text.getText().toString();
        String email = email_text.getText().toString();
        String password = password_text.getText().toString();

        if(userLog.contains(username)) {
            error_message.setText(getResources().getString(R.string.usernameExists));
            error_message.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            if (password.length() < 8) {

            }
            else{

            }

        }
        openFragment(LoginFragment.newInstance("",""));

    }
}
