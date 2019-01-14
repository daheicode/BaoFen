package com.example.administrator.baofen.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.baofen.bean.MobileNoVO;

import java.util.ArrayList;
import java.util.List;

public class MobileNoRecyclerAdapter extends RecyclerView.Adapter<MobileNoRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MobileNoVO> list;

    public MobileNoRecyclerAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setData(List<MobileNoVO> setList) {
        this.list.clear();
        addData(setList);
    }

    public void addData(List<MobileNoVO> addList){
        int size = this.list.size();
        this.list.addAll(addList);
        if (size == 0){
            notifyDataSetChanged();
        }else {
            notifyItemRangeInserted(size, addList.size());
        }
    }

    public String getItemData(int position){
        return position+" "+list.get(position).getResourceName()+" "+list.get(position).getMobileNo();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ViewHolder(new TextView(context));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textView.setTextSize(19);
        viewHolder.textView.setText(getItemData(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
