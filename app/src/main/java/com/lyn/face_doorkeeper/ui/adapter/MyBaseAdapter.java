package com.lyn.face_doorkeeper.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：龙永宁
 *
 * @param <A>
 * @param <B>
 */
public abstract class MyBaseAdapter<A, B extends ViewBinding> extends BaseAdapter {
    private volatile List<A> data;

    public List<A> getData() {
        return data;
    }

    protected B binding;

    public void setData(List<A> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(List<A> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void remove(int position){
        if (data!=null){
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public A getItem(int position) {
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            binding = getViewBinding(parent);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (B) convertView.getTag();
        }
        if (data != null && data.size() > 0) {
            A a = data.get(position);
            bindData(binding, a, position);
        }
        return binding.getRoot();
    }

    protected abstract B getViewBinding(ViewGroup parent);

    protected abstract void bindData(B b, A a, int position);

    protected abstract void bindListener(B b, A a, int position);
}
