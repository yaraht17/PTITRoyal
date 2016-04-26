package com.ptit.ptitroyal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.MainActivity;
import com.ptit.ptitroyal.PostDetailActivity;
import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Post;
import com.ptit.ptitroyal.view.AwesomeTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Ken on 16/04/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUsername, tvTimePost, tvContent, tvNumOfLikes, tvNumOfCmts;
        public AwesomeTextView txtTopic;
        public ImageView imgAvatar, imgContent, imgLike, imgComment;
        public LinearLayout layoutLike, layoutCmt;

        public PostViewHolder(View v) {
            super(v);
            imgAvatar = (ImageView) v.findViewById(R.id.imgAvatar);
            tvUsername = (TextView) v.findViewById(R.id.tvUsername);
            tvTimePost = (TextView) v.findViewById(R.id.tvTimePost);
            tvContent = (TextView) v.findViewById(R.id.tvContent);
            tvNumOfLikes = (TextView) v.findViewById(R.id.tvNumOfLikes);
            tvNumOfCmts = (TextView) v.findViewById(R.id.tvNumOfCmts);
            imgContent = (ImageView) v.findViewById(R.id.imgContent);
            imgLike = (ImageView) v.findViewById(R.id.imgLike);
            imgComment = (ImageView) v.findViewById(R.id.imgComment);
            txtTopic = (AwesomeTextView) v.findViewById(R.id.txtTopic);
            layoutCmt = (LinearLayout) v.findViewById(R.id.layoutCmt);
            layoutLike = (LinearLayout) v.findViewById(R.id.layoutLike);
        }
    }

    public PostsAdapter(Context context) {
        this.context = context;
    }

    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_list, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        Post post = postList.get(position);
        Picasso.with(context)
                .load(post.getAuthor().getAvatarURI())
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .into(holder.imgAvatar);
        holder.tvUsername.setText(post.getAuthor().getUsername());
        holder.tvTimePost.setText(post.getTime());
        holder.tvContent.setText(post.getContent());
        String imageURI = post.getImageURI();
        holder.tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
        holder.tvNumOfCmts.setText(String.valueOf(post.getNumberOfComments()));
        holder.txtTopic.setText(switchTag(post.getTopic()));
        if (imageURI != null && !imageURI.equals("")) {
            Picasso.with(context)
                    .load(Constants.URL_HOST + "/" + post.getImageURI())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.imgContent);
            holder.imgContent.setVisibility(View.VISIBLE);
        } else {
            holder.imgContent.setVisibility(View.GONE);
        }

        if (post.isLiked()) {
            holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like));
        } else {
            holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like_inactive));
        }

        holder.layoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLike(postList.get(position), holder);
            }
        });

        holder.layoutCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickComment(position);
            }
        });
    }

    private String switchTag(String tag) {
        switch (tag) {
            case "study":
                return context.getString(R.string.icon_study);
            case "food":
                return context.getString(R.string.icon_food);
            case "relax":
                return context.getString(R.string.icon_relax);
        }
        return context.getString(R.string.icon_tag);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void onClickLike(Post post, PostViewHolder holder) {
        if (post.isLiked()) {
            unlike(post, holder);
        } else {
            like(post, holder);
        }
    }

    private void onClickComment(int position) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("postID", postList.get(position).getId());
        context.startActivity(intent);
    }

    private void like(final Post post, final PostViewHolder holder) {
        String url = Constants.URL_HOST + "/api/posts/" + post.getId() + "/likes";
        APIConnection.post(context, url, null, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("TienDH", "Reponse " + response.toString());
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        Log.d("FOO", "liked");
                        post.incLike();
                        holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like));
                        post.setLiked(!post.isLiked());
                        holder.tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
                    } else {
                        Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlike(final Post post, final PostViewHolder holder) {
        String url = Constants.URL_HOST + "/api/posts/" + post.getId() + "/likes";
        APIConnection.delete(context, url, null, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("TienDH", "Reponse " + response.toString());
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        Log.d("FOO", "disliked");
                        post.decLike();
                        holder.imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like_inactive));
                        post.setLiked(!post.isLiked());
                        holder.tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
                    } else {
                        Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, Constants.SERVER_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
