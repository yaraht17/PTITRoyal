package com.ptit.ptitroyal.cores;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.Serializable;

public abstract class CoreActivity extends AppCompatActivity implements Serializable {
    protected String TAG = "Core Activity";

    public void showSnackBar(View v, String msg, String action, OnClickListener o) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction(action, o).show();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }


    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void loge(final Object msg) {
        if (msg == null) {
            Log.e(TAG, "NULL");
        } else {
            Log.e(TAG, msg.toString());
        }
    }

    public void logd(final Object msg) {
        if (msg == null) {
            Log.d(TAG, "NULL");
        } else {
            Log.d(TAG, msg.toString());
        }
    }

    public void logi(final Object msg) {
        if (msg == null) {
            Log.i(TAG, "NULL");
        } else {
            Log.i(TAG, msg.toString());
        }
    }

    public void showToastShort(final Object message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message == null) {
                    Toast.makeText(CoreActivity.this, "NULL", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CoreActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        logi(new StringBuilder().append("Done. Show short Toast: ").append(message).toString());
    }

    public void showToastLong(final Object message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message == null) {
                    Toast.makeText(CoreActivity.this, "NULL", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CoreActivity.this, message.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        logi(new StringBuilder().append("Done. Show long Toast: ").append(message).toString());
    }

    public String readSharedPreferences(String preferencesName, String elementName) {
        SharedPreferences pre = getSharedPreferences(preferencesName, MODE_PRIVATE);
        String s = pre.getString(elementName, "");
        logi(new StringBuilder().append("Done. Read shared preferences [ ")
                .append(preferencesName).append("]:[").append(elementName)
                .append("]: ").append(s).toString());
        return s;
    }

    public void saveSharedPreferences(String preferencesName, String elementName, String data) {
        SharedPreferences pre = getSharedPreferences(preferencesName, MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putString(elementName, data);
        edit.commit();
        logi(new StringBuilder().append("Done. Write shared preferences [ ")
                .append(preferencesName).append("]:[").append(elementName)
                .append("]: ").append(data).toString());
    }


    protected ProgressDialog pDialog;

    public void showDialog(String msg) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(msg);
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
    }

    public void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
