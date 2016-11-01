package cn.appleye.flexiblelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

/**
 * 弹性ListView。
 * 在http://blog.csdn.net/eastman520/article/details/19043973的基础上添加了上拉和下拉功能
 */
public class FlexibleListView extends ListView implements OnTouchListener{
    /**初始可拉动Y轴方向距离*/
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;

    private Context mContext;

    /**实际可上下拉动Y轴上的距离*/
    private int mMaxYOverscrollDistance;

    private float mStartY = -1;
    /**开始计算的时候，第一个或者最后一个item是否可见的*/
    private boolean mCalcOnItemVisible = false;
    /**是否开始计算*/
    private boolean mStartCalc = false;

    /**用户自定义的OnTouchListener类*/
    private OnTouchListener mTouchListener;

    /**上拉和下拉监听事件*/
    private OnPullListener mPullListener;

    public FlexibleListView(Context context){
        super(context);
        mContext = context;
        super.setOnTouchListener(this);
        initBounceListView();
    }

    public FlexibleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        super.setOnTouchListener(this);
        initBounceListView();
    }

    public FlexibleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();
    }

    private void initBounceListView(){
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //实现的本质就是在这里动态改变了maxOverScrollY的值
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    /**
     * 覆盖父类的方法，设置OnTouchListener监听对象
     * @param listener 用户自定义的OnTouchListener监听对象
     * */
    public void setOnTouchListener(OnTouchListener listener) {
        mTouchListener = listener;
    }

    /**
     * 设置上拉和下拉监听对象
     * @param listener 上拉和下拉监听对象
     * */
    public void setOnPullListener(OnPullListener listener){
        mPullListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*用户自定义的触摸监听对象消费了事件，则不执行下面的上拉和下拉功能*/
        if(mTouchListener!=null && mTouchListener.onTouch(v, event)) {
            return true;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if(getFirstVisiblePosition() == 0 || (getLastVisiblePosition() == getAdapter().getCount()-1)) {
                    mStartY = event.getY();
                    mStartCalc = true;
                    mCalcOnItemVisible = true;
                }else{
                    mStartCalc = false;
                    mCalcOnItemVisible = false;
                }
            }
            case MotionEvent.ACTION_MOVE:{
                if(!mStartCalc && (getFirstVisiblePosition() == 0|| (getLastVisiblePosition() == getAdapter().getCount()-1))) {
                    mStartCalc = true;
                    mCalcOnItemVisible = false;
                    mStartY = event.getY();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{
                float distance = event.getY() - mStartY;
                checkIfNeedRefresh(distance);
            }
        }

        return false;
    }

    /**
     * 根据滑动的距离判断是否需要回调上拉或者下拉事件
     * @param distance 滑动的距离
     * */
    private void checkIfNeedRefresh(float distance) {
        if(distance > 0 && getFirstVisiblePosition() == 0) { //下拉
            View view = getChildAt(0);
            if(view == null) {
                return;
            }

            float realDistance = distance;
            if(!mCalcOnItemVisible) {
                realDistance = realDistance - view.getHeight();//第一个item的高度不计算在内容
            }
            if(realDistance > mMaxYOverscrollDistance) {
                if(mPullListener != null){
                    mPullListener.onPullDown();
                }
            }
        } else if(distance < 0 && getLastVisiblePosition() == getAdapter().getCount()-1) {//上拉
            View view = getChildAt(getChildCount()-1);
            if(view == null) {
                return;
            }

            float realDistance = -distance;
            if(!mCalcOnItemVisible) {
                realDistance = realDistance - view.getHeight();//最后一个item的高度不计算在内容
            }
            if(realDistance > mMaxYOverscrollDistance) {
                if(mPullListener != null){
                    mPullListener.onPullUp();
                }
            }
        }
    }

    public interface OnPullListener{
        /**
         * 下拉
         * */
        void onPullDown();
        /**
         * 上拉
         * */
        void onPullUp();
    }
}
