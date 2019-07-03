package com.sathi4shopping.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sathi4shopping.R;

public class PageAdapt extends PagerAdapter {
    private Context context;

    public PageAdapt(Context nc) {
        this.context = nc;
    }

    public String[] title = {
            "Get lowest price\nof your shopping",
            "Save lot's of time\non your shopping",
            "Know latest\nTrends and Offers",
            "Make your shopping\nBest with Sathi4Shopping"
    };
    private int[] images = {
            R.drawable.ic_slide2,
            R.drawable.ic_slide1,
            R.drawable.ic_trending,
            R.drawable.ic_slide3
    };

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.guide_template, container, false);
        TextView textView = view.findViewById(R.id.textlogo);
        ImageView imageView = view.findViewById(R.id.imagelogo);
        textView.setText(title[position]);
        imageView.setImageResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}