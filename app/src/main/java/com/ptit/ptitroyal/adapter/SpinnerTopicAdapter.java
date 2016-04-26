package com.ptit.ptitroyal.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.models.Topic;
import com.ptit.ptitroyal.view.AwesomeTextView;

import java.util.ArrayList;


public class SpinnerTopicAdapter extends ArrayAdapter<Topic> {

    private ArrayList<Topic> objects;
    private Activity context;

    public SpinnerTopicAdapter(Activity context, int resourceId, ArrayList<Topic> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_spinner, parent, false);

        AwesomeTextView name = (AwesomeTextView) row.findViewById(R.id.txtTag);
        TextView text = (TextView) row.findViewById(R.id.tv_hashtag);

        Topic d = objects.get(position);
        name.setText(d.getIcon());
        text.setText(d.getName());
        return row;
    }
}
