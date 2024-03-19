package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameEditPenalty;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {
    private ArrayList<GameEditPenalty> gameEditPenaltyList;
    private Context context;
    EditAdapterClick editAdapterClick;

    public interface EditAdapterClick {
//        void onClickDelete(EditAdapter.ViewHolder holder, int position, View v);
//
//        void onClickInfo(EditAdapter.ViewHolder holder, int position, View v);
        void onClickDelete(GameEditPenalty gameEditPenalty);

        void onClickInfo(GameEditPenalty gameEditPenalty);

    }

    public EditAdapter(Context context, ArrayList<GameEditPenalty> gameEditPenaltyList) {
        this.context = context;
        this.gameEditPenaltyList = gameEditPenaltyList;
    }

    public void setOnclick(EditAdapterClick editAdapterClick) {
        this.editAdapterClick = editAdapterClick;
    }

    @NonNull
    @Override
    public EditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_tv_delete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditAdapter.ViewHolder holder, int position) {
        GameEditPenalty gameEditPenalty = gameEditPenaltyList.get(position);
        String gameEditName = gameEditPenalty.getPenalty();
        holder.bind(gameEditName);

        holder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAdapterClick.onClickDelete(gameEditPenalty);
            }
        });
        holder.tv_rv_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAdapterClick.onClickInfo(gameEditPenalty);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameEditPenaltyList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rv_random;
        ImageButton ib_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rv_random = itemView.findViewById(R.id.tv_rv);
            ib_delete = itemView.findViewById(R.id.ib_rv);
        }

        public void bind(String gameEdit) {
            setRandomText(gameEdit);
        }


        private void setRandomText(String text) {
            tv_rv_random.setText(text);
        }
    }
}
