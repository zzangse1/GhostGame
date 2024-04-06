package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameAdd;
import com.zzangse.ghostgame.Group;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;


public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {
    //private ArrayList<TeamInfo> teamInfoArrayList;
    private Context context;
    private ArrayList<GameAdd> playerNameList;
    private AddAdpaterClick addAdpaterClick;

    public interface AddAdpaterClick{
        void onClickDelete(GameAdd gameAdd);
    }

    public void setOnClick(AddAdpaterClick addAdpaterClick) {
        this.addAdpaterClick = addAdpaterClick;
    }


    public AddAdapter(Context context, ArrayList<GameAdd> playerNameList) {
        this.context = context;
        this.playerNameList = playerNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_tv_delete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameAdd gameAdd = playerNameList.get(position);
        holder.tv_playerName.setText(playerNameList.get(position).getPlayerName());

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addAdpaterClick != null) {
                    addAdpaterClick.onClickDelete(gameAdd);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        //return teamInfoArrayList.size();
        return playerNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playerName;
        ImageView ib_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_playerName = itemView.findViewById(R.id.tv_rv);
            ib_delete = itemView.findViewById(R.id.ib_rv);
        }

    }
}
