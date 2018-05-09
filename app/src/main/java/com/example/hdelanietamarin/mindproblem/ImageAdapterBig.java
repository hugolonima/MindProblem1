package com.example.hdelanietamarin.mindproblem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by h.de.la.nieta.marin on 18/04/2018.
 */

public class ImageAdapterBig extends BaseAdapter {
    private Context mContext;
    public ImageView imageView;

    public ImageAdapterBig(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return mThumbIds[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(6, 6, 6, 6);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public int addImage(){
        int code =0;
        Random r = new Random();
        int i1 = r.nextInt(4 - 0);//

        switch (i1){
            case 0:
                code = R.drawable.baby;
                break;
            case 1:
                code = R.drawable.football;
                break;
            case 2:
                code = R.drawable.house;
                break;
            case 3:
                code = R.drawable.plane;
                break;
        }
        return code;
    }

    private Integer[] mThumbIds = {

            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(), addImage(),
            addImage(),addImage(),
            addImage(), addImage(),

    };
}
