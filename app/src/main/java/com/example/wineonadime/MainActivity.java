package com.example.wineonadime;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.Manifest;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.util.jar.Pack200.Packer.ERROR;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.Collection;


public class MainActivity extends AppCompatActivity implements SearchListener {

    BottomNavigationView bottomNavigation;
    private static FirebaseFirestore mFirestore;
    private static FirebaseAuth mAuth;
    private String newFirst = "";
    private String newLast = "";
    private String newPassword = "";
    private boolean hideMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   userLog = getSharedPreferences("wineOnADimeLogin", Context.MODE_PRIVATE);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        //set up firebase
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        hideMenu = true;



        hideBottomBar(false);
        onStart();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            openFragment(LoginFragment.newInstance("", ""));
        } else {
            //TODO
            openFragment(LoginFragment.newInstance("", ""));
            updateUI(currentUser);
        }
    }

    public void hideBottomBar(boolean isHidden) {
        bottomNavigation.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        if(hideMenu) {
            return false;
        }
        else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_options, menu);
            return true;
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void openFragment2(View view) {
        switch (view.getId()) {
            case R.id.guide_button:
                hideMenu = true;
                openFragment(GuideFragment.newInstance("", ""));
                break;
            case R.id.map_button:
                hideMenu = true;
                openFragment(MapFragment.newInstance("", ""));
                //goToMap();
                break;
            case R.id.search_wines_button:
                hideMenu = false;
                ArrayList<String> displayWines = new ArrayList<>();
                populateWines(displayWines);
                openFragment(SearchFragment.newInstance(displayWines));
                break;
            case R.id.profile_button:
                hideMenu = true;
                openFragment(ProfileFragment.newInstance("", ""));
                break;
        }

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        hideMenu = true;
                        openFragment(HomeFragment.newInstance("", ""));
                        return true;
                    case R.id.navigation_guide:
                        hideMenu = true;
                        openFragment(GuideFragment.newInstance("", ""));
                        return true;
                    case R.id.navigation_map:
                        hideMenu = true;
                        openFragment(MapFragment.newInstance("", ""));
                        //goToMap();
                        return true;
                    case R.id.navigation_search:
                        hideMenu = false;
                        ArrayList<String> displayWines = new ArrayList<>();
                        populateWines(displayWines);
                        openFragment(SearchFragment.newInstance(displayWines));
                        return true;
                    case R.id.navigation_profile:
                        hideMenu = true;
                        openFragment(ProfileFragment.newInstance("", ""));
                        return true;
                }
                return false;
            };

    public void goToMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void openSearch(View view) {
        onSearchRequested();
        Log.i("search", "onsearch called");
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

    public void register(View view) {
        EditText email_text = findViewById(R.id.email_register);
        EditText password_text = findViewById(R.id.password_register);
        TextView error_message = findViewById(R.id.registerInfoText);
        EditText firstname = findViewById(R.id.firstname_register);
        EditText lastname = findViewById(R.id.lastname_register);

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
        } else {
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

    public void populateWines(ArrayList<String> displayWines) {
        CollectionReference stores = mFirestore.getInstance().collection("wines-collection");
        CollectionReference wines = mFirestore.getInstance().collection("stores-collection");
        ArrayList<Wine> winesList = new ArrayList<Wine>();
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);; // Save the instance
        int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
        GeoFirestore geoFirestoreStores = new GeoFirestore(stores);

        //getting location information
        // Check if permission granted
        int permission = ActivityCompat.checkSelfPermission( this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        //If not, ask for it
        if( permission == PackageManager.PERMISSION_DENIED )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
        }
        //If permission granted get wines in nearby area and display in listview
        else
        {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener( this, task -> {
                Location mLastKnownLocation = task.getResult();
                if( task.isSuccessful() && mLastKnownLocation != null )
                {
                    LatLng mCurrentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude() );
                    GeoQuery geoQuery = geoFirestoreStores.queryAtLocation(new GeoPoint(mCurrentLatLng.latitude, mCurrentLatLng.longitude),1);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

                        @Override
                        public void onKeyMoved(String s, GeoPoint geoPoint) {
                            //this should not happed
                        }

                        @Override
                        public void onKeyExited(String s) {
                            //this should not happen
                        }

                        @Override
                        public void onKeyEntered(String s, GeoPoint geoPoint) {
                            //this should not happen
                        }

                        @Override
                        public void onGeoQueryError(Exception e) {
                            Log.d(ERROR, "An error occurred loading the query documents.");
                        }

                        @Override
                        public void onGeoQueryReady() {
                            Log.d(TAG, "All initial data has been loaded and events have been fired!");
                        }
                });

//                    for(int i = 0; i < geoQuery.getQueries().size(); ++i) {
//                        Query store = geoQuery.getQueries().get(i).addS
//                        Log.d(TAG, store. + " => " + document.getData());
//                    }
                    wines.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });

                    stores.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });



                    for (Wine wine: winesList) {
                        displayWines.add(String.format("Wine:%s\nCheapest Price: $%d\nStore:", wine.getName(), wine.getPrice(), wine.getStore()));
                    }



                }
            });
        }


    }
}
