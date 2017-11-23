package com.example.myb.presenter;


import com.example.myb.bean.InfoBean;
import com.example.myb.model.InfoModel;
import com.example.myb.utils.OnNetListener;
import com.example.myb.view.InfoView;

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
