package com.fbee.smarthome_wl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fbee.smarthome_wl.R;

/**
 * Created by ZhaoLi.Wang on 2017/3/22 16:05
 */
public class AnimationLinearLayout extends LinearLayout {
	private boolean mIsExpend;
	private int mOriengHeight;
	private View mClickedView;
	private View mHideView;
	private ImageView mChangeView;
	private int downResource = R.mipmap.down_arrow;
	private int upResource = R.mipmap.up_arrow;

	public AnimationLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AnimationLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimationLinearLayout(Context context) {
		super(context);
	}

	public void setIsExpend(boolean isExpend) {
		this.mIsExpend = isExpend;
		Animation animation;
		if(null ==mHideView)
			return;
		mHideView.setVisibility(View.VISIBLE);
		if (mIsExpend) {
			animation = new LTranslateAnimation(mHideView, 0, mOriengHeight);
			mChangeView.setImageResource(upResource);
		} else {
			animation = new LTranslateAnimation(mHideView, mOriengHeight, 0);
			mChangeView.setImageResource(downResource);
		}
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mClickedView.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mClickedView.setClickable(true);
			}
		});

		clearAnimation();
		startAnimation(animation);

	}



	public void  setClickedViewHideView(final View clickedView, final View hideView, final ImageView changeView, boolean defaultIsExpend) {
		mChangeView = changeView;
		mClickedView = clickedView;
		mHideView = hideView;
		mIsExpend = defaultIsExpend;
		
		int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		mHideView.measure(width,height);
		mOriengHeight = mHideView.getMeasuredHeight();

		clickedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setIsExpend(!mIsExpend);
			}
		});
		if (!mIsExpend) {
			Animation animation = new LTranslateAnimation(mHideView, mOriengHeight, 0);
			animation.setFillAfter(true);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					mClickedView.setClickable(false);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mClickedView.setClickable(true);
				}
			});

			clearAnimation();
			startAnimation(animation);
		}
	}

	/**
	 * Created by ZhaoLi.Wang on 2017/4/21 9:44
	 */
	public void setHideView(final View hideView){
		clearAnimation();
		int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		hideView.measure(width,height);
		mOriengHeight = hideView.getMeasuredHeight();
		hideView.getLayoutParams().height = mOriengHeight;
		hideView.requestLayout();

	}




	public class LTranslateAnimation extends Animation {
		private final View mTargetView;
		private final int mStartHeight;
		private final int mEndHeight;

		public LTranslateAnimation(View view, int startHeight, int endHeight) {
			mTargetView = view;
			mStartHeight = startHeight;
			mEndHeight = endHeight;
			setDuration(200);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			final int newHeight = (int) ((mEndHeight - mStartHeight)
					* interpolatedTime + mStartHeight);
			mTargetView.getLayoutParams().height = newHeight;
			mTargetView.requestLayout();

		}
	}

	public void setDownResource(int downResource) {
		this.downResource = downResource;
	}

	public void setUpResource(int upResource) {
		this.upResource = upResource;
	}
}
