package com.ptit.ptitroyal.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class GastronomyFragment extends Fragment {


    private List<Post> posts;
    private RecyclerView rvStudyGastronomy;
    private PostsAdapter adapter;
    private View rootView;
    private SwipyRefreshLayout swipyRefreshGastronomy;

    public GastronomyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_gastronomy, container, false);
        rvStudyGastronomy = (RecyclerView) rootView.findViewById(R.id.rvGastronomyPosts);
        adapter = new PostsAdapter(getActivity(), posts);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvStudyGastronomy.setLayoutManager(layoutManager);
        rvStudyGastronomy.setItemAnimator(new DefaultItemAnimator());
        rvStudyGastronomy.setAdapter(adapter);

        swipyRefreshGastronomy = (SwipyRefreshLayout) rootView.findViewById(R.id.swipyRefreshGastronomy);
        swipyRefreshGastronomy.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    refresh();
                } else {
                    loadMore();
                }
                swipyRefreshGastronomy.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        getNewPost();
    }


    private void refresh() {
        Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_SHORT).show();
        APIConnection.getNumberOfNotifications(getActivity(), MainActivity.accessToken);
        getNewPost();
    }

    private void loadMore() {
        Toast.makeText(getActivity(), "Load more", Toast.LENGTH_SHORT).show();
    }

    private void getNewPost() {
        String url = Constants.URL_HOST + "/api/topics/" + Constants.GASTRONOMY + "/posts";
        APIConnection.get(getActivity(), url, MainActivity.accessToken, new VolleyCallback() {
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
                Toast.makeText(getActivity(), Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
