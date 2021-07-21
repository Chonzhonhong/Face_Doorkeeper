package com.lyn.face_doorkeeper.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public  class MyPagerAdapter extends PagerAdapter {
    private List<View> list;

    public MyPagerAdapter(List<View> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list!=null&&list.size()>0){
            return list.size();
        }
        return 0;
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        return super.getItemPosition(object);
    }

    public View getItemView(int position){
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {

        return view==list.get(Integer.parseInt(object.toString()));
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View view= (View) list.get(position);
        container.addView(view);
        return position;
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView(list.get(position));
    }
}
