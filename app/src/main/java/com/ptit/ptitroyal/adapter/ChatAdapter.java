package com.ptit.ptitroyal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public ChatAdapter(Context context, int textViewResourceId,
                       ArrayList<ChatMessage> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        chatMessageList = objects;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(ArrayList<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        inflater = (LayoutInflater) this.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left == true) {
            row = inflater.inflate(R.layout.item_chat_left, parent, false);
        } else {
            row = inflater.inflate(R.layout.item_chat_right, parent, false);
        }
        viewHolder.message = (TextView) row.findViewById(R.id.message);
        row.setTag(viewHolder);
        viewHolder.message.setText(chatMessageObj.message);
        return row;
    }

    private class ViewHolder {
        TextView message;
        ImageView avatar;
    }


}