package com.max.rm.inventory;

public interface requestDownload {
    public void onResponse(String response);
    public void onError(String Error);
     public void onProgress(Integer [] p) ;
}
