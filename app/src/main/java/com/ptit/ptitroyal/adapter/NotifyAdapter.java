package com.ptit.ptitroyal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ptit.ptitroyal.R;
import com.ptit.ptitroyal.models.Noti;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manh on 4/21/16.
 */
public class NotifyAdapter extends ArrayAdapter<Noti> {
    private int resource;
    private Context context;
    private ViewHolder viewHolder;
    private LayoutInflater inflater;
    private List<Noti> notifList = new ArrayList<Noti>();

    public NotifyAdapter(Context context, int resource, ArrayList<Noti> list) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.notifList = list;
    }

    @Override
    public int getCount() {
        return notifList.size();
    }

    @Override
    public Noti getItem(int position) {
        return notifList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        final Noti item = getItem(position);
        View row = convertView;
        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.item_notify, parent, false);
            viewHolder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
            viewHolder.tvTime = (TextView) row.findViewById(R.id.tv_time);
            viewHolder.imageNotify = (ImageView) row.findViewById(R.id.image_notify);
            viewHolder.relativeLayout = (RelativeLayout) row.findViewById(R.id.item_notify);
            row.setTag(viewHolder);
            String titleNoti = "";
            if (item.getType() == 1) {
                titleNoti = item.getFromName() + " đã thích bài viết của bạn.";
            } else {
                titleNoti = item.getFromName() + " đã bình luận bài viết của bạn.";
            }
            viewHolder.tvTitle.setText(titleNoti);
            viewHolder.tvTime.setText(item.getCreateDate());
            //  viewHolder.imageNotify ;
            Picasso.with(context)
                    .load(item.getFromAvatar())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(viewHolder.imageNotify);
            // set color item
            if (item.getRead().equals("true")) {
                Log.d("manh", item.getRead());
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                viewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#EDEFF5"));
            }

        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        return row;
    }


    private class ViewHolder {
        private TextView tvTitle;
        private TextView tvTime;
        private ImageView imageNotify;
        private RelativeLayout relativeLayout;
    }
}
