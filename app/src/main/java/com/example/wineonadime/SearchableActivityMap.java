package com.example.wineonadime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SearchableActivityMap extends AppCompatActivity implements
        SearchMapAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    String query; //query passed to activity from search bar
    ArrayList<Store> resultsStores = new ArrayList<>();
    ArrayList<Wine> resultsWines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable_map);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("QUERY", query);
        }

        // create recyclerview for search query
        recyclerView = (RecyclerView) findViewById( R.id.query_recycler );

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        updateListOfMatchingStoresWinesFromJSON(); //update store and wine list that match query
        mAdapter = new SearchMapAdapter(resultsStores, resultsWines, this );
        ((SearchMapAdapter) mAdapter).setClickListener( this );
        recyclerView.setAdapter(mAdapter);
    }

    public void updateListOfMatchingStoresWinesFromJSON()
    {

        try
        {
            Log.i( "jsondata", "read stores called" );
            //Read JSON file
            JSONObject jsonObject = new JSONObject( loadJSONStoreFile() );
            //Get all stores
            JSONArray storeArray = jsonObject.getJSONArray( "stores" );

            for( int i = 0; i < storeArray.length(); i++ )
            {
                //Read each parameter for creating a new store
                JSONObject iStore = storeArray.getJSONObject( i ).getJSONObject( "store" );
                String storeName = iStore.getString( "name" );

                if( storeName.toLowerCase().contains( query.toLowerCase() ) )
                {
                    Store newStore = buildStoreFromJSON( iStore );
                    //add to list of stores
                    this.resultsStores.add( newStore );
                }

                //If wine matches, add to the wines list
                JSONArray jsonWineArray = iStore.getJSONArray( "wines" );
                for( int j = 0; j < jsonWineArray.length(); j++ )
                {
                    if( jsonWineArray.getJSONObject(j).getString( "name" ).toLowerCase()
                            .contains( query.toLowerCase() ) )
                    {
                        Wine newWine = buildWineFromJSON( jsonWineArray.getJSONObject(j) );
                        this.resultsWines.add( newWine );
                    }
                }
            }
        }
        catch( JSONException e )
        {
            e.printStackTrace();
            Log.i( "jsondata", "read stores error" );
        }
        Log.i( "jsondata", "size:" + this.resultsStores.size() );
    }

    public Store buildStoreFromJSON( JSONObject iStore )
    {
        return Store.buildStoreFromJSONData( iStore );
    }

    public Wine buildWineFromJSON( JSONObject jsonWine )
    {
        return Wine.buildWineFromJSONData( jsonWine );
    }

    public String loadJSONStoreFile()
    {
        String json;

        try
        {
            InputStream inputStream = getAssets().open( "derulo.json" );
            int bufferSize = inputStream.available();
            byte[] buffer = new byte[bufferSize];
            inputStream.read( buffer );
            inputStream.close();
            json = new String( buffer, "UTF-8" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return null;
        }

        return json;
    }

    @Override
    public void onItemClick( View view, int position )
    {
        //Load a fragment and set its args to the title of the store so we can call store in frag
        Bundle storeBundle = new Bundle();
        storeBundle.putString( "storename", ((SearchMapAdapter) mAdapter).getItem( position ) );
        Fragment storeFragment = new StorePageFragment();
        storeFragment.setArguments( storeBundle );

        //Navigate to new fragment using FragmentManager and FragmentTransaction
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.frame_search_page, storeFragment );
        fragmentTransaction.addToBackStack( null );
        fragmentTransaction.commit();
    }
}
