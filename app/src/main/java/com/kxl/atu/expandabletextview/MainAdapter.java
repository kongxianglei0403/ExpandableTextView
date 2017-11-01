package com.kxl.atu.expandabletextview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author:ATU
 * Date:2017/10/31  16:58
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>
{
    private List<DataBean> lists;
    private LayoutInflater inflater;
    public MainAdapter(Context context, ArrayList<DataBean> models)
    {
        this.lists = models;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_item,parent,false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position)
    {
        holder.expandable_text.setText(lists.get(position).getText(),lists.get(position).isCollpased());
        holder.expandable_text.setListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(boolean isExpanded) {
                lists.get(position).setCollpased(isExpanded);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return lists.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder
    {
        ExpandableTextView expandable_text;
        public MainViewHolder(View itemView) {
            super(itemView);
            expandable_text= (ExpandableTextView) itemView.findViewById(R.id.expandable_text);
        }
    }
}
