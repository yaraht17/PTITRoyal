package com.ptit.ptitroyal.connect;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ptit.ptitroyal.MainActivity;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Noti;
import com.ptit.ptitroyal.models.PostContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APIConnection {

    public static void login(Context context, final String facebookToken, final JSONObjectRequestListener callback) {

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(Constants.FACEBOOK_TOKEN, facebookToken);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, Constants.API_LOGIN, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                            Log.d("TienDH", "Respone Success login: " + response.toString());
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onError(error);
                            Log.d("TienDH", "Respone Login Error: " + error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("charset", "utf-8");
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //using for logout, validate token, get profile
    public static void request(Context context, String url, final String accessToken, final JSONObjectRequestListener callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                        Log.d("TienDH", "Respone Request Success: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        Log.d("TienDH", "Respone Request Error: " + error.toString());
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "access_token " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }


    //update gcm id
    public static void updateGCMID(Context context, final String accessToken, String gcmID, final JSONObjectRequestListener callback) {

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put(Constants.GCM_ID, gcmID);
            Log.d("TienDH", "json send: " + jsonBody.toString());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, Constants.API_GCM, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("TienDH", "Update GCM ID Success: " + response.toString());
                            callback.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TienDH", "Update GCM ID Error: " + error.toString());
                            callback.onError(error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "access_token " + accessToken);
//                    params.put("Content-Type", "application/json");
//                    params.put("charset", "utf-8");
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void getNumberOfNotifications(final Context context, final String accessToken) {
        APIConnection.get(context, Constants.GET_NUMBER_OF_NOTIFICATIONS_URL, accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        MainActivity.setNumberOfNotifications(response.getInt("result"));
                    } else {
                        Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("FOO", "Convert fail");
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void get(Context context, String url, final String accessToken, final VolleyCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void post(Context context, String url, JSONObject requestBody, final String accessToken, final VolleyCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void delete(Context context, String url, JSONObject requestBody, final String accessToken, final VolleyCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void getNotifications(Context context, String url, final String accessToken, final VolleyCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }



    public static void makeNotiRead(Context context, String url, final String accessToken, final VolleyCallback callback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void doPostStatus(Context context, PostContent postContent, final String accessToken, final VolleyCallback callback) throws JSONException {
        final JSONObject jsonBody = new JSONObject();
        jsonBody.put(Constants.CONTENT, postContent.getContent());
        jsonBody.put(Constants.IMAGE_UPLOAD, postContent.getImage());
        String url = Constants.DO_POST_STATUS;
        url = url.replace(Constants.POST_ID_TOPIC_REPLACE, postContent.getTopic());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "access_token " + accessToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

}
