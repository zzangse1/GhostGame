package com.zzangse.ghostgame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.Group;
import com.zzangse.ghostgame.R;

import java.util.ArrayList;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Group> list;
    private SettingAdapterClick settingAdapterClick;


    public SettingAdapter(Context context, ArrayList<Group> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClick(SettingAdapterClick settingAdapterClick) {
        this.settingAdapterClick = settingAdapterClick;
    }
    public interface SettingAdapterClick{
       void onClickDelete(Group group);
       void onClickInfo(Group group);
      // void onClickInfo(View v, int pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_tv_delete, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = list.get(position);
        holder.bind(group);

        holder.ib_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingAdapterClick != null) {
                    settingAdapterClick.onClickDelete(group);
                }
            }
        });
        holder.tv_GroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (settingAdapterClick != null) {
                    settingAdapterClick.onClickInfo(group);
                    //settingAdapterClick.onClickInfo(v,pos);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_GroupName;
        ImageButton ib_Delete;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tv_GroupName = itemView.findViewById(R.id.tv_rv);
            ib_Delete = itemView.findViewById(R.id.ib_rv);
        }

//        public void bind(String groupName) {
//            tv_GroupName.setText(groupName);
//
//        }
        public void bind(Group groupName) {
            tv_GroupName.setText(groupName.getGroupName());

        }
    }
}
