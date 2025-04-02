package com.project.prayerpal;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PrayerCardAdapter extends RecyclerView.Adapter<PrayerCardAdapter.ViewHolder>{

    private final ArrayList<PrayerModel> prayerModelArrayList;
    Context context;

    public PrayerCardAdapter(Context context, ArrayList<PrayerModel> prayerModelArrayList){

        this.context = context;
        this.prayerModelArrayList = prayerModelArrayList;
    }


    @Override
    public PrayerCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayer_card_view,parent, false);

        return new PrayerCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrayerCardAdapter.ViewHolder holder, int position) {

        PrayerModel model = prayerModelArrayList.get(position);

        holder.prayerName.setText(model.getPrayerName());
        holder.relativeLayout.setBackground(new BitmapDrawable(context.getResources(), model.getPrayerImage()));
        holder.prayerTime.setText(model.getPrayerTime());
        holder.prayerDup.setText(model.getPrayerName());
    }

    @Override
    public int getItemCount() {
        return prayerModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView prayerName;
        private final RelativeLayout relativeLayout;
        private final TextView prayerTime;
        private final TextView prayerDup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prayerName = itemView.findViewById(R.id.prayer_text);
            relativeLayout = itemView.findViewById(R.id.prayer_relative_layout);
            prayerTime = itemView.findViewById(R.id.prayer_time);
            prayerDup = itemView.findViewById(R.id.shadow);
        }
    }
}
