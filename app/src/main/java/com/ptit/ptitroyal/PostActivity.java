package com.ptit.ptitroyal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.adapter.SpinnerTopicAdapter;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.cores.CoreActivity;
import com.ptit.ptitroyal.models.PostContent;
import com.ptit.ptitroyal.models.Topic;
import com.ptit.ptitroyal.view.AwesomeButton;
import com.ptit.ptitroyal.view.AwesomeTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Manh on 4/21/16.
 */
public class PostActivity extends CoreActivity {
    private Spinner spHashtag;
    private EditText edContent;
    private TextView tvHashtag;
    private AwesomeTextView imageSelect;
    private ImageView imagePreview;
    private int PICK_IMAGE_REQUEST = 1;
    private String token = MainActivity.accessToken;

    private TextView txtTitle;
    private AwesomeButton btnLeft;
    private Button btnRight;

    private Bitmap bitmap;
    private ArrayList<Topic> topics;
    private SpinnerTopicAdapter spAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_status);
        edContent = (EditText) findViewById(R.id.ed_content);
        tvHashtag = (TextView) findViewById(R.id.tv_hashtag);
        imageSelect = (AwesomeTextView) findViewById(R.id.image_select);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        spHashtag = (Spinner) findViewById(R.id.spiner_hashtag);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        createTopic();
        spAdapter = new SpinnerTopicAdapter(this, -1, topics);


        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHashtag.setAdapter(spAdapter);
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        txtTitle.setText("Đăng lên PTIT Royal");

        btnLeft.setText(getString(R.string.icon_back));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doPostStatus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void selectImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                ImageView imageView = (ImageView) findViewById(R.id.image_preview);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void createTopic() {
        topics = new ArrayList<>();
        topics.add(new Topic("Giải trí", getString(R.string.icon_relax)));
        topics.add(new Topic("Ăn uống", getString(R.string.icon_food)));
        topics.add(new Topic("Học tập", getString(R.string.icon_study)));
    }

    public void doPostStatus() throws JSONException {
        String image = "";

        if (bitmap != null) {
            image = getStringImage(bitmap);
        }
        String content = edContent.getText().toString();

        Topic topicObj = (Topic) spHashtag.getSelectedItem();
        String topic = topicObj.getName();
        Log.d("manh", topicObj.getName());
        if (content == "null") return;
        switch (topicObj.getName()) {
            case "Giải trí":
                topic = "relax";
                break;
            case "Ăn uống":
                topic = "food";
                break;
            case "Học tập":
                topic = "study";
                break;
        }
        PostContent postContent = new PostContent(content, image, topic);
        showDialog("Đăng bài");
        try {
            APIConnection.doPostStatus(this, postContent, token, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("manh", "success");
                    hideDialog();
                    showToastLong("Đăng bài thành công");
                    //intent

                    finish();
                }

                @Override
                public void onError(VolleyError error) {
                    hideDialog();
                    Log.d("manh", "fail");
                    showToastLong(getString(R.string.request_error));

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
