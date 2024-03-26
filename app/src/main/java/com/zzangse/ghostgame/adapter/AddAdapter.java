package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameAdd;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;


public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {
    //private ArrayList<TeamInfo> teamInfoArrayList;
    private Context context;
    private ArrayList<GameAdd> playerNameList;

//    public AddAdapter(Context context, ArrayList<TeamInfo> teamInfoArrayList) {
//        this.context = context;
//        this.teamInfoArrayList = teamInfoArrayList;
//    }


    public AddAdapter(Context context, ArrayList<GameAdd> playerNameList) {
        this.context = context;
        this.playerNameList = playerNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_add_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // holder.tv_playerName.setText(teamInfoArrayList.get(position).getPlayerName());
        holder.tv_playerName.setText(playerNameList.get(position).getPlayerName());
    }


    @Override
    public int getItemCount() {
        //return teamInfoArrayList.size();
        return playerNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_playerName = itemView.findViewById(R.id.tv_rv_add);
        }

    }
}
