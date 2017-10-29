package com.hairstyle.simu.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairstyle.simu.R;
import com.hairstyle.simu.model.Filter;

import java.util.List;


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder>{
    private Context mContext;
    private List<Filter> mData;

    public FilterAdapter(Context mContext, List<Filter> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_filter, parent, false));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Filter bean = mData.get(position);
        if (bean != null) {
            holder.tvName.setText(bean.getName());
            holder.ivImg.setImageResource(bean.getImgRes());

            holder.ivImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(bean.getId());
                    }
                }
            });
        } else {
            holder.ivImg.setImageResource(0);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivImg;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
