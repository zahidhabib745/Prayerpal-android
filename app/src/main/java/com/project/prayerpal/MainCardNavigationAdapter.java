package com.project.prayerpal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainCardNavigationAdapter extends RecyclerView.Adapter<MainCardNavigationAdapter.CardViewHolder> {

    List<String> cardTitles;
    List<Bitmap> cardIcons;
    Intent intent;
    double originLat;
    double originLng;
    Resources resources;

    public MainCardNavigationAdapter(List<String> cardTitles, List<Bitmap> cardIcons, double originLat, double originLng, Context context){

        this.cardTitles = cardTitles;
        this.cardIcons = cardIcons;
        this.originLat = originLat;
        this.originLng = originLng;
        resources = context.getResources();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        String title = cardTitles.get(position);
        holder.cardTitle.setText(title);

        Bitmap bitmap = cardIcons.get(position);
        holder.cardIcon.setImageBitmap(bitmap);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(holder.getAdapterPosition()){

                    case 0:

                        intent = new Intent(view.getContext(), PrayerActivity.class);
                        view.getContext().startActivity(intent);
                        break;

                    case 1:

                        intent =  new Intent(view.getContext(), MosqueNearMeActivity.class);
                        intent.putExtra(resources.getString(R.string.origin_lat), originLat);
                        intent.putExtra(resources.getString(R.string.origin_lng), originLng);
                        view.getContext().startActivity(intent);
                        break;

                }


            }
        });
    }

    @Override
    public int getItemCount() {

        return cardTitles.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder{

        TextView cardTitle;
        CardView cardView;
        ImageView cardIcon;

        public CardViewHolder(View itemView){
            super(itemView);

            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardView = itemView.findViewById(R.id.main_card);
            cardIcon = itemView.findViewById(R.id.main_card_icon);
        }
    }

}
