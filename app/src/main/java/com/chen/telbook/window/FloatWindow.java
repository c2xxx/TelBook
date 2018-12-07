package com.chen.telbook.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.telbook.MyApplication;
import com.chen.telbook.R;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.utils.ImageGlide;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by zhongxiang.huang on 2017/6/23.
 */


public class FloatWindow implements View.OnTouchListener {


    private static FloatWindow floatWindow;
    private Context mContext;

    private WindowManager.LayoutParams mWindowParams;

    private WindowManager mWindowManager;


    private View mFloatLayout;

    private float mInViewX;

    private float mInViewY;

    private float mDownInScreenX;

    private float mDownInScreenY;

    private float mInScreenX;

    private float mInScreenY;


    public FloatWindow(Context context) {
        this.mContext = context;
        initFloatWindow();
    }


    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        mFloatLayout = (View) inflater.inflate(R.layout.view_toast_message, null);
        mFloatLayout.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return floatLayoutTouch(motionEvent);
    }


    private boolean floatLayoutTouch(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                mInViewX = motionEvent.getX();
                mInViewY = motionEvent.getY();
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                break;
            case MotionEvent.ACTION_MOVE:
                // 更新浮动窗口位置参数
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mWindowParams.x = (int) (mInScreenX - mInViewX);
                mWindowParams.y = (int) (mInScreenY - mInViewY);
                // 手指移动的时候更新小悬浮窗的位置
                mWindowManager.updateViewLayout(mFloatLayout, mWindowParams);
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mDownInScreenX == mInScreenX && mDownInScreenY == mInScreenY) {

                }
                break;
        }
        return true;
    }

    public static void showNumber(String number) {
        dismiss();
        floatWindow = new FloatWindow(MyApplication.getContext());
        floatWindow.metchPhone(number);
        floatWindow.setFloatLayoutAlpha(0.5f);
        floatWindow.showFloatWindow();
    }

    public static void dismiss() {
        if (floatWindow != null) {
            try {
                floatWindow.hideFloatWindow();
                floatWindow = null;
            } catch (Exception e) {
            }
        }
    }

    public void metchPhone(String number) {
        if (TextUtils.isEmpty(number)) {
            hideFloatWindow();
            return;
        }
        number = number.replace(" ", "").replace("-", "");
        TelNum telNum = getInfoByNumber(number);
        if (telNum == null) {
            hideFloatWindow();
            return;
        }
        disPlayContent(telNum.getImg(), telNum.getName(), number);
    }

    private void disPlayContent(String img, String name, String number) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.init(mFloatLayout);
        viewHolder.show(img, name, number);
    }

    public TelNum getInfoByNumber(String number) {
        Map<String, TelNum> map = TelBookXmlHelper.loadLocolData();
        return map.get(number);
    }


    public void showFloatWindow() {
        if (mFloatLayout.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            //默认固定位置，靠屏幕右边缘的中间
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = metrics.widthPixels;
            mWindowParams.y = metrics.heightPixels / 2 - getSysBarHeight(mContext);
            mWindowManager.addView(mFloatLayout, mWindowParams);
        }
    }


    public void hideFloatWindow() {
        if (mFloatLayout.getParent() != null)
            mWindowManager.removeView(mFloatLayout);
    }


    /**
     * 透明度
     *
     * @param value 0到1f
     */
    public void setFloatLayoutAlpha(float value) {
        mFloatLayout.setAlpha(value);
    }

    // 获取系统状态栏高度
    public static int getSysBarHeight(Context contex) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    private static class ViewHolder {
        View root;
        TextView tvName;
        TextView tvNumber;
        ImageView ivImg;

        private void init(View view) {
            root = view;
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvNumber = (TextView) view.findViewById(R.id.tvNumber);
            ivImg = (ImageView) view.findViewById(R.id.ivImg);
//            initMoveListener();
        }

        private void show(String img, String name, String number) {
            ImageGlide.show(MyApplication.getContext(), img, ivImg);
            tvName.setText(name);
            tvNumber.setText(number);
        }

        private void setVisible(boolean isShow) {
            root.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

        /**
         * 对windowManager进行设置
         *
         * @return
         */
        public WindowManager.LayoutParams getParams() {
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
            //wmParams.type = LayoutParams.TYPE_PHONE;
            //wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置图片格式，效果为背景透明
            wmParams.format = PixelFormat.RGBA_8888;
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            //wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //设置可以显示在状态栏上
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

            //设置悬浮窗口长宽数据
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.x = 1000;

            return wmParams;
        }
    }
}