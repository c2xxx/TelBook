package com.chen.telbook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.libchen.Logger;
import com.chen.telbook.R;
import com.chen.telbook.bean.CallLog;
import com.chen.telbook.utils.ImageGlide;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 分类
 * Created by hui on 2016/10/6.
 */

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private Context mContext;
    private List<CallLog> list;
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public CallLogAdapter(Context mContext, List<CallLog> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), getItem_telnum(), null);
        return new ViewHolder(view);
    }

    protected int getItem_telnum() {
        return R.layout.item_call_log;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initData(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        Logger.d("itemCount=" + list.size());
        return list.size();
    }

    public void setData(List<CallLog> telList) {
        if (telList != null) {
            this.list.clear();
            this.list.addAll(telList);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImg;
        private TextView tvName;
        private TextView tvTel;
        private TextView tvTime;
        private TextView tvType;
        private TextView tvDuring;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.ivImg);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTel = (TextView) itemView.findViewById(R.id.tvTel);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvDuring = (TextView) itemView.findViewById(R.id.tvDuring);
        }

        public void initData(CallLog telNum, final int position) {
            tvName.setText(telNum.getName());
            tvTel.setText(telNum.getTel());

            String imgUrl = telNum.getImg();
            if (!TextUtils.isEmpty(imgUrl)) {
                if (imgUrl.indexOf("clouddn.com") != -1 && imgUrl.indexOf("?") == -1) {
                    imgUrl = imgUrl + "?imageView2/2/w/300/h/300/q/100";
                }
            }
            ImageGlide.show(mContext, imgUrl, ivImg);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            tvTime.setText(sdf.format(telNum.getDate()));
            tvType.setText(("" + telNum.getType()).replace("1", "来电").replace("2", "去电").replace("3", "未接来电").replace("9", "拒接"));
            tvType.setTextColor(Color.WHITE);
            if (telNum.getType() == 3) {
                tvDuring.setText("响铃次数：" + telNum.getRingTimes());
                tvType.setTextColor(Color.RED);
            } else if (telNum.getType() == 9) {
                tvDuring.setText("");
            } else {
                tvDuring.setText(telNum.getDuringString() + "");
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClick != null) {
                        onItemClick.onItemClick(position);
                    }
                    return false;
                }
            });
        }
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }
}
