package com.ptit.ptitroyal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.adapter.ChatAdapter;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.StringRequestListener;
import com.ptit.ptitroyal.models.ChatMessage;
import com.ptit.ptitroyal.view.AwesomeButton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

public class ChatBotActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtTitle;
    private AwesomeButton btnLeft;
    private Button btnRight;

    private static final int REQUEST_CODE = 1234;

    private ListView messageListView;
    private TextView btnVoice, btnSend;
    private EditText etChat;

    private ChatAdapter chatAdapter;

    private ArrayList<ChatMessage> messageList = new ArrayList<>();

    private Typeface font_awesome;
    private int screenHeight, screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        txtTitle.setText("Xàm cùng PTIT BOT");

        btnLeft.setText(getString(R.string.icon_back));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRight.setVisibility(View.INVISIBLE);

        //lay kich co man hinh
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //an thanh nhap text
        font_awesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        btnSend = (TextView) findViewById(R.id.btnSend);
        btnVoice = (TextView) findViewById(R.id.btnVoice);
        etChat = (EditText) findViewById(R.id.etChat);
        etChat.setOnFocusChangeListener(null);
        messageListView = (ListView) findViewById(R.id.list_chat);

        chatAdapter = new ChatAdapter(this, R.layout.item_chat_left, messageList);
        messageListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageListView.setAdapter(chatAdapter);

        btnSend.setTypeface(font_awesome);
        btnVoice.setTypeface(font_awesome);

        btnVoice.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:

                sendChatMessage(etChat.getText().toString());
                break;

            case R.id.btnVoice:
                // an thanh nhap text va mo google voice
                startVoiceRecognitionActivity();
                break;

            default:
                break;
        }


    }


    //mo google voice
    private void startVoiceRecognitionActivity() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Điện thoại của bạn không hỗ trợ Google Voice", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //tra ve ket qua google voice
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Populate the wordsList with the String values the recognition
            // engine thought it heard
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() != 0) {
                sendChatMessage(matches.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //them tin nhan
    private void sendChatMessage(String message) {
        if (message == null || message.equals("")) return;
        chatAdapter.add(new ChatMessage(false, message, ""));


        try {
            APIConnection.getChatMessage(this, message, new StringRequestListener() {
                @Override
                public void onSuccess(String response) {
                    chatAdapter.add(new ChatMessage(true, response, ""));
                }


                @Override
                public void onError(VolleyError error) {
                    chatAdapter.add(new ChatMessage(true, "Xảy ra lỗi, vui lòng thử lại!", ""));
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        etChat.setText("");
    }

}
