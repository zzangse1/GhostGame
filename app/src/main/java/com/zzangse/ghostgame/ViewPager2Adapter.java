package com.zzangse.ghostgame;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zzangse.ghostgame.databinding.Viewpager2ItemBinding;


import java.util.ArrayList;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>{

    private Context context;
    private Activity activity;
    private ArrayList<ListItem> listItems;

    public ViewPager2Adapter(Context context, Activity activity, ArrayList<ListItem> listItems) {
        this.context = context;
        this.activity = activity;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager2_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imagePath = Uri.parse("android.resource://" + activity.getPackageName() + "/"
                + listItems.get(position).getList().get(0).getImagePath());
        Glide.with(context).load(imagePath).into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private Viewpager2ItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewBinding();
        }

        private void viewBinding(){
            binding = Viewpager2ItemBinding.bind(itemView);
        }
    }
}