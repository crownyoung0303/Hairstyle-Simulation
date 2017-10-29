package com.hairstyle.simu.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hairstyle.simu.R;
import com.hairstyle.simu.model.Photo;

import java.util.List;

/**
 * Created by Simon on 2017/6/23.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {
    private Context mContext;
    private List<Photo> mData;

    public PhotoAdapter(Context mContext, List<Photo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Photo photo = mData.get(position);
        if (photo != null && photo.getPath() != null) {
            Glide.with(mContext).load(photo.getPath())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.ivImg);

            holder.ivImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(photo.getPath());
                    }
                }
            });
        } else {
            holder.ivImg.setImageResource(0);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImg;

        MyViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
