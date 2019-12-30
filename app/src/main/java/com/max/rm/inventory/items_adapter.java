package com.max.rm.inventory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class items_adapter extends RecyclerView.Adapter<items_adapter.MyViewHolder>{
    ArrayList<itemObject> items  ;
    Activity activity ;
    rec_interface onItemClick  ;
    rec_interface.longClickInf onLongItemClick  ;

    public items_adapter(ArrayList<itemObject> items, Activity activity, rec_interface onItemClick , rec_interface.longClickInf onLongItemClick) {
        this.items = items;
        this.activity = activity;
        this.onItemClick = onItemClick;
         this.onLongItemClick = onLongItemClick ;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row,null);
        items_adapter.MyViewHolder rc= new items_adapter.MyViewHolder(v);
        return rc;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        holder.item_name.setText(items.get(i).getItemName());
        holder.item_id.setText(items.get(i).getItemId());
        holder.item_count.setText(String.valueOf(items.get(i).getInventoryItemCount()));
        holder.item_unit.setText(items.get(i).getUnit());
        holder.store_num.setText(items.get(i).getStoreCode());
        if (holder.check_layout.getTag().equals("land") && holder.batch == 1) {
         holder.batch_num.setText(items.get(i).getBatch_num());
        }
        if(holder.check_layout.getTag().equals("land")&&holder.expire==1) {
            holder.expire_date.setText(items.get(i).getExpire_date());

        }
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onItemClick.onRecItemSelected(i , v);
             }
         });
         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                  onLongItemClick.onRecItemLongClick(i , v);
                 return false;
             }
         });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView item_name ,  item_id ,item_count , item_unit , store_num , batch_num ,expire_date;
         View check_layout ;
        LinearLayout expire_layout , batch_layout ;
         int batch , expire ;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_name = (TextView)itemView.findViewById(R.id.item_name);
            item_id = (TextView)itemView.findViewById(R.id.item_id);
            item_count = (TextView)itemView.findViewById(R.id.item_count);
            item_unit = (TextView)itemView.findViewById(R.id.item_unit);
            store_num = (TextView)itemView.findViewById(R.id.store_num);
            check_layout = (View) itemView.findViewById(R.id.check_layout);
            SharedPreferences shared = activity.getSharedPreferences(keys.SharedPrefrenceName , 0) ;
             batch = shared.getInt(keys.BACH_NUM , 0) ;
             expire= shared.getInt(keys.EXPIRE_DATE , 0) ;
             if(check_layout.getTag().equals("land")&&batch==1) {
                 batch_num = (TextView)itemView.findViewById(R.id.batch_num);
                 batch_layout = (LinearLayout) itemView.findViewById(R.id.batch_layout);
                 batch_layout.setVisibility(View.VISIBLE);
            }
            if(check_layout.getTag().equals("land")&&expire==1) {
                expire_date = (TextView)itemView.findViewById(R.id.expire_date);
                expire_layout = (LinearLayout) itemView.findViewById(R.id.expire_layout);
                expire_layout.setVisibility(View.VISIBLE);
            }

        }
    }
}
