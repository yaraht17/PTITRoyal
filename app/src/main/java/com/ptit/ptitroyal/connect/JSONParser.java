package com.ptit.ptitroyal.connect;

import com.ptit.ptitroyal.data.Constants;
import com.ptit.ptitroyal.models.Author;
import com.ptit.ptitroyal.models.Noti;
import com.ptit.ptitroyal.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSONParser {
    public static Author parseAuthor(JSONObject object) {
        String id = "", name = "", avatar = "";


        try {
            id = object.getString("id");
            name = object.getString("name");
            avatar = object.getString("avatar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Author user = new Author(id, name, avatar);
        return user;
    }

    public static Post parsePost(JSONObject object) {

        String id = "", content = "", image = "", time = "";
        int likes = 0, comments = 0;
        boolean liked = false;
        Author author = new Author();
        String topic = "";

        try {
            id = object.getString("id");
            author = parseAuthor(object.getJSONObject("author"));
            content = object.getString("content");

            try {
                image = object.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
                image = "";
            }
            time = (object.getString("create_date"));
            likes = (object.getInt("likes"));
            comments = (object.getInt("comments"));
            liked = (object.getBoolean("liked"));
            topic = object.getString("topic");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Post post = new Post(id, author, time, content, image, likes, comments, liked, topic);
        return post;
    }


    public static ArrayList<Noti> parseNotifications(JSONObject response) {
        ArrayList<Noti> list = new ArrayList<>();
        //JSONArray arrayRes = response.getJSONArray("result");
        try {
            JSONArray arrayRes = response.getJSONArray("result");
            for (int i = 0; i < arrayRes.length(); i++) {
                JSONObject object = arrayRes.getJSONObject(i);
                String id = object.getString(Constants.ID);
                JSONObject from = object.getJSONObject(Constants.FROM);
                int type = object.getInt(Constants.TYPE);
                String post = object.getString(Constants.POST);
                String createDate = object.getString(Constants.CREATE_DATE);
                String fromID = from.getString(Constants.ID);
                String fromName = from.getString(Constants.NAME);
                String fromAvatar = from.getString(Constants.AVATAR);
                String read = object.getString(Constants.READ);
                Noti noti = new Noti(id, fromID, fromName, fromAvatar, type, post, createDate, read);
                list.add(noti);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
