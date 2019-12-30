package com.max.rm.inventory;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.internal.gmsg.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eng.Reham Mokhtar on 28/5
 */

public class Api {



    public static  String BASE_URL="http://jazeerademo.webhop.net:8091/api/";

    Activity activity;
    RequestQueue queue;
    String user_id , org_id ;
    public Api(Activity activity) {
        this.activity = activity;
        this.queue= Volley.newRequestQueue(activity);
        SharedPreferences shared = activity.getSharedPreferences(keys.SharedPrefrenceName , 0) ;
        user_id=shared.getString(keys.USER_ID,"");
        org_id=shared.getString(keys.ORG_ID,"");
        if(shared.contains(keys.URl)){
            BASE_URL =shared.getString(keys.URl ,BASE_URL) ;
        }
    }
    public static String jsonFormat(String response){
        String n = response.substring(1, response.length() - 1);
        n = n.replaceAll("(\\\\r\\\\n|\\\\|)", "");
        return  n ;
    }
    public void login(final String org_id, final String user_name , final String password, final String mobile , final RequestInterface object){
        String url = BASE_URL+"Users";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = jsonFormat(response);
                object.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                object.onError();
              //  Dialog.window(error.toString() ,activity);

                Log.d("errorlogin", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("Org_id", org_id) ;
                map.put("user_id", user_name) ;
                map.put("password", password) ;
                map.put("Device_Name", mobile) ;
                return map;

            }
        }
        ;
     request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
    public void setting(final String org_id, final String user_name , final String mobile , final RequestInterface object){
        String url = BASE_URL+"Inventory";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result=   jsonFormat(response) ;
                object.onResponse(result);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                object.onError();
                Log.d("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("Org_id", org_id) ;
                map.put("user_id", user_name) ;
                map.put("FuncationType","GetInvSetting") ;
                map.put("Device_Name", mobile) ;
                return map;

            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
    public void getStores(final RequestInterface object){
        String url = BASE_URL+"Inventory";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = jsonFormat(response);
                object.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                object.onError();
                Log.d("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("Org_id", org_id) ;
                map.put("user_id", user_id) ;
                map.put("FuncationType", "GetInveontryList") ;
                return map;

            }
        };
       request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
     //dy  htt3ml local
    public void getItemByBarCode(final String barCode , final String storeId, final RequestInterface object){
        String url = BASE_URL+"Inventory";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = jsonFormat(response);
                object.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                object.onError();
                Log.d("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("Org_id", org_id) ;
                Log.d("orgid" ,org_id);
                map.put("user_id", user_id) ;
                Log.d("user_id" ,user_id);

                map.put("FuncationType", "GetItem_ByID/Barcode") ;
                map.put("ID_BarCode", barCode ) ;
                Log.d("barcode" ,barCode);

                map.put("WH_Code", storeId ) ;
                Log.d("WH_Code" ,storeId);

                return map;

            }
        };
 request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
    public void getItemByUnit(final String barCode , final String unit, final RequestInterface object){
        String url = BASE_URL+"Inventory";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = jsonFormat(response);
                object.onResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                object.onError();
                Log.d("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("Org_id", org_id) ;
                map.put("user_id", user_id) ;

                map.put("FuncationType", "GetItem_ByID_And_Unit") ;
                map.put("ID_BarCode", barCode ) ;
                map.put("Unit",unit) ;

                return map;

            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
    public void sendInventory(){

    }
     public String createPointString(ArrayList<itemObject> items ,inventoryObject inv , String phone_name ,String function_name,String inv_id){
        String data = "{\"FuncationType\":\""+function_name+"\""+
                      ",\"Org_id\":"+"\""+org_id+"\" "+
                      ",\"User_id\":"+"\""+user_id+"\" "+
                      ",\"Device_Name\":"+"\""+phone_name +"\""+
         ",\"MACH_INV\":{"+ inv.invData(inv_id) +
         ",\"MACH_INV_DTL\":[" ;
             for(int i = 0 ; i <items.size() ; i++) {
           String itemObject = items.get(i).itemData(inv_id) ;
            data =data+itemObject +"," ;

             }
           data=  data.substring( 0,data.length()-1) ;
             data=data+"]}}";



         return data  ;
     }
     static class SendInventoryPoint extends AsyncTask<String , Void , String>{
        RequestInterface onResponse ;
         public SendInventoryPoint(RequestInterface onResponse ) {
             this.onResponse=onResponse ;
         }

         @Override
         protected String doInBackground(String... strings) {
                // HttpURLConnection urlConn;
                 URL mUrl = null;
                 String output ="" ;
                 String line ;
                 String body =strings[0] ;
                  Log.d("body" , body);
                 try {
                     mUrl = new URL(BASE_URL+"Inventory" );

                   /*  urlConn = (HttpURLConnection) mUrl.openConnection();
                     urlConn.setRequestMethod("POST");
                     urlConn.setDoOutput(true);
                    urlConn.addRequestProperty("Content-Type", "application/" + "POST");*/

                     HttpURLConnection conn =(HttpURLConnection) mUrl.openConnection();
                     conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                     conn.setRequestProperty("Accept","application/json");
                     conn.setRequestMethod("POST");
                     conn.setDoOutput(true);
                     OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());


                     wr.write(body);
                     wr.flush();
                     InputStream input= conn.getInputStream() ;
                     BufferedReader reader = new BufferedReader(new InputStreamReader(input)) ;
                     while ((line=reader.readLine())!=null){
                        output=output+line ;
                     }



                 } catch (MalformedURLException e) {
                     e.printStackTrace();

                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             return output;
         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             keys.log("ttt" ,s);
             String result = jsonFormat(s);
             onResponse.onResponse(result);
             keys.log("output" ,result);
         }
     }
    static class downloadItems extends AsyncTask<String , Integer , String>{
        requestDownload onResponse ;
        public downloadItems(requestDownload onResponse ) {
            this.onResponse=onResponse ;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("progress"  , String.valueOf(values[0])) ;
             onResponse.onProgress(values);
        }
        int status = 0;

        @Override
        protected String doInBackground(String... strings) {
            // HttpURLConnection urlConn;
            for (int i = 0; i < Integer.parseInt(strings[1]); i++){
                status++;
                try {
                    publishProgress(status);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }}
                    URL mUrl = null;
            String output ="" ;
            String line ;
            String body = strings[0] ;
          //  Log.d("body" , body);
            try {
                mUrl = new URL(BASE_URL+"Inventory" );

                   /*  urlConn = (HttpURLConnection) mUrl.openConnection();
                     urlConn.setRequestMethod("POST");
                     urlConn.setDoOutput(true);
                    urlConn.addRequestProperty("Content-Type", "application/" + "POST");*/

                HttpURLConnection conn =(HttpURLConnection) mUrl.openConnection();
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(body);
                wr.flush();
                InputStream input= conn.getInputStream() ;
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                BufferedReader reader = new BufferedReader(new InputStreamReader(input)) ;
                while ((line=reader.readLine())!=null){

                    output=output+line ;
                //    publishProgress((int) (output.length() * 100 / 5000));

                }



            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return output;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            keys.log("ttt" ,s);
            String result = jsonFormat(s);
            onResponse.onResponse(result);
            keys.log("output" ,result);
        }
    }
      public void testVolley(String body){
          RequestQueue requestQueue = Volley.newRequestQueue(activity);
          String URL =BASE_URL+"Inventory";
          JSONObject jsonBody = new JSONObject();

          final String requestBody = body ;

          StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                  Log.i("VOLLEY", response);
              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  Log.e("VOLLEY", error.toString());
              }
          }) {
              @Override
              public String getBodyContentType() {
                  return "application/json; charset=utf-8";
              }

              @Override
              public byte[] getBody() throws AuthFailureError {
                  try {
                      return requestBody == null ? null : requestBody.getBytes("utf-8");
                  } catch (UnsupportedEncodingException uee) {
                      VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                      return null;
                  }
              }

              @Override
              protected Response<String> parseNetworkResponse(NetworkResponse response) {
                  String responseString = "";
                  if (response != null) {
                      responseString = String.valueOf(response.statusCode);
                      // can get more details such as response.headers
                  }
                  return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
              }
          };
          stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


          requestQueue.add(stringRequest);
      }
       public void getInvList(final RequestInterface object , final String phone ){
           String url = BASE_URL+"Inventory";
           StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   String result = jsonFormat(response);
                   object.onResponse(result);
                   Log.d("result", result);

               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   object.onError();
                   Log.d("error", error.toString());
               }
           }){
               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   Map<String, String> map= new HashMap<String, String>();
                   map.put("Org_id", org_id) ;
                   map.put("Device_Name", phone) ;
                   map.put("FuncationType", "GET_MACH_INV_MST") ;
                   map.put("user_id", user_id) ;


                   return map;

               }
           };
           request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


           queue.add(request);

       }
        public void getInvItems(final String inv_id , final String phone , final RequestInterface object){
            String url = BASE_URL+"Inventory";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String result = jsonFormat(response);
                    object.onResponse(result);
                    Log.d("resultitems", result);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    object.onError();
                    Log.d("error", error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map= new HashMap<String, String>();
                    map.put("Org_id", org_id) ;
                    map.put("FuncationType", "GET_MACH_INV_DTL") ;
                   // map.put("User_id", inv_id) ;
                  map.put("MACH_INV", "{" +
                            " \"INV_NO\":\""+inv_id+"\"" +
                            "}") ;


                    return map;

                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            queue.add(request);

        }
         public String deleteBody(String function_type , String inv_no,String record_id){
            String s="{\n" +
                    "\"FuncationType\":\""+function_type+"\",\n" +
                    "\"Org_id\":\""+org_id+"\",\n" +
                    "\"User_id\":\""+user_id+"\",\n" +
                    "\"MACH_INV\":\n" +
                    "       {\n" +
                    "           \"INV_NO\":\""+inv_no+"\",\n" +
                    "            \"MACH_INV_DTL\":[\n" +
                    " {\"RERD_NO\":\""+record_id+"\",\"INV_NO\":\""+inv_no+"\"}\n" +
                    "]\n" +
                    "}\n" +
                    "}" ;
             return  s;
             }
              public String delete_inv_body(String inv_id){
String s= "{\n" +
        "\"FuncationType\":\"Delete_MACH_INV\",\n" +
        "\"Org_id\":\""+org_id+"\",\n" +
        "\"User_id\":\""+user_id+"\",\n" +
        "\"Device_Name\":\""+Login.getMacAddr()+"\",\n" +
        "\"MACH_INV\":\n" +
        "       {\n" +
        "           \"INV_NO\":\""+inv_id+"\"\n" +
        "       }\n" +
        "}" ;
return  s;
              }
               public  void getItemsCount  ( final String org_id , final RequestInterface object){
                   String url = BASE_URL+"Inventory";
                   StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           String result = jsonFormat(response);
                           object.onResponse(result);
                           Log.d("resultitems", result);

                       }
                   }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           object.onError();
                           Log.d("error", error.toString());
                       }
                   }){
                       @Override
                       protected Map<String, String> getParams() throws AuthFailureError {
                           Map<String, String> map= new HashMap<String, String>();
                           map.put("Org_id", org_id) ;
                           map.put("FuncationType", "GetItemCount") ;




                           return map;

                       }
                   };
                   request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                   queue.add(request);

               }
                public ArrayList<itemObject >  getItemByIdBarCodeSqlite  (String BarCodeID){
                    dataBase db = new dataBase(activity);
                    ArrayList<itemObject> list  = new ArrayList<>() ;
                    SQLiteDatabase sql = db.getReadableDatabase();
                    Cursor cursor = sql.rawQuery("select * from " +
                                    keys.All_ITEMS_TableName + " where " + keys._item_id + " = ? OR " + keys._item_barcode +" = ?",

                            new String[] { BarCodeID , BarCodeID});
                  /* Cursor cursor = sql.query(keys.All_ITEMS_TableName, null,
                            keys._item_id + " = ? OR " + keys._item_barcode + " = ?",
                            new String[] {"%" + BarCodeID + "%", "%" + BarCodeID + "%"},
                            null, null, null, null);*/

                    if(cursor.moveToFirst()) {
                        do {
                            String item_name = cursor.getString(cursor.getColumnIndex(keys._item_name)) ;
                            String item_size  = cursor.getString(cursor.getColumnIndex(keys._item_size)) ;
                            String item_id = cursor.getString(cursor.getColumnIndex(keys._item_id)) ;
                            String item_barCode = cursor.getString(cursor.getColumnIndex(keys._item_barcode)) ;
                            String item_unit = cursor.getString(cursor.getColumnIndex(keys._item_unit)) ;
                            String item_store = cursor.getString(cursor.getColumnIndex(keys._item_store_num)) ;
                            String batch_num = cursor.getString(cursor.getColumnIndex(keys._BACH_NUM)) ;
                            String expire_date = cursor.getString(cursor.getColumnIndex(keys._EXPIRE_DATE)) ;
itemObject  item = new itemObject(item_id , item_barCode , item_name , item_size , item_unit , " " , item_store , batch_num, expire_date) ;
 list.add(item)  ;


                        }while (cursor.moveToNext()) ;
                    }

      return list ;
    }
}



