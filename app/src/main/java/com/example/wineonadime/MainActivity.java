package com.example.wineonadime;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.multidex.MultiDex;

import android.Manifest;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.util.jar.Pack200.Packer.ERROR;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SearchListener, filterDialog.filterDialogListener {

    BottomNavigationView bottomNavigation;
    private FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;
    String id;
    String firstName;
    String lastName;
    String displayName;
    private String newFirst;
    private String newLast;
    String email;
    private String newPassword;
    private boolean hideMenu;
    private LatLng mLocation;
    ArrayList<FavoriteItem> favorites;
    String favoritesId;
    DatabaseReference databaseUsers;

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
        mLocation = new LatLng(43.0731, 89.4012);


        hideBottomBar(false);

        // User Database
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        favorites = new ArrayList<>();

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
            //openFragment(LoginFragment.newInstance("", ""));
            createUI(currentUser);
        }
    }

    public void hideBottomBar(boolean isHidden) {
        bottomNavigation.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

//    @Override
//    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
//        if (hideMenu) {
//            return false;
//        } else {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.search_options, menu);
//            return true;
//        }
//    }

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
                ArrayList<Wine> displayWines = new ArrayList<Wine>();
                //populateWines(displayWines);
                Log.w(TAG, displayWines.toString());
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
                        ArrayList<Wine> displayWines = new ArrayList<Wine>();
                        //populateWines(displayWines);
                        Log.w(TAG, "Here");
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
        //trying to add the map fragment to backstack but it would not work
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.addToBackStack(null);
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

        email = username_text.getText().toString();
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
                            id = mAuth.getCurrentUser().getUid();
                            createUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void register(View view) {
        EditText email_text = findViewById(R.id.email_register);
        EditText password_text = findViewById(R.id.password_register);
        TextView error_message = findViewById(R.id.registerInfoText);
        EditText firstName_text = findViewById(R.id.firstname_register);
        EditText lastName_text = findViewById(R.id.lastname_register);

        firstName = firstName_text.getText().toString().trim();
        lastName = lastName_text.getText().toString().trim();
        email = email_text.getText().toString().trim();
        String password = password_text.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstName_text.setError("First name required.");
            firstName_text.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastName_text.setError("Last name required.");
            lastName_text.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            email_text.setError("Email address required.");
            email_text.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_text.setError("Enter a valid email address.");
            email_text.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_text.setError("Password required.");
            password_text.requestFocus();
            return;
        }

        if (password.length() < 6) {
            password_text.setError("Password should be at least 6 characters long.");
            password_text.requestFocus();
            return;
        }

        createAccount(firstName, lastName, email, password);
    }

    public void createAccount(String first_name, String last_name, String email_address, String password) {
        mAuth.createUserWithEmailAndPassword(email_address, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(first_name, last_name, email_address, new ArrayList<>());
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser curr_user = mAuth.getCurrentUser();
                                        createUI(curr_user);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Registration unsuccessful.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createUI(FirebaseUser user) {
        // User has just signed up - update display name
        if (firstName != null || lastName != null) {
            displayName = firstName + " " + lastName;
            if (user != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build();
                user.updateProfile(profile);
            }
        } else {
            displayName = user.getDisplayName();
            String[] firstAndLast = displayName.split(" ");
            firstName = firstAndLast[0];
            lastName = firstAndLast[1];
            email = user.getEmail();
        }

        openFragment(HomeFragment.newInstance("", ""));
        hideBottomBar(false);
    }

    public void updateUI(FirebaseUser user) {
        //TODO: have some way to have the favorites and profile set

        // User has changed name - update display name
        if (newFirst != null) {
            firstName = newFirst;
            newFirst = null;
        }
        if (newLast != null) {
            lastName = newLast;
            newLast = null;
        }
        displayName = firstName + " " + lastName;
        if (user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            user.updateProfile(profile);
            updateUser(firstName, lastName);
        }

        openFragment(ProfileFragment.newInstance("", ""));
    }

    public boolean updateUser(String first, String last) {
        id = mAuth.getCurrentUser().getUid();
        // User Database
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(id);
        User user = new User(first, last, email, favorites);
        databaseUsers.setValue(user);
        return true;
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

    public void editName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("                 Edit Name");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputFirst = new EditText(this);
        inputFirst.setText(firstName);
        inputFirst.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        inputFirst.setSingleLine();
        inputFirst.setPadding(70, 50, 80, 40);
        layout.addView(inputFirst);

        final EditText inputLast = new EditText(this);
        inputLast.setText(lastName);
        inputLast.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        inputLast.setSingleLine();
        inputLast.setPadding(70, 20, 80, 40);
        layout.addView(inputLast);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            newFirst = inputFirst.getText().toString();
            newLast = inputLast.getText().toString();
            FirebaseUser curr_user = mAuth.getCurrentUser();
            updateUI(curr_user);
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
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Password successfully changed.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to change password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void deleteAccount(View view) {
        // Create "pop up"
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("          Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This is a permanent action.");

        // Set up the buttons
        builder.setPositiveButton("Delete Account", (dialog, which) -> {
            FirebaseUser user = mAuth.getCurrentUser();
            // Remove user from database
            id = mAuth.getCurrentUser().getUid();
            databaseUsers = FirebaseDatabase.getInstance().getReference("Users").child(id);
            databaseUsers.removeValue();

            // Remove user from authentication
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Successfully deleted account.", Toast.LENGTH_SHORT).show();
                                    openFragment(LoginFragment.newInstance("", ""));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to delete account.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

//    public void populateWines(ArrayList<Wine> displayWines) {
//
//        //update location information
//        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
//        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//        //If permission granted update the location
//        else {
//            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, task -> {
//                Location mLastKnownLocation = task.getResult();
//                if (task.isSuccessful() && mLastKnownLocation != null) {
//                    LatLng mCurrentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                    setLocation(mCurrentLatLng);
//                }
//            });
//        }
//
//        Log.w(TAG, "Location" + getLocation().toString());
//
//        findStores(displayWines);
//
//    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void addToFavorites(String name, double price, String brand) {
        FirebaseUser cUser = mAuth.getCurrentUser();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference mDataChild = mData.child(cUser.getUid());
        favoritesId = mDataChild.child("favorites").push().getKey();
        mDataChild.child(favoritesId).setValue(new FavoriteItem(name, price, brand));

        ProfileFragment fragment = new ProfileFragment();
        fragment.addToFavorites(name, price, brand);
        favorites.add(new FavoriteItem(name, price, brand));

        User user = new User(firstName, lastName, email, favorites);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Added to favorites.", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(MainActivity.this, "Could not add to favorites.", Toast.LENGTH_SHORT);
                }
            }
        });
    }


//    public void findStores(ArrayList<Wine> displayWines) {
//        CollectionReference stores = mFirestore.collection("wine-info/stores/stores-collection");
//        GeoFirestore geoFirestoreStores = new GeoFirestore(stores);
////        geoFirestoreStores.setLocation("1", new GeoPoint(43.06488, -89.417854));
////        geoFirestoreStores.setLocation("3", new GeoPoint(43.07721, -89.44962));
////        geoFirestoreStores.setLocation("4", new GeoPoint(43.045593, -87.91113));
////        geoFirestoreStores.setLocation("5", new GeoPoint(43.095253, -87.88641));
////        geoFirestoreStores.setLocation("6", new GeoPoint(42.97329, -88.02649));
////        try {
////            Log.w(TAG, "Starting GeoQuery");
////            geoFirestoreStores.getAtLocation(new GeoPoint(getLocation().latitude, getLocation().longitude), 500000000, new GeoFirestore.SingleGeoQueryDataEventCallback() {
////                @Override
////                public void onComplete(List<? extends DocumentSnapshot> list, Exception e) {
////                    Log.w(TAG, "Completed GeoQuery");
////                    Log.w(TAG, "List Size "  + list.size());
////                    for(DocumentSnapshot documentSnapshot : list) {
////                        String storeName = documentSnapshot.get("store-name").toString();
////                        List<Integer> wines = (List<Integer>) documentSnapshot.get("store-wines");
////                        List<Double> prices = (List<Double>) documentSnapshot.get("wine-price");
////                        for (int j = 0; j < wines.size(); ++j) {
////                            displayWines.add(findWine(wines.get(j), storeName, prices.get(j)));
////                        }
////                    }
////                }
////            });
////        }
////        catch (Exception e) {
////            Log.w(TAG, "Error getting documents.");
////        }
//
//        stores.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()) {
////                            for (QueryDocumentSnapshot document : task.getResult())
////                            Map<String, Object> map = document.getData();
////                            for (Map.Entry<String, Object> entry : map.entrySet()) {
////                                if (entry.getKey().equals("dungeon_group")) {
////                                    Log.d("TAG", entry.getValue().toString());
////                                }
////                            }
////                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                String storeName = document.get("store-name").toString();
////                                List<String> wines = (List<String>) document.get("store-wines");
////                                List<String> prices = (List<String>) document.get("wine-price");
////                                for (int j = 0; j < wines.size(); ++j) {
////                                    assert prices != null;
////                                    String winesku = wines.get(j).toString();
////                                    Double price = Double.parseDouble(prices.get(j));
////                                    displayWines.add(findWine(winesku, storeName, price));
////                                }
////                                Log.w(TAG, "Wines: " + displayWines.size());
////                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }
//
//
//    public Wine findWine(String sku, String store, Double price) {
//        String path = "wine-info/wines/wines-collection/" + sku;
//        DocumentReference oneWine = mFirestore.document(path);
//        ArrayList<Wine> wine = new ArrayList<Wine>();
//        oneWine.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                        Wine wineHolder = new Wine(document.getData().get("wine-name").toString(),
//                                price, document.getData().get("wine-type").toString(),
//                                document.getData().get("wine-brand").toString(),
//                                document.getData().get("wine-year").toString(),
//                                document.getData().get("wine-country").toString());
//                        wineHolder.setStore(store);
//                        wine.add(wineHolder);
//            }
//        }});
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()) {
////                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                Wine wineHolder = new Wine(document.getData().get("wine-name").toString(),
////                                        price, document.getData().get("wine-type").toString(),
////                                        document.getData().get("wine-brand").toString(),
////                                        document.getData().get("wine-year").toString(),
////                                        document.getData().get("wine-country").toString());
////                                wineHolder.setStore(store);
////                                wine.add(wineHolder);
////                            }
////                        } else {
////                            Log.w(TAG, "Error getting documents.", task.getException());
////                        }
////                    }
////                });
//
//
//
////                //getResult();
////        Wine wineHolder = new Wine(document.getData().get("wine-name").toString(),
////                                        price, document.getData().get("wine-type").toString(),
////                                        document.getData().get("wine-brand").toString(),
////                                        document.getData().get("wine-year").toString(),
////                                        document.getData().get("wine-country").toString());
////                                wineHolder.setStore(store);
////                                wine.add(wineHolder);
//
//
//
//        if (wine.size() > 0) {
//            Log.w(TAG, "WINE FOUND");
//            return wine.get(0);
//        }
//        return null;
//    }

    public void setLocation(LatLng location) {
        mLocation = location;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public void sendDirections(View view) {
        TextView store = findViewById(R.id.textViewStore);
        String storeName = store.getText().toString().substring(7);
        Log.w(TAG, "Store Name: " + storeName );
        openFragment(StorePageFragment.newInstance(storeName, ""));
    }

    public void filterDialog(MenuItem item) {

        DialogFragment dialog = new filterDialog();
        dialog.show(getSupportFragmentManager(), "filterDialog");

        Log.w(TAG, "Filter Button Clicked");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.search_options_2);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        return true;
    }

    public void sendFavorite(View itemView) {
        TextView wineName;
        TextView winePrice;
        TextView wineBrand;

        wineName = itemView.findViewById(R.id.textViewWine);
        winePrice = itemView.findViewById(R.id.textViewPrice);
        wineBrand = itemView.findViewById(R.id.textViewBrand);

        String wineNameString = wineName.getText().toString();
        String wineBrandString = wineBrand.getText().toString().substring(7);
        Double winePriceString = Double.parseDouble(winePrice.getText().toString().substring(8));

        //pass into method
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
