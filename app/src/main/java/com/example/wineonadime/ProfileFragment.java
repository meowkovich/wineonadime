package com.example.wineonadime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.util.Listener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public String firstName;
    public String lastName;

    public String email;

    Button settings;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdaptor;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<FavoriteItem> favoritesList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firstName = ((MainActivity)getActivity()).getFirstName();
        lastName = ((MainActivity)getActivity()).getLastName();
        email = ((MainActivity)getActivity()).getEmail();
        favoritesList = ((MainActivity)getActivity()).getFavorites();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        if (currentUser != null) {
            TextView textViewName = (TextView) rootView.findViewById(R.id.userName);
            String fullName = firstName + " " + lastName;
            textViewName.setText(fullName);

            TextView textViewEmail = (TextView) rootView.findViewById(R.id.profileEmail);
            textViewEmail.setText(email);


//            favoritesList.add(new FavoriteItem("Name1", 19.99, "Brand1"));
//            favoritesList.add(new FavoriteItem("Name2", 19.99, "Brand2"));
//            favoritesList.add(new FavoriteItem("Name3", 19.99, "Brand3"));
//            favoritesList.add(new FavoriteItem("Name4", 19.99, "Brand4"));
//            favoritesList.add(new FavoriteItem("Name5", 19.99, "Brand5"));
//            favoritesList.add(new FavoriteItem("Name6", 19.99, "Brand6"));

            mRecyclerView = rootView.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getContext());
            mAdaptor = new FavoriteAdapter(favoritesList);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdaptor);

        }

        // Inflate the layout for this fragment
        return rootView;
    }

//    public void addToFavorites(String name, double price, String brand) {
//        favoritesList.add(new FavoriteItem(name, price, brand));
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    }

    public void callMain(View view){
        ((MainActivity)getActivity()).openSettings(view);
    }

}
