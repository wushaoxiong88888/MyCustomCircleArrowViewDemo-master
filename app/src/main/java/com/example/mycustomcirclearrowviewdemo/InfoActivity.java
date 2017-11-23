package com.example.mycustomcirclearrowviewdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycustomcirclearrowviewdemo.adapter.MyAdapter;
import com.example.mycustomcirclearrowviewdemo.bean.InfoBean;
import com.example.mycustomcirclearrowviewdemo.presenter.InfoPres;
import com.example.mycustomcirclearrowviewdemo.utils.DownLoadFile;
import com.example.mycustomcirclearrowviewdemo.utils.GlideImageLoader;
import com.example.mycustomcirclearrowviewdemo.view.InfoView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private RecyclerView mInfoRlv;
    private Banner mInfoBan;
    private List<String> imgList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private SeekBar mInfoSb;
    /**
     * 下载
     */
    private TextView mInfoTv;
    private boolean b = true; //判断
    DownLoadFile downLoadFile; //实例化下载的类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();


        //关联
        InfoPres infoPres = new InfoPres(new InfoView() {
            @Override
            public void infoShow(InfoBean infoBean) {
                //String msg = infoBean.getMsg();
                //Toast.makeText(InfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                List<InfoBean.DataBean> list = infoBean.getData();

                for (int i = 0; i < list.size(); i++) {
                    String image_url = list.get(i).getImage_url();
                    String title = list.get(i).getTitle();
                    imgList.add(image_url);
                    titles.add(title);
                }

                //图片轮播
                mInfoBan.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                mInfoBan.setImageLoader(new GlideImageLoader());
                mInfoBan.setImages(imgList);
                mInfoBan.setBannerTitles(titles);
                //Toast.makeText(InfoActivity.this, imgList.size()+"", Toast.LENGTH_SHORT).show();
                mInfoBan.setDelayTime(2000);
                mInfoBan.start();

                //设置适配器
                mInfoRlv.setLayoutManager(new LinearLayoutManager(InfoActivity.this));
                MyAdapter adapter = new MyAdapter(InfoActivity.this, list);
                mInfoRlv.setAdapter(adapter);

                //长按item下载
                adapter.setOnItemListener(new MyAdapter.OnItemListener() {
                    @Override
                    public void OnItemClick(InfoBean.DataBean dataBean) {

                        //true 进行下载
                        if (b) {
                            Toast.makeText(InfoActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                            //下载地址
                            final String vedio_url = dataBean.getVedio_url();
                            //存入本地的名称
                            //private String filePath = Environment.getExternalStorageDirectory() + "/" + "网易云音乐.mp4";
                            String filePath = Environment.getExternalStorageDirectory() + "/" + dataBean.getTitle() + ".mp4";
                            //回调下载类
                            downLoadFile = new DownLoadFile(InfoActivity.this, vedio_url, filePath, 3);
                            downLoadFile.setOnDownLoadListener(new DownLoadFile.DownLoadListener() {
                                @Override
                                public void getProgress(int progress) {
                                    mInfoSb.setProgress(progress);
                                    mInfoTv.setText("当前进度 ：" + progress + " %");
                                }

                                @Override
                                public void onComplete() {
                                    Toast.makeText(InfoActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                                    b = true;
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(InfoActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //下载方法
                            downLoadFile.downLoad();
                        } else {
                            Toast.makeText(InfoActivity.this, "继续下载", Toast.LENGTH_SHORT).show();
                            downLoadFile.onStart();
                        }

                    }
                });
                //单击暂停
                adapter.setOnItemListener2(new MyAdapter.OnItemListener2() {
                    @Override
                    public void OnItemClick2(InfoBean.DataBean dataBean) {
                        Toast.makeText(InfoActivity.this, "暂停下载", Toast.LENGTH_SHORT).show();
                        downLoadFile.onPause();
                        b = false;
                        //获取当前下载的大小
                        int currLength = downLoadFile.currLength;
                        Log.e("TAG------",currLength+"");
                    }
                });
            }
        });
        infoPres.infoMandV();

    }

    private void initView() {
        mInfoRlv = (RecyclerView) findViewById(R.id.info_rlv);
        mInfoBan = (Banner) findViewById(R.id.info_ban);
        mInfoSb = (SeekBar) findViewById(R.id.info_sb);
        mInfoTv = (TextView) findViewById(R.id.info_tv);
    }

}
