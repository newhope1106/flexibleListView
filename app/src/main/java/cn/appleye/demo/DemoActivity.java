package cn.appleye.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.appleye.flexiblelistview.FlexibleListView;

public class DemoActivity extends Activity {

    private FlexibleListView mFlexibleListView;
    private DataAdapter mAdapter;

    private ArrayList<String> mDatas = new ArrayList<>();

    /**一次生成数据个数*/
    private final static int COUNT_PER_BATCH = 10;

    /**下拉产生的数据偏移值*/
    private int mDownOffset = 0;
    /**上拉产生的数据偏移值*/
    private int mPullOffset = 0;
    /**初始位置*/
    private int mStartPos = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mFlexibleListView = (FlexibleListView) findViewById(R.id.flexible_list_view);

        setupListView();
    }

    private void setupListView() {
        initData();
        mAdapter = new DataAdapter();
        mFlexibleListView.setAdapter(mAdapter);

        /*mFlexibleListView.setOnPullListener(new FlexibleListView.OnPullListener(){

            @Override
            public void onPullDown() {
                Log.d("xxxx", "[onPullDown]");
            }

            @Override
            public void onPullUp() {
                Log.d("xxxx", "[onPullUp]");
            }
        });*/
    }

    /**
     * 先初始化一些值
     * */
    private void initData() {
        mDownOffset = mStartPos;
        mPullOffset = mStartPos;
        for(int i=0; i<COUNT_PER_BATCH; i++) {
            mDatas.add("this is item " + mPullOffset);
            mPullOffset++;
        }
    }

    private class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView itemView;

            if(convertView == null) {
                itemView = (TextView) LayoutInflater.from(DemoActivity.this).inflate(R.layout.list_item_view, null);
            } else {
                itemView = (TextView) convertView;
            }

            itemView.setText(mDatas.get(position));

            return itemView;
        }
    }
}
