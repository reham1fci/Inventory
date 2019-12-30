package com.max.rm.inventory;

import java.io.Serializable;

/**
 * Created by Eng.Reham Mokhtar
 */

public class itemObject implements Serializable {
            String itemId ;
            String barCode ;
            String itemName ;
            String itemSize ;
            String unit ;
            String itemDesc ;
            String storeCode ; //WH_CODE
            String storeItemCount ; //AVL_QTY
            int inventoryItemCount ;//ITM_QTY
            String sellingPrice ;//ITM_PRICE
            String costPrice ;//STK_NEW
            String batch_num ;
            String expire_date ;
     int record_id ;

    public String getExpire_date() {
        return expire_date;
    }

    public int getRecord_id() {
        return record_id;
    }

    public String getBatch_num() {
        return batch_num;
    }

    public itemObject(String itemId, String barCode, String itemName, String itemSize, String unit, String itemDesc, String storeCode, String storeItemCount, int inventoryItemCount, String sellingPrice, String costPrice, String batch_num, String expire_date, int record_id) {
        this.itemId = itemId;
        this.barCode = barCode;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.unit = unit;
        this.itemDesc = itemDesc;
        this.storeCode = storeCode;
        this.storeItemCount = storeItemCount;
        this.inventoryItemCount = inventoryItemCount;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.batch_num = batch_num;
        this.expire_date = expire_date;
        this.record_id = record_id;
    }

    public itemObject(String itemId, String barCode, String itemName, String itemSize, String unit,
                      String itemDesc, String storeCode, String storeItemCount, int inventoryItemCount,
                      String sellingPrice, String costPrice  ) {
        this.itemId = itemId;
        this.barCode = barCode;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.unit = unit;
        this.itemDesc = itemDesc;
        this.storeCode = storeCode;
        this.storeItemCount = storeItemCount;
        this.inventoryItemCount = inventoryItemCount;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
    }

    public itemObject(String itemId, String barCode, String itemName, String itemSize, String unit, String itemDesc, String storeCode, String storeItemCount, int inventoryItemCount, String sellingPrice, String costPrice, String batch_num, String expire_date) {
        this.itemId = itemId;
        this.barCode = barCode;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.unit = unit;
        this.itemDesc = itemDesc;
        this.storeCode = storeCode;
        this.storeItemCount = storeItemCount;
        this.inventoryItemCount = inventoryItemCount;
        this.sellingPrice = sellingPrice;
        this.costPrice = costPrice;
        this.batch_num = batch_num;
        this.expire_date = expire_date;
    }

    public itemObject(String itemId, String barCode, String itemName, String itemSize, String unit, String itemDesc, String storeCode, String batch_num, String expire_date) {
        this.itemId = itemId;
        this.barCode = barCode;
        this.itemName = itemName;
        this.itemSize = itemSize;
        this.unit = unit;
        this.itemDesc = itemDesc;
        this.storeCode = storeCode;
        this.batch_num = batch_num;
        this.expire_date = expire_date;
    }

    public String getItemId() {
        return itemId;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemSize() {
        return itemSize;
    }

    public String getUnit() {
        return unit;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getStoreItemCount() {
        return storeItemCount;
    }

    public int getInventoryItemCount() {
        return inventoryItemCount;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setBatch_num(String batch_num) {
        this.batch_num = batch_num;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    @Override
    public String toString() {
        return "itemObject{" +
                "itemId='" + itemId + '\'' +
                ", barCode='" + barCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemSize='" + itemSize + '\'' +
                ", unit='" + unit + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", storeCode='" + storeCode + '\'' +
                ", storeItemCount='" + storeItemCount + '\'' +
                ", inventoryItemCount=" + inventoryItemCount +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", costPrice='" + costPrice + '\'' +
                ", batch_num='" + batch_num + '\'' +
                ", expire_date='" + expire_date + '\'' +
                '}';
    }
     public String itemData(String inv_num){
        String item=   "{"+
                "\"RERD_NO\":"+"\""+record_id+"\""+
                ",\"INV_NO\":"+"\""+inv_num+"\""+
                ",\"ITEM_ID\":"+"\""+itemId +"\""+
                ",\"UNIT\":"+"\""+unit + "\""+
                ",\"UNIT_SIZE\":"+"\""+itemSize+"\""+
                ",\"WH_CODE\":"+"\""+storeCode+"\""+
                ",\"AVL_QTY\":"+"\""+storeItemCount+"\""+
                ",\"ITM_QTY\":"+"\""+inventoryItemCount +"\"";
         if (batch_num!=null){
              String batch=  ",\"BATCH_ID\":"+"\""+batch_num+"\"";
           item= item+ batch;
         }
         if (expire_date!=null){
              String exp= ",\"EXP_DATE\":"+"\""+expire_date+"\"";
             item=item+exp;

         }


        return
      item+
        " }" ;
     }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public void setInventoryItemCount(int inventoryItemCount) {
        this.inventoryItemCount = inventoryItemCount;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public void setStoreItemCount(String storeItemCount) {
        this.storeItemCount = storeItemCount;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }
}
