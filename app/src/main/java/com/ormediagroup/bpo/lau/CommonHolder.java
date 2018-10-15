package com.ormediagroup.bpo.lau;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ormediagroup.bpo.bean.CompanyBean;
import com.squareup.picasso.Picasso;

/**
 * CommonHolder for RecyclerView
 * Created by Lau on 2018/8/13.
 */

public class CommonHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public CommonHolder(View itemView, boolean isHeaderOrFooter) {
        super(itemView);
        if (isHeaderOrFooter) {
            return;
        }
        mViews = new SparseArray<>();
    }

    /**
     * 从稀疏数组中取出对应的view，没有则findViewById添加到稀疏数组中
     * @param resId     资源id
     * @param <T>       任意类型view
     * @return
     */
    public <T extends View> T getView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = itemView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (T) view;
    }

    public void setBackGroundColor(int color) {
        itemView.setBackgroundColor(color);
    }

    public CommonHolder setVisibility(int resId,boolean isVisible){
        if (isVisible){
            getView(resId).setVisibility(View.VISIBLE);
        }else{
            getView(resId).setVisibility(View.GONE);
        }
        return this;
    }

    public CommonHolder setText(int resId, String text) {
        TextView textView = getView(resId);
        textView.setText(text);
        return this;
    }

    public CommonHolder setText(int resId,String text,String color){
        TextView textView = getView(resId);
        textView.setText(text);
        textView.setTextColor(Color.parseColor(color));
        return this;
    }

    public CommonHolder setText(int resId,String text,String color,boolean underline){
        TextView textView = getView(resId);
        textView.setText(text);
        textView.setTextColor(Color.parseColor(color));
        if (underline){
            textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
        return this;
    }

    public CommonHolder setImageResource(int resId, int imageId) {
        ImageView imageView = getView(resId);
        imageView.setImageResource(imageId);
        return this;
    }

    public CommonHolder setImageURL(int resId,String url){
        ImageView imageView = getView(resId);
        Picasso.get().load(url).resize(200,200).into(imageView);
        return this;
    }


    // ... and so on.
}
