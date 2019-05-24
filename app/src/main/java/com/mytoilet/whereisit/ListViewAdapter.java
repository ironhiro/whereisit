package com.mytoilet.whereisit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mytoilet.R;

import java.util.ArrayList;
import java.util.Collections;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> items = new ArrayList<>();

    public ListViewAdapter()
    {

    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context= parent.getContext();
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.customlistlayout, parent, false);
        }

        TextView title = (TextView)convertView.findViewById(R.id.list_title);
        TextView addr = (TextView)convertView.findViewById(R.id.list_addr);
        TextView dist = (TextView)convertView.findViewById(R.id.list_dist);

        ListViewItem item = items.get(position);
        title.setText(item.getTitle());
        addr.setText(item.getAddr());
        dist.setText(((int)item.getDist()*1000<1000) ? (String.valueOf((int)(item.getDist()*1000))+"m"):(String.valueOf(item.getDist())+"km"));
        return convertView;
    }

    public void addItem(String title, String addr, double dist)
    {
        ListViewItem item = new ListViewItem();
        item.setTitle(title);
        item.setAddr(addr);
        item.setDist(dist);
        items.add(item);
    }

    public void clear()
    {
        items.clear();
    }

    public void sort()
    {
        Collections.sort(items);
    }
}
