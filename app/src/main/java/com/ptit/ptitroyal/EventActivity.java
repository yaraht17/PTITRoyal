package com.ptit.ptitroyal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.view.AwesomeButton;

public class EventActivity extends AppCompatActivity {
    private TextView txtTitle;
    private AwesomeButton btnLeft;
    private Button btnRight;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(Constants.URL_HOST + "/event");
        txtTitle.setText("Sự kiện");

        btnLeft.setText(getString(R.string.icon_back));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRight.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
