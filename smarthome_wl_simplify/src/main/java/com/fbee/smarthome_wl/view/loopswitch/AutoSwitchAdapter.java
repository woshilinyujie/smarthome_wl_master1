package com.fbee.smarthome_wl.view.loopswitch;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.fbee.smarthome_wl.utils.ImageLoader;

import java.util.List;

/**
 * @author ryze
 * @since 1.0  2016/07/17
 */
public class AutoSwitchAdapter extends AutoLoopSwitchBaseAdapter {

  private Context mContext;

  private List<LoopModel> mDatas;
  private OnIitemClickListener listener;

  public AutoSwitchAdapter() {
    super();
  }

  public AutoSwitchAdapter(Context mContext, List<LoopModel> mDatas) {
    this.mContext = mContext;
    this.mDatas = mDatas;
  }

  public void setListener(OnIitemClickListener listener) {
    this.listener = listener;
  }

  @Override
  public int getDataCount() {
    return mDatas == null ? 0 : mDatas.size();
  }

  @Override
  public View getView(final int position) {
    ImageView imageView = new ImageView(mContext);
    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
    LoopModel model = (LoopModel) getItem(position);
    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

    if(model.getTitle()==null){
      try{
        imageView.setImageResource(model.getResId());
      }catch(Exception e){}

    }else{
      ImageLoader.load(mContext, Uri.parse(model.getTitle()),imageView);
    }

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onIitemClickListener(position);
      }
    });

    return imageView;
  }

  @Override
  public Object getItem(int position) {
    if (position >= 0 && position < getDataCount()) {

      return mDatas.get(position);
    }
    return null;
  }


  @Override
  public View getEmptyView() {
    return null;
  }

  @Override
  public void updateView(View view, int position) {

  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    super.destroyItem(container, position, object);
  }

  public interface OnIitemClickListener {
    void onIitemClickListener(int positon);
  }

}
