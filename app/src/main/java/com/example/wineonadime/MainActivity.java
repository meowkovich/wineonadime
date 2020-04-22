package com.example.wineonadime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    SharedPreferences userLog;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   userLog = getSharedPreferences("wineOnADimeLogin", Context.MODE_PRIVATE);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        mAuth = FirebaseAuth.getInstance();
      //  hideBottomBar(false);
        onStart();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            openFragment(LoginFragment.newInstance("",""));
        }
        else {
            //TODO
            openFragment(LoginFragment.newInstance("",""));
            updateUI(currentUser);
        }
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
        openFragment(RegisterFragment.newInstance());
    }

    public void login(View view) {
        EditText username_text = findViewById(R.id.email);
        EditText password_text = findViewById(R.id.password);
        TextView error_message = findViewById(R.id.loginInfoText);

        String email = username_text.getText().toString();
        String password = password_text.getText().toString();

        signInWithEmailAndPassword(email, password);

    }

    public void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void continueAsGuest(View view) {

      //  openFragment(HomeFragment.newInstance("", ""));
    }

    public void register(View view) {
        EditText email_text = findViewById(R.id.email_register);
        EditText password_text = findViewById(R.id.password_register);
        TextView error_message = findViewById(R.id.registerInfoText);

        String email = email_text.getText().toString();
        String password = password_text.getText().toString();

        createAccount(email, password);
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        //TODO have some way to have the favorites and profile set
        if (user == null) {
            //there is no user
            //openFragment(HomeFragment.newInstance("", ""));
        }
        else {
            //there is some user
          openFragment(HomeFragment.newInstance("", ""));
        }
    }

}
