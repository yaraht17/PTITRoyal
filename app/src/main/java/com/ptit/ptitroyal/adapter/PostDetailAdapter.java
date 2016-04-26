package com.ptit.ptitroyal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ptit.ptitroyal.MainActivity;
import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.connect.APIConnection;
import com.ptit.ptitroyal.connect.VolleyCallback;
import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Author;
import com.ptit.ptitroyal.models.Comment;
import com.ptit.ptitroyal.models.Post;
import com.ptit.ptitroyal.view.AwesomeTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ken on 24/04/2016.
 */
public class PostDetailAdapter extends BaseAdapter {

    private Context context;
    private Post post;
    private List<Comment> comments;
    private LayoutInflater inflater;
    private ImageView imgLike;
    private TextView tvNumOfLikes;
    private AwesomeTextView txtTopic;

    static class ViewHolder {
        public TextView tvCommentUsername, tvReplyTo, tvCommentContent, tvCommentTime;
        public ImageView imgCommentAvatar;
    }

    public PostDetailAdapter(Context context, Post post, List<Comment> comments) {
        this.context = context;
        this.post = post;
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return comments.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return post;
        }
        return comments.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = inflater.inflate(R.layout.item_post_list, null);
            getPostView(convertView);
            return convertView;
        } else {
            convertView = inflater.inflate(R.layout.item_comment_list, null);
            getCommentView(position, convertView);
            if (convertView == null) {
                Log.d("FOO", "null 2");
            }
            return convertView;
        }
    }

    public void setPost(Post post) {
        this.post = post;
    }

    private void getPostView(View convertView) {
        TextView tvUsername, tvTimePost, tvContent, tvNumOfCmts;

        ImageView imgAvatar, imgContent, imgComment;
        imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatar);
        tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvTimePost = (TextView) convertView.findViewById(R.id.tvTimePost);
        tvContent = (TextView) convertView.findViewById(R.id.tvContent);
        tvNumOfLikes = (TextView) convertView.findViewById(R.id.tvNumOfLikes);
        tvNumOfCmts = (TextView) convertView.findViewById(R.id.tvNumOfCmts);
        imgContent = (ImageView) convertView.findViewById(R.id.imgContent);
        imgLike = (ImageView) convertView.findViewById(R.id.imgLike);
        imgComment = (ImageView) convertView.findViewById(R.id.imgComment);
        txtTopic = (AwesomeTextView) convertView.findViewById(R.id.txtTopic);

        Picasso.with(context)
                .load(post.getAuthor().getAvatarURI())
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .into(imgAvatar);
        tvUsername.setText(post.getAuthor().getUsername());
        tvTimePost.setText(post.getTime());
        tvContent.setText(post.getContent());
        String imageURI = post.getImageURI();
        tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
        tvNumOfCmts.setText(String.valueOf(post.getNumberOfComments()));

        Log.d("TienDH", "topic: " + switchTag(post.getTopic()));
        txtTopic.setText("abc");
        Log.d("TienDH", "txtTopic: " + txtTopic.toString());

        if (imageURI != null && !imageURI.equals("")) {
            Picasso.with(context)
                    .load(Constants.URL_HOST + "/" + post.getImageURI())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imgContent);
            imgContent.setVisibility(View.VISIBLE);
        } else {
            imgContent.setVisibility(View.GONE);
        }

        if (post.isLiked()) {
            imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like));
        } else {
            imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like_inactive));
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLike();
            }
        });
    }

    private void getCommentView(int position, View convertView) {
        TextView tvCommentUsername, tvReplyTo, tvCommentContent, tvCommentTime;
        ImageView imgCommentAvatar;
        tvCommentUsername = (TextView) convertView.findViewById(R.id.tvCommentUsername);
        tvReplyTo = (TextView) convertView.findViewById(R.id.tvReplyTo);
        tvCommentContent = (TextView) convertView.findViewById(R.id.tvCommentContent);
        tvCommentTime = (TextView) convertView.findViewById(R.id.tvCommentTime);
        imgCommentAvatar = (ImageView) convertView.findViewById(R.id.imgCommentAvatar);

        Comment comment = comments.get(position - 1);
        Picasso.with(context)
                .load(comment.getAuthor().getAvatarURI())
                .placeholder(R.mipmap.ic_avatar)
                .error(R.mipmap.ic_avatar)
                .into(imgCommentAvatar);
        tvCommentUsername.setText(comment.getAuthor().getUsername());
        Author replyTo = comment.getReplyTo();
        if (replyTo == null) {
            tvReplyTo.setVisibility(View.GONE);
        } else {
            tvReplyTo.setText("@" + replyTo.getUsername());
            tvReplyTo.setVisibility(View.VISIBLE);
        }
        tvCommentContent.setText(comment.getContent());
        tvCommentTime.setText(comment.getTime());
    }

    private void onClickLike() {
        if (post.isLiked()) {
            unlike();
        } else {
            like();
        }
    }

    private void like() {
        String url = Constants.URL_HOST + "/api/posts/" + post.getId() + "/likes";
        APIConnection.post(context, url, null, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        Log.d("FOO", "liked");
                        post.incLike();
                        imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like));
                        post.setLiked(!post.isLiked());
                        tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
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

    private void unlike() {
        String url = Constants.URL_HOST + "/api/posts/" + post.getId() + "/likes";
        APIConnection.delete(context, url, null, MainActivity.accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals(Constants.SUCCESS)) {
                        Log.d("FOO", "disliked");
                        post.decLike();
                        imgLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_like_inactive));
                        post.setLiked(!post.isLiked());
                        tvNumOfLikes.setText(String.valueOf(post.getNumberOfLikes()));
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
