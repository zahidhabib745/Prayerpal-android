package com.project.prayerpal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private final ArrayList<MapModel> mapModelArrayList;

    public MapAdapter(ArrayList<MapModel> mapModelArrayList){

        this.mapModelArrayList = mapModelArrayList;
    }


    @NonNull
    @Override
    public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdapter.ViewHolder holder, int position) {

        MapModel model = mapModelArrayList.get(position);

        holder.mosqueName.setText(model.getMosqueName());
        holder.mosqueAddress.setText(model.getMosqueAddress());
        holder.mosqueDistance.setText(model.getMosqueDistance());

    }

    @Override
    public int getItemCount() {
        return mapModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView mosqueName;
        private final TextView mosqueAddress;
        private final TextView mosqueDistance;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mosqueName = itemView.findViewById(R.id.mosqueTitle);
            mosqueAddress = itemView.findViewById(R.id.mosqueAddress);
            mosqueDistance = itemView.findViewById(R.id.mosqueDistance);
        }
    }
}
