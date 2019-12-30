package com.max.rm.inventory;

import android.view.View;

/**
 * Created by Eng.Reham Mokhtar
 */

public interface rec_interface {
    public void onRecItemSelected(int position, View view);
    public interface  longClickInf{
        public void onRecItemLongClick(int position, View view);

    }
}
