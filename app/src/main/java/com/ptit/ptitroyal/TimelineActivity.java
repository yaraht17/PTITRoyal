package com.ptit.ptitroyal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    private List<Post> posts;
    private RecyclerView rvTimelinePosts;
    private PostsAdapter adapter;
    private SwipyRefreshLayout swipyRefreshTimline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profiles);

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
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        APIConnection.getNumberOfNotifications(this, MainActivity.accessToken);
        getOwnPost();
    }

    private void loadMore() {
        Toast.makeText(this, "Load more", Toast.LENGTH_SHORT).show();
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
}
