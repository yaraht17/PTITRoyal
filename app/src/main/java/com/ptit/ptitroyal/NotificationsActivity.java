package com.ptit.ptitroyal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.adapter.NotifyAdapter;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONParser;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Noti;

import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Noti> listNoti = new ArrayList<Noti>();
    private ListView list;
    private NotifyAdapter notifyAdapter;
    private ProgressBar progressBar;
    private String token = MainActivity.accessToken;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_notifications);
        list = (ListView) findViewById(R.id.lv_notify);
        list.setOnItemClickListener(NotificationsActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) findViewById(R.id.item_notify);
        progressBar.setVisibility(View.VISIBLE);


        APIConnection.getNotifications(this, Constants.GET_NOTIS, token, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
                listNoti = JSONParser.parseNotifications(response);
                Log.d("Manh", String.valueOf(listNoti.size()));
                notifyAdapter = new NotifyAdapter(NotificationsActivity.this, -1, listNoti);
                list.setAdapter(notifyAdapter);

            }

            @Override
            public void onError(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("manh", error.toString());
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String idNoti = listNoti.get(position).getId();
        APIConnection.makeNotiRead(NotificationsActivity.this, Constants.URL_HOST + Constants.GET_NOTIS + "/"+ idNoti, token, new VolleyCallback() {

            @Override
            public void onSuccess(JSONObject response) {
                //intent next activity
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
