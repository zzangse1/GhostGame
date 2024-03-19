package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameResult;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;

public class GameResultAdapter extends RecyclerView.Adapter<GameResultAdapter.ViewHolder>{
   private ArrayList<GameResult> gameResultArrayList;
   private Context context;

    public GameResultAdapter(Context context,ArrayList<GameResult> gameResultArrayList) {
        this.context = context;
        this.gameResultArrayList = gameResultArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_game_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameResultAdapter.ViewHolder holder, int position) {
        GameResult gameResult = gameResultArrayList.get(position);
        holder.bind(gameResult);
    }

    @Override
    public int getItemCount() {
        return gameResultArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_game_result_player_name;
        TextView tv_game_result_penalty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_game_result_player_name = itemView.findViewById(R.id.tv_rv_game_result_player_name);
            tv_game_result_penalty = itemView.findViewById(R.id.tv_rv_game_result_penalty);
        }

        public void bind(GameResult gameResult) {
            setPlayerNameText(gameResult.getPlayerName());
            setPenaltyText(gameResult.getPenalty());
        }

        private void setPlayerNameText(String playerName) {
            tv_game_result_player_name.setText(playerName);
        }
        private void setPenaltyText(String playerName) {
            tv_game_result_penalty.setText(playerName);
        }
    }
}
