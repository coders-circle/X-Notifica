package com.fabb.notifica;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int HEADER_TYPE = 0;
    private static final int ROW_TYPE = 1;

    private String[] rows;
    private int[] icon;
    private String userName;
    private String userRoll;

    private int selectedItem = 1;
    public void SetSelected(int item) {
        if (item>=0)
            selectedItem = item;
    }
    public int GetSelected() {
        return selectedItem;
    }

    public DrawerAdapter(String[] rows,int[] icons,String userName,String userRoll) {
        this.rows=rows;
        this.icon=icons;
        this.userName=userName;
        this.userRoll=userRoll;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_TYPE){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header,parent,false);
            return new ViewHolder(view,viewType);
        }
        else if(viewType == ROW_TYPE){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row,parent,false);
            return new ViewHolder(view,viewType);
        }

        return null;
    }

    private HashMap<Integer, Integer> counts = new HashMap<>();
    public void setCount(int index, int count) {
        counts.put(index, count);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder.viewType==ROW_TYPE){
            String rowText = rows[position - 1];
            holder.row_title.setText(rowText);
            int rowIcon = icon[position-1];
            holder.row_icon.setImageResource(rowIcon);

            if (counts.containsKey(position-1) && counts.get(position-1)>0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(counts.get(position-1) + "");
            }
            else
                holder.badge.setVisibility(View.INVISIBLE);

            if (position == selectedItem+1) {
                int color = Color.parseColor("#FD4444");
                if (position == 2)
                    color = Color.parseColor("#388E3C");
                else if (position == 3)
                    color = Color.parseColor("#3F51B4");
                holder.row_title.setTextColor(color);
                holder.row_icon.setColorFilter(Color.parseColor("#FFFFFF"));
                holder.row_icon.setColorFilter(color);
                holder.itemView.setSelected(true);
            }
            else {
                int color = Color.parseColor("#757575");
                holder.row_title.setTextColor(color);
                holder.row_icon.setColorFilter(color);
                holder.itemView.setSelected(false);
            }
        }
        if(holder.viewType==HEADER_TYPE){
            holder.user_name.setText(userName);
            holder.user_roll.setText(userRoll);
        }
    }

    @Override
    public int getItemCount() {
        return rows.length+1;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0)
            return HEADER_TYPE;
        return ROW_TYPE;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        protected int viewType;

        ImageView row_icon;
        TextView row_title;
        TextView user_name;
        TextView user_roll;
        TextView badge;

        public ViewHolder(View itemView,int viewType) {
            super(itemView);

            this.viewType = viewType;
            if(viewType==ROW_TYPE){
                row_icon = (ImageView) itemView.findViewById(R.id.drawer_row_icon);
                row_title = (TextView) itemView.findViewById(R.id.drawer_row_title);
                badge = (TextView) itemView.findViewById(R.id.drawer_badge);
                itemView.setClickable(true);
            }
            if(viewType == HEADER_TYPE){
                user_name=(TextView) itemView.findViewById(R.id.user_name);
                user_roll=(TextView) itemView.findViewById(R.id.user_roll);
            }

        }
    }
}

