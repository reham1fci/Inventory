package com.max.rm.inventory;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class itemSearchAdapater extends BaseAdapter {
    ArrayList<itemObject> items ;
    Activity activity ;

    public itemSearchAdapater(ArrayList<itemObject> items, Activity activity) {
        this.items = items;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         View v= activity.getLayoutInflater().inflate(R.layout.item_search_row, null);
        TextView item_name= (TextView)v.findViewById(R.id.item_name);
        TextView item_id= (TextView)v.findViewById(R.id.item_id);
        TextView item_unit= (TextView)v.findViewById(R.id.item_unit);
        Log.d("items" ,items.toString());
        item_name.setText(items.get(position).getItemName());
        item_id.setText(items.get(position).getItemId());
        item_unit.setText(items.get(position).getUnit());
        return v;
    }
}
