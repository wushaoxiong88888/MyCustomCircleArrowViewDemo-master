package com.example.myb;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myb.adapter.MyAdapter;
import com.example.myb.bean.InfoBean;
import com.example.myb.presenter.InfoPres;
import com.example.myb.utils.GlideImageLoader;
import com.example.myb.view.InfoView;
import com.loopj.android.http.HttpGet;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";

    //下载线程的数量
    private final static int threadsize = 3;

    protected static final int SET_MAX = 0;
    public static final int UPDATE_VIEW = 1;

    //显示进度和更新进度
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_MAX://设置进度条的最大值
                    int filelength = msg.arg1;
                    mInfoSb.setMax(filelength);
                    break;
                case UPDATE_VIEW://更新进度条  和 下载的比率
                    int len = msg.arg1;//新下载的长度
                    mInfoSb.setProgress(mInfoSb.getProgress() + len);//设置进度条的刻度

                    int max = mInfoSb.getMax();//获取进度的最大值
                    int progress = mInfoSb.getProgress();//获取已经下载的数据量
                    //  下载：30    总：100
                    int result = (progress * 100) / max;

                    mInfoTv.setText("下载:" + result + "%");

                    break;

                default:
                    break;
            }
        }

        ;
    };


    private RecyclerView mInfoRlv;
    private Banner mInfoBan;
    private List<String> imgList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private SeekBar mInfoSb;
    /**
     * 下载
     */
    private TextView mInfoTv;
    private int gouzi = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                mInfoRlv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                MyAdapter adapter = new MyAdapter(MainActivity.this, list);
                mInfoRlv.setAdapter(adapter);

                //长按item下载
                adapter.setOnItemListener(new MyAdapter.OnItemListener() {
                    @Override
                    public void OnItemClick(InfoBean.DataBean dataBean) {
                        //Toast.makeText(InfoActivity.this, "234567", Toast.LENGTH_SHORT).show();
                        //mInfoSb.setVisibility(View.VISIBLE);
                        gouzi++;

                        final String vedio_url = dataBean.getVedio_url();

                        new Thread() {//子线程
                            public void run() {
                                try {
                                    //获取服务器上文件的大小
                                    HttpClient client = new DefaultHttpClient();
                                    HttpHead request = new HttpHead(vedio_url);
                                    HttpResponse response = client.execute(request);
                                    //response  只有响应头  没有响应体
                                    if (response.getStatusLine().getStatusCode() == 200) {
                                        Header[] headers = response.getHeaders("Content-Length");
                                        String value = headers[0].getValue();
                                        //文件大小
                                        int filelength = Integer.parseInt(value);
                                        Log.i(TAG, "filelength:" + filelength);

                                        //设置进度条的最大值
                                        Message msg_setmax = Message.obtain(mHandler, SET_MAX, filelength, 0);
                                        msg_setmax.sendToTarget();


                                        //处理下载记录文件
                                        for (int threadid = 0; threadid < threadsize; threadid++) {
                                            //对应的下载记录文件
                                            File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
                                            //判断文件是否存在
                                            if (!file.exists()) {
                                                //创建文件
                                                file.createNewFile();
                                            }
                                        }


                                        //在sdcard创建和服务器大小一样的文件
                                        String name = getFileName(vedio_url);
                                        File file = new File("/mnt/shared", name);
                                        //随机访问文件
                                        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                                        //设置文件的大小
                                        raf.setLength(filelength);
                                        //关闭
                                        raf.close();

                                        //计算每条线程的下载量
                                        int block = (filelength % threadsize == 0) ? (filelength / threadsize) : (filelength / threadsize + 1);

                                        //开启三条线程执行下载
                                        for (int threadid = 0; threadid < threadsize; threadid++) {
                                            new DownloadThread(threadid, vedio_url, file, block).start();
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

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


    //线程下载类
    private class DownloadThread extends Thread {
        private int threadid;//线程的id
        private String uri;//下载的地址
        private File file;//下载文件
        private int block;//下载的块
        private int start;
        private int end;

        public DownloadThread(int threadid, String uri, File file, int block) {
            super();
            this.threadid = threadid;
            this.uri = uri;
            this.file = file;
            this.block = block;
            //计算下载的开始位置和结束位置
            start = threadid * block;
            end = (threadid + 1) * block - 1;

            try {
                //读取该条线程原来的下载记录
                int existDownloadLength = readDownloadInfo(threadid);

                //修改下载的开始位置
                start = start + existDownloadLength;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //下载   状态码：200是普通的下载      206是分段下载        Range:范围
        @Override
        public void run() {
            super.run();
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //跳转到起始位置
                raf.seek(start);

                //分段下载
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(uri);
                request.addHeader("Range", "bytes:" + start + "-" + end);//添加请求头
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    //把流写入到文件
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        //如果暂停下载   就直接return
                        /*if(!flag){
                            return;//标准线程结束
                        }*/
                        //写数据
                        raf.write(buffer, 0, len);

                        //读取原来下载的数据量
                        int existDownloadLength = readDownloadInfo(threadid);//原来下载的数据量

                        //计算最新的下载
                        int newDownloadLength = existDownloadLength + len;

                        //更新下载记录
                        updateDownloadInfo(threadid, newDownloadLength);

                        //更新进度条的显示   下载的百分比
                        Message update_msg = Message.obtain(mHandler, UPDATE_VIEW, len, 0);
                        update_msg.sendToTarget();
                        //模拟  看到进度条动的效果
                        SystemClock.sleep(50);
                    }
                    inputStream.close();
                    raf.close();
                    Log.i(TAG, "第" + threadid + "条线程下载完成");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 读取指定线程的下载数据量
     *
     * @param threadid 线程的id
     * @return
     * @throws Exception
     */
    public int readDownloadInfo(int threadid) throws Exception {
        //下载记录文件
        File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        //读取一行数据
        String content = br.readLine();

        int downlength = 0;
        //如果该文件第一次创建去执行读取操作  文件里面的内容是 null
        if (!TextUtils.isEmpty(content)) {
            downlength = Integer.parseInt(content);
        }
        //关闭流
        br.close();
        return downlength;
    }


    /**
     * 更新下载记录
     *
     * @param threadid
     * @param newDownloadLength
     */
    public void updateDownloadInfo(int threadid, int newDownloadLength) throws Exception {
        //下载记录文件
        File file = new File(Environment.getExternalStorageDirectory(), threadid + ".txt");
        FileWriter fw = new FileWriter(file);
        fw.write(newDownloadLength + "");
        fw.close();
    }

    /**
     * 获取文件的名称
     *
     * @param uri
     * @return
     */
    private String getFileName(String uri) {
        return uri.substring(uri.lastIndexOf("/") + 1);
    }


}
