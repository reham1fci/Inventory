package com.max.rm.inventory;

import java.io.Serializable;

/**
 * Created by Eng.Reham Mokhtar
 */

public class inventoryObject  implements Serializable {
    int inv_id ;
    String inv_desc ;
    String inv_date ;
    String inv_ref  ;
    int inv_done   ;
    int inv_store ;

    public String getInv_desc() {
        return inv_desc;
    }

    public String getInv_date() {
        return inv_date;
    }

    public String getInv_ref() {
        return inv_ref;
    }

    public int getInv_done() {
        return inv_done;
    }

    public int getInv_store() {
        return inv_store;
    }

    public int getInv_id() {

        return inv_id;
    }

    public inventoryObject(int inv_id, String inv_desc, String inv_date, String inv_ref, int inv_done, int inv_store) {

        this.inv_id = inv_id;
        this.inv_desc = inv_desc;
        this.inv_date = inv_date;
        this.inv_ref = inv_ref;
        this.inv_done = inv_done;
        this.inv_store = inv_store;
    }

    public inventoryObject(int inv_id, String inv_desc, String inv_date, String inv_ref, int inv_done) {
        this.inv_id = inv_id;
        this.inv_desc = inv_desc;
        this.inv_date = inv_date;
        this.inv_ref = inv_ref;
        this.inv_done = inv_done;
    }

    public String invData(String inv_num){
        return

                        "\"INV_NO\":"+"\""+inv_num+"\""+
                        ",\"INV_Date\":"+"\""+inv_date +"\""+
                        ",\"INV_DESC\":"+"\""+inv_desc +"\""+
                        ",\"REF_NO\":"+"\""+inv_ref+"\"";
    }
}
