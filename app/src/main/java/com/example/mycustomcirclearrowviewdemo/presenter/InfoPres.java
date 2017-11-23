package com.example.mycustomcirclearrowviewdemo.presenter;

import com.example.mycustomcirclearrowviewdemo.bean.InfoBean;
import com.example.mycustomcirclearrowviewdemo.model.InfoModel;
import com.example.mycustomcirclearrowviewdemo.utils.OnNetListener;
import com.example.mycustomcirclearrowviewdemo.view.InfoView;

/**
 * Created by pc on 2017/11/18.
 */

public class InfoPres {
    InfoView infoView;
    private final InfoModel infoModel;

    public InfoPres(InfoView infoView) {
        this.infoView = infoView;
        infoModel = new InfoModel();
    }
    public void infoMandV(){
        infoModel.infoGetData(new OnNetListener() {
            @Override
            public void onSuccess(InfoBean infoBean) {
                infoView.infoShow(infoBean);
            }
        });
    }

}
