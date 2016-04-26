package com.ptit.ptitroyal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.ptit.ptitroyal.adapter.PostsAdapter;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.JSONParser;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Post;
import com.ptit.ptitroyal.models.User;
import com.ptit.ptitroyal.view.AwesomeButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimelineActivity extends AppCompatActivity {

    private List<Post> posts;
    private RecyclerView rvTimelinePosts;
    private PostsAdapter adapter;
    private SwipyRefreshLayout swipyRefreshTimline;

    private TextView txtTitle;
    private AwesomeButton btnLeft;
    private Button btnRight;
    private User mUser;

    private ImageView imgCover, imgAvatar;
    private TextView txtName, txtEmail, txtGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        mUser = (User) getIntent().getExtras().getSerializable("USER");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnLeft = (AwesomeButton) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtGender = (TextView) findViewById(R.id.txtGender);

        btnLeft.setText(getString(R.string.icon_back));
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtTitle.setText(mUser.getName());
        btnRight.setVisibility(View.INVISIBLE);
        txtName.setText(mUser.getName());
        txtEmail.setText(mUser.getEmail());
        txtGender.setText(mUser.getGender());

        Picasso.with(TimelineActivity.this)
                .load(mUser.getAvatar())
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .into(imgAvatar);
        setCoverPhoto();
//        Picasso.with(TimelineActivity.this)
//                .load(mUser.getCover())
//                .placeholder(R.drawable.nav_header_bg)
//                .error(R.drawable.nav_header_bg)
//                .into(imgCover);

        rvTimelinePosts = (RecyclerView) findViewById(R.id.rvTimelinePosts);
        posts = new ArrayList<>();
        adapter = new PostsAdapter(this, posts);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTimelinePosts.setLayoutManager(layoutManager);
        rvTimelinePosts.setItemAnimator(new DefaultItemAnimator());
        rvTimelinePosts.setAdapter(adapter);

        swipyRefreshTimline = (SwipyRefreshLayout) findViewById(R.id.swipyRefreshTimeline);
        swipyRefreshTimline.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    refresh();
                } else {
                    loadMore();
                }
                swipyRefreshTimline.setRefreshing(false);
            }
        });

        getOwnPost();
    }

    private void refresh() {
        APIConnection.getNumberOfNotifications(this, MainActivity.accessToken);
        getOwnPost();
    }

    private void loadMore() {

    }

    private void getOwnPost() {
        String url = Constants.URL_HOST + "/api/me/posts";
        APIConnection.get(this, url, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        JSONArray jsonArray = response.getJSONArray("result");
                        posts.clear();
                        int n = jsonArray.length();
                        for (int i = 0; i < n; ++i) {
                            posts.add(JSONParser.parsePost(jsonArray.getJSONObject(i)));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TimelineActivity.this, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TimelineActivity.this, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int[] coverImages = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4};

    private void setCoverPhoto() {
        Random rd = new Random();
        int x = rd.nextInt(4);
        imgCover.setImageResource(coverImages[x]);
    }
}
