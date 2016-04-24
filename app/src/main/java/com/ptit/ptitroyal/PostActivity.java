package com.ptit.ptitroyal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.models.PostContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Manh on 4/21/16.
 */
public class PostActivity extends AppCompatActivity {
    private Spinner spHashtag;
    private EditText edContent;
    private TextView tvHashtag;
    private ImageView imageSelect;
    private Button btnPost;
    private ImageView imagePreview;
    private int PICK_IMAGE_REQUEST = 1;
    private String token = MainActivity.accessToken;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_status);
        btnPost = (Button) findViewById(R.id.btn_post);
        edContent = (EditText) findViewById(R.id.ed_content);
        tvHashtag = (TextView) findViewById(R.id.tv_hashtag);
        imageSelect = (ImageView) findViewById(R.id.image_select);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        spHashtag = (Spinner) findViewById(R.id.spiner_hashtag);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hashtag_array, R.layout.item_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spHashtag.setAdapter(adapter);
        // spHashtag.getSelectedItem();
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
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

    public void selectHashtag() {

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
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, true);

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

    public void doPostStatus() throws JSONException {


        String image = getStringImage(bitmap);

        String content = edContent.getText().toString();

        String topic = spHashtag.getSelectedItem().toString();
        Log.d("manh", topic);
        if (content == "null") return;
        //topic = "Giải Trí";
        switch (topic) {
            case "Giải Trí":
                topic = "relax";
                break;
            case "Ăn Uống":
                topic = "food";
                break;
            case "Học Tập":
                topic = "study";
                break;
        }
        PostContent postContent = new PostContent(content, image, topic);

        try {
            APIConnection.doPostStatus(this, postContent, token, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("manh", "success");
                    showToast("Success");
                    //intent

                }

                @Override
                public void onError(VolleyError error) {
                    Log.d("manh", "fail");
                    showToast("Hãy thử lại");

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String error) {
        Toast.makeText(getApplicationContext(), error,
                Toast.LENGTH_LONG).show();
    }

}
