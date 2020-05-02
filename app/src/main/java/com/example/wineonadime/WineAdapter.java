package com.example.wineonadime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WineAdapter extends RecyclerView.Adapter<WineAdapter.WineNote> {
    private ArrayList<String> wineData;

    public static class WineNote extends RecyclerView.ViewHolder {

        public TextView textView;
        public WineNote(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.wine_list_item);
        }
    }

    public WineAdapter(ArrayList<String> wineData) {
        this.wineData = wineData;
    }

    @NonNull
    @Override
    public WineAdapter.WineNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wine_text_view, parent, false);
        return new WineNote(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WineAdapter.WineNote holder, int position) {
        // -get element from your dataset at this position
        // -replace the contents of the view with that element
        holder.textView.setText(wineData.get(position));
    }

    @Override
    public int getItemCount() {
        return wineData.size();
    }
}
