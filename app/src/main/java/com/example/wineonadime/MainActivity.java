package com.example.wineonadime;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity implements SearchListener {

    BottomNavigationView bottomNavigation;
    SharedPreferences userLog;
    public FirebaseAuth mAuth;
    private String newFirst = "";
    private String newLast = "";
    private String newPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   userLog = getSharedPreferences("wineOnADimeLogin", Context.MODE_PRIVATE);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        mAuth = FirebaseAuth.getInstance();
        hideBottomBar(false);
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
                            //goToMap();
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

    public void goToMap()
    {
        Intent intent = new Intent( this, MapActivity.class );
        startActivity( intent );
    }

    @Override
    public void openSearch( View view )
    {
        onSearchRequested();
        Log.i("search", "onsearch called" );
    }

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

    public void openSettings(View view) {
        openFragment(SettingsFragment.newInstance());
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        openFragment(LoginFragment.newInstance());
        Toast.makeText(this, "Successfully logged out.",
                Toast.LENGTH_LONG).show();
    }

    //Test
    public void editName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("                 Edit Name");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputFirst = new EditText(this);
        inputFirst.setHint("First Name");
        inputFirst.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        inputFirst.setSingleLine();
        inputFirst.setPadding(70, 50, 80, 40);
        layout.addView(inputFirst);

        final EditText inputLast = new EditText(this);
        inputLast.setHint("Last Name");
        inputLast.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        inputLast.setSingleLine();
        inputLast.setPadding(70, 20, 80, 40);
        layout.addView(inputLast);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            newFirst = inputFirst.getText().toString();
            newLast = inputLast.getText().toString();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void changePassword(View view) {
        // Create "pop up"
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("          Change Password");

        // To include multiple things in the alert dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Input Password
        final EditText input = new EditText(this);
        input.setHint("New Password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setSingleLine();
        input.setPadding(70, 50, 80, 40);
        input.setTransformationMethod(new PasswordTransformationMethod());
        layout.addView(input);

        // Show/Hide Password
        final AppCompatCheckBox show = new AppCompatCheckBox(this);
        show.setText("Show Password");
        layout.addView(show);

        // Control visibility
        show.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                // show password
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                // hide password
                input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            newPassword = input.getText().toString();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}
