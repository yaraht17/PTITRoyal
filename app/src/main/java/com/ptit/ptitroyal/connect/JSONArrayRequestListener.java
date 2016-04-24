package com.ptit.ptitroyal.connect;

import com.android.volley.VolleyError;

import org.json.JSONArray;


public interface JSONArrayRequestListener {
    public void onResponse(JSONArray response);

    public void onError(VolleyError error);
}
