package com.max.rm.inventory;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eng.Reham Mokhtar
 */

public class Inventory_points_adapter extends RecyclerView.Adapter<Inventory_points_adapter.MyViewHolder> {


    Activity context ;
    ArrayList<inventoryObject> points ;
    rec_interface onItemClick ;
    rec_interface.longClickInf onLongItemClick  ;

    public Inventory_points_adapter(Activity context, ArrayList<inventoryObject> points, rec_interface onItemClick, rec_interface.longClickInf onLongItemClick) {
        this.context = context;
        this.points = points;
        this.onItemClick = onItemClick;
        this.onLongItemClick = onLongItemClick;
    }



        @Override
        public Inventory_points_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.point_row,null);
            Inventory_points_adapter.MyViewHolder rc= new Inventory_points_adapter.MyViewHolder(v);
            return rc;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            context.getString(R.string.app_name);
            holder.point_name.setText(context.getString(R.string.app_name)+points.get(position).getInv_id());
            holder.date.setText(points.get(position).getInv_date());
             if(points.get(position).getInv_desc()!=null){
            holder.description.setText(points.get(position).getInv_desc());}
            holder. itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onRecItemSelected(position ,v);
                }
            });
             holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View v) {
                      onLongItemClick.onRecItemLongClick(position,v);
                     return false;
                 }
             });
        }

        @Override
        public int getItemCount() {
            return points.size() ;
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView point_name , date , description;

            public MyViewHolder(View itemView) {
                super(itemView);
                point_name = (TextView)itemView.findViewById(R.id.point_name);
                date = (TextView)itemView.findViewById(R.id.point_date);
                description = (TextView)itemView.findViewById(R.id.desc);

            }
        }
    }

