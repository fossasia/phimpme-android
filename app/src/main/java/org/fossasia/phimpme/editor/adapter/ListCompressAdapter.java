package org.fossasia.phimpme.editor.adapter;

/**
 * Created by JASPREET SINGH on 01-01-2018.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.CompressImageActivity;

import java.util.ArrayList;


public class ListCompressAdapter extends BaseAdapter  {
   private Activity context;
   private ArrayList<String> title;

    public ListCompressAdapter(CompressImageActivity context, ArrayList<String> title)
    {
        super();
        this.context=context;
        this.title=title;

    }


    @Override
    public int getCount() {

        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
       private TextView txtviewtitle;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View convertView1 = convertView;
        LayoutInflater inflater=context.getLayoutInflater();
        if(convertView1==null)
        {
            convertView1=inflater.inflate(R.layout.compress_item,null);
            holder=new ViewHolder();
            holder.txtviewtitle=(TextView)convertView1.findViewById(R.id.compressoption);

        }
        else{
            holder=(ViewHolder)convertView1.getTag();
        }
        holder.txtviewtitle.setText(title.get(position));


        notifyDataSetChanged();
        return convertView1;
    }


}
