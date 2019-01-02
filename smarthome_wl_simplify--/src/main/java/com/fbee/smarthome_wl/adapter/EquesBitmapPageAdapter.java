package com.fbee.smarthome_wl.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class EquesBitmapPageAdapter extends PagerAdapter {
	List<ImageView> imageViews;
	public EquesBitmapPageAdapter(){
		imageViews=new ArrayList();
	}
	public void addAll(List<ImageView> imageVs){	
		imageViews.addAll(imageVs);
		notifyDataSetChanged();
	}
	public void clear(){
		imageViews.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageViews.size();
	}
	
	 @Override  
     public void destroyItem(ViewGroup container, int position, Object object)   {     
         container.removeView(imageViews.get(position));//删除页卡  
     }  
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		ImageView image=imageViews.get(position);
		container.addView(image);
		return image;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

}
