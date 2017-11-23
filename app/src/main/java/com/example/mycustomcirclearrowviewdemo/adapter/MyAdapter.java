package com.example.mycustomcirclearrowviewdemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mycustomcirclearrowviewdemo.R;
import com.example.mycustomcirclearrowviewdemo.bean.InfoBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by pc on 2017/11/18.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<InfoBean.DataBean> list;

    private OnItemListener onItemListener;

    public interface OnItemListener {
        public void OnItemClick(InfoBean.DataBean dataBean);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    private OnItemListener2 onItemListener2;

    public interface OnItemListener2 {
        public void OnItemClick2(InfoBean.DataBean dataBean);
    }

    public void setOnItemListener2(OnItemListener2 onItemListener2) {
        this.onItemListener2 = onItemListener2;
    }


    public MyAdapter(Context context, List<InfoBean.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rlv_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        String image_url = list.get(position).getImage_url();
        myViewHolder.sdv.setImageURI(Uri.parse(image_url));

        String vedio_url = list.get(position).getVedio_url();
        myViewHolder.jz.setUp(vedio_url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, list.get(position).getContent());
        myViewHolder.jz.thumbImageView.setImageURI(Uri.parse(list.get(position).getImage_url()));

        //长按item下载
        myViewHolder.ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemListener.OnItemClick(list.get(position));
                return true;
            }
        });

        //单击暂停
        myViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemListener2!=null){
                    onItemListener2.OnItemClick2(list.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView sdv;
        private final LinearLayout ll;
        private final JZVideoPlayerStandard jz;

        public MyViewHolder(View itemView) {
            super(itemView);
            sdv = itemView.findViewById(R.id.rlv_sdv);
            ll = itemView.findViewById(R.id.rlv_ll);
            jz = itemView.findViewById(R.id.rlv_jz);
        }
    }

}
