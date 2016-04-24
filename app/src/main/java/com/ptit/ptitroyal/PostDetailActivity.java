package com.ptit.ptitroyal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PostDetailActivity extends AppCompatActivity {

    private Author user;
    private Post post;
    private ArrayList<Comment> comments;

    private EditText edInput;

    private ListView listView;
    private PostDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        user = (Author) intent.getSerializableExtra("user");
        post = (Post) intent.getSerializableExtra("post");
        comments = new ArrayList<Comment>();

        initComponents();
        getComments();
//        fakeData();
    }

    private void initComponents() {
        listView = (ListView) findViewById(R.id.listView);
        adapter = new PostDetailAdapter(this, post, comments);
        listView.setAdapter(adapter);
        edInput = (EditText) findViewById(R.id.edInput);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void getComments() {
        String url = Constants.URL_HOST + "/api/posts/" + post.getId();
        APIConnection.get(this, url, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {

                        JSONArray jsonArray = response.getJSONObject("result").getJSONArray("comments");
                        comments.clear();
                        int n = jsonArray.length();
                        for (int i = 0; i < n; ++i) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            Author user = JSONParser.parseAuthor(object.getJSONObject("author"));
                            String content = object.getString("content");
                            String time = object.getString("create_at");

                            comments.add(new Comment("", user, null, content, time));
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
            String url = Constants.URL_HOST + "/api/posts/" + post.getId() + "/comments";
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
