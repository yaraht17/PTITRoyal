package com.ptit.ptitroyal.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.ptit.ptitroyal.MainActivity;
import com.ptit.ptitroyal.R;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivitiesFragment extends Fragment {

    private List<Post> posts;
    private RecyclerView rvActivitiesPosts;
    private PostsAdapter adapter;
    private View rootView;
    private SwipyRefreshLayout swipyRefreshActivities;
    private ProgressBar progressBar;

    public ActivitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_activities, container, false);
        rvActivitiesPosts = (RecyclerView) rootView.findViewById(R.id.rvActivitiesPosts);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        posts = new ArrayList<>();
        getNewPost();
        adapter = new PostsAdapter(getActivity(), posts);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvActivitiesPosts.setLayoutManager(layoutManager);
        rvActivitiesPosts.setItemAnimator(new DefaultItemAnimator());
        rvActivitiesPosts.setAdapter(adapter);

        swipyRefreshActivities = (SwipyRefreshLayout) rootView.findViewById(R.id.swipyRefreshActivities);
        swipyRefreshActivities.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    refresh();
                } else {
                    loadMore();
                }
                swipyRefreshActivities.setRefreshing(false);
            }
        });

        return rootView;
    }


    private void refresh() {
        APIConnection.getNumberOfNotifications(getActivity(), MainActivity.accessToken);
        getNewPost();
    }

    private void loadMore() {
    }

    private void getNewPost() {

        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.URL_HOST + "/api/topics/" + Constants.ACTIVITIES + "/posts";
        APIConnection.get(getActivity(), url, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("TienDH", "posts " + response.toString());
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
                        Toast.makeText(getActivity(), Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
