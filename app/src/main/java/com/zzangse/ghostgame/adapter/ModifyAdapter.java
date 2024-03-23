package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameModify;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;

public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GameModify> gameModifyArrayList;
    private ModifyAdapterClick modifyAdapterClick=null;

    public interface ModifyAdapterClick{
       // void onClickDelete(View v,int pos);
        void onClickDelete(GameModify gameModify);
        void onClickInfo(GameModify gameModify);
    }

    public ModifyAdapter(Context context, ArrayList<GameModify> gameModifyArrayList) {
        this.context=context;
        this.gameModifyArrayList=gameModifyArrayList;
    }

    public void setOnClick(ModifyAdapterClick modifyAdapterClick) {
        this.modifyAdapterClick = modifyAdapterClick;
    }


    @NonNull
    @Override
    public ModifyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_tv_delete,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModifyAdapter.ViewHolder holder, int position) {
        final GameModify gameModify = gameModifyArrayList.get(position);
        if (gameModify != null) { // gameModify가 null이 아닌지 확인
            String playerName = gameModify.getPlayerName();
            holder.bind(playerName);
//            holder.ib_Delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = holder.getAdapterPosition();
//                    modifyAdapterClick.onClickDelete(v,pos);
//                }
//            });
            holder.ib_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modifyAdapterClick != null) {
                        modifyAdapterClick.onClickDelete(gameModify); // 올바른 객체와 함께 호출하는지 확인
                    } else {
                        Log.d("onClickDelete", "ModifyAdapterClick is null");
                    }
                }
            });
            holder.tv_playerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modifyAdapterClick != null) {
                        modifyAdapterClick.onClickInfo(gameModify); // 올바른 객체와 함께 호출하는지 확인
                    } else {
                        Log.d("onClickInfo", "ModifyAdapterClick is null");
                    }
                }
            });
        } else {
            Log.d("onBindViewHolder", "GameModify object is null at position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        return gameModifyArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playerName;
        ImageButton ib_Delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_playerName = itemView.findViewById(R.id.tv_rv);
            ib_Delete = itemView.findViewById(R.id.ib_rv);
        }

        public void bind(String playerName) {
            tv_playerName.setText(playerName);
        }
    }
}
