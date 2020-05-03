package com.example.wineonadime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WineAdapter extends RecyclerView.Adapter<WineAdapter.WineNote> {
    private ArrayList<Wine> wineData;

    public static class WineNote extends RecyclerView.ViewHolder {

        public TextView wineName;
        public TextView winePrice;
        public TextView wineBrand;
        public TextView wineStore;
        public WineNote(View itemView) {
            super(itemView);
            wineName = itemView.findViewById(R.id.textViewWine);
            winePrice = itemView.findViewById(R.id.textViewPrice);
            wineBrand = itemView.findViewById(R.id.textViewBrand);
            wineStore = itemView.findViewById(R.id.textViewStore);
        }
    }

    public WineAdapter(ArrayList<Wine> wineData) {
        this.wineData = wineData;
    }

    @NonNull
    @Override
    public WineAdapter.WineNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wine_view, parent, false);
        return new WineNote(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WineAdapter.WineNote holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        Wine holderW = wineData.get(position);
        holder.wineName.setText(holderW.getName());
        holder.winePrice.setText("Price: $" + holderW.getPrice().toString());
        holder.wineBrand.setText("Brand: " + holderW.getBrand());
        holder.wineStore.setText("Brand: " + holderW.getStore());

    }

    @Override
    public int getItemCount() {
        return wineData.size();
    }
}
