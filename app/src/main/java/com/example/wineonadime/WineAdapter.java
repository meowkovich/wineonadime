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

        private TextView wineName;
        private TextView winePrice;
        private TextView wineBrand;
        private TextView wineStore;
        private TextView wineType;
        private TextView wineYear;
        public WineNote(View itemView) {
            super(itemView);
            wineName = itemView.findViewById(R.id.textViewWine);
            winePrice = itemView.findViewById(R.id.textViewPrice);
            wineBrand = itemView.findViewById(R.id.textViewBrand);
            wineStore = itemView.findViewById(R.id.textViewStore);
            wineType = itemView.findViewById(R.id.textViewType);
            wineYear = itemView.findViewById(R.id.textViewYear);
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
        holder.winePrice.setText("Price: $" + holderW.getPrice());
        holder.wineBrand.setText("Brand: " + holderW.getBrand());
        holder.wineStore.setText("Store: " + holderW.getStore());
        holder.wineYear.setText("Year: " + holderW.getYear());
        holder.wineType.setText("Type: " + holderW.getType());

    }

    @Override
    public int getItemCount() {
        return wineData.size();
    }
}
