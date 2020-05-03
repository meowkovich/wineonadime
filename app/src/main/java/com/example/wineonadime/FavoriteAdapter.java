package com.example.wineonadime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private ArrayList<FavoriteItem> mFavoritesList;

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public TextView wineName;
        public TextView winePrice;
        public TextView wineBrand;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            wineName = itemView.findViewById(R.id.textViewWine);
            winePrice = itemView.findViewById(R.id.textViewPrice);
            wineBrand = itemView.findViewById(R.id.textViewBrand);
        }
    }

    public FavoriteAdapter(ArrayList<FavoriteItem> favoritesList) {
        mFavoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_wine_view, parent, false);
        FavoriteViewHolder fvh = new FavoriteViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteItem currentItem = mFavoritesList.get(position);

        holder.wineName.setText(currentItem.getName());
        holder.winePrice.setText(currentItem.getPrice() + "");
        holder.wineBrand.setText(currentItem.getBrand());
    }

    @Override
    public int getItemCount() {
        return mFavoritesList.size();
    }
}
