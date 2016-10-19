package com.chen.telbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.telbook.R;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.utils.ImageGlide;

import java.util.List;

/**
 * 分类
 * Created by hui on 2016/10/6.
 */

public class TelNumAdapter extends RecyclerView.Adapter<TelNumAdapter.ViewHolder> {

    private Context mContext;
    private List<TelNum> list;
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public TelNumAdapter(Context mContext, List<TelNum> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), getItem_telnum(), null);
        return new ViewHolder(view);
    }

    protected int getItem_telnum() {
        return R.layout.item_telnum;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.initData(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<TelNum> telList) {
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

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.ivImg);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTel = (TextView) itemView.findViewById(R.id.tvTel);
        }

        public void initData(TelNum telNum, final int position) {
            tvName.setText(telNum.getName());
            tvTel.setText(telNum.getTel());

            String imgUrl = telNum.getImg();
            if (imgUrl.indexOf("?") == -1) {
                imgUrl = imgUrl + "?imageView2/2/w/300/h/300/q/100";
            }
            ImageGlide.show(mContext, imgUrl, ivImg);
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
