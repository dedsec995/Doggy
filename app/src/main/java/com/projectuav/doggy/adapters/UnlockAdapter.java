package com.projectuav.doggy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.projectuav.doggy.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class UnlockAdapter extends RecyclerView.Adapter<UnlockAdapter.UnlockViewHolder> {
    private UnlockAdapter.OnItemClickListener listener;
    private String[] mDataset;
    String compare = "0";



    @Override
    public void onBindViewHolder(UnlockViewHolder holder, int position) {
        String[] completeDataX = mDataset[position].split("-_-_-_-_-");

        if (completeDataX[1].equals("1")){
            Picasso.get().load(Uri.parse(completeDataX[2])).into(holder.profiledp);
            holder.profilename.setText(completeDataX[0]);
            holder.profilename.setTextColor(Color.parseColor("#161A2C"));
            holder.cardColor.setBackgroundResource(R.color.colorSelected);
        }
        else{
            holder.profilename.setText("???");
            holder.profilename.setTextColor(Color.parseColor("#6F87E6"));
            holder.cardColor.setBackgroundResource(R.color.colorPrimary);
            Random raand = new Random();
            int raandX = raand.nextInt(3);
            if(raandX == 0){
                Picasso.get().load(Uri.parse("android.resource://com.projectuav.doggy/drawable/dogunknown1")).into(holder.profiledp);
            }
            else if(raandX == 1){
                Picasso.get().load(Uri.parse("android.resource://com.projectuav.doggy/drawable/dogunknown2")).into(holder.profiledp);
            }
            else if(raandX == 2){
                Picasso.get().load(Uri.parse("android.resource://com.projectuav.doggy/drawable/dogunknown3")).into(holder.profiledp);
            }
            else if(raandX == 3){
                Picasso.get().load(Uri.parse("android.resource://com.projectuav.doggy/drawable/dogunknown4")).into(holder.profiledp);
            }
        }

    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public UnlockAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UnlockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.unlock_card, parent, false);

        return new UnlockViewHolder(itemView);

    }
    class UnlockViewHolder extends RecyclerView.ViewHolder {
        TextView profilename;
        ImageView profiledp;
        ConstraintLayout cardColor;


        public UnlockViewHolder(View itemView) {
            super(itemView);
            profilename = itemView.findViewById(R.id.profile_user_name_following);
            profiledp = itemView.findViewById(R.id.profile_Dp_following);
            cardColor = itemView.findViewById(R.id.cardColor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onListClick(position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {

        void onListClick(int position);
    }
    public void setOnListClickListener(UnlockAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}