package com.max.rm.inventory;

public class storeObject  {
    int store_id ;
    String AVL_QTY ;
    String cost_price ;
    String store_name ;

    public storeObject(int store_id, String store_name) {
        this.store_id = store_id;
        this.store_name = store_name;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getAVL_QTY() {
        return AVL_QTY;
    }

    public String getCost_price() {
        return cost_price;
    }

    public storeObject(int store_id, String AVL_QTY, String cost_price) {
        this.store_id = store_id;
        this.AVL_QTY = AVL_QTY;
        this.cost_price = cost_price;
    }
}
