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
            textView = (TextView) itemView;
        }
    }

    public WineAdapter(ArrayList<String> wineData) {
        this.wineData = wineData;
    }

    @NonNull
    @Override
    public WineAdapter.WineNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (TextView) new TextView(parent.getContext());
        WineAdapter.WineNote holder = new WineAdapter.WineNote(view);

        WineNote wineNote = new WineNote(view);
        return null;
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
