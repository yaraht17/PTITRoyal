package com.ptit.ptitroyal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.adapter.PostDetailAdapter;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONParser;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Author;
import com.ptit.ptitroyal.models.Comment;
import com.ptit.ptitroyal.models.Post;
import com.ptit.ptitroyal.view.AwesomeButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PostDetailActivity extends AppCompatActivity {

    private TextView txtTitle;
    private AwesomeButton btnLeft;
    private Button btnRight;

    private String postID;
    private Post post;
    private ArrayList<Comment> comments;

    private EditText edInput;
    private ProgressBar progressBar;
    private ListView listView;
    private PostDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtTitle.setText("Chi tiết bài viết");
        btnLeft.setText(getString(R.string.icon_back));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        btnRight.setVisibility(View.INVISIBLE);
        comments = new ArrayList<Comment>();


        initComponents();
        getComments();
    }

    private void initComponents() {
        listView = (ListView) findViewById(R.id.listView);
        adapter = new PostDetailAdapter(this, null, comments);
        edInput = (EditText) findViewById(R.id.edInput);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickBack() {
        finish();
    }


    public void getComments() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.URL_HOST + "/api/posts/" + postID;
        APIConnection.get(this, url, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        JSONObject result = response.getJSONObject("result");
                        Author author = JSONParser.parseAuthor(result.getJSONObject("author"));
                        String content = result.getString("content");
                        String image = "";
                        try {
                            image = result.getString("image");
                        } catch (JSONException e) {

                        }
                        int numOfLikes = result.getJSONArray("likes").length();
                        int numOfComments = result.getJSONArray("comments").length();
                        String time = result.getString("create_date");
                        boolean isLiked = result.getBoolean("liked");
                        String topic = result.getString("topic");
                        post = new Post(postID, author, time, content, image, numOfLikes, numOfComments, isLiked, topic);
                        adapter.setPost(post);
                        listView.setAdapter(adapter);

                        JSONArray jsonArray = result.getJSONArray("comments");
                        comments.clear();
                        int n = jsonArray.length();
                        for (int i = 0; i < n; ++i) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            Author user = JSONParser.parseAuthor(object.getJSONObject("author"));
                            String commentContent = object.getString("content");
                            String commentTime = object.getString("create_at");

                            comments.add(new Comment("", user, null, commentContent, commentTime));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(PostDetailActivity.this, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("FOO", "Convert fail");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PostDetailActivity.this, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickComment(View v) {
        String cmt = edInput.getText().toString();
        if (cmt.equals("")) {
            return;
        }

        edInput.setText("");

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("content", cmt);
            String url = Constants.URL_HOST + "/api/posts/" + postID + "/comments";
            APIConnection.post(this, url, requestBody, MainActivity.accessToken, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if (status.equals(Constants.SUCCESS)) {
                            Log.d("FOO", "commented");
                            getComments();
                        } else {
                            Toast.makeText(PostDetailActivity.this, status, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("FOO", "Convert fail");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(PostDetailActivity.this, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
