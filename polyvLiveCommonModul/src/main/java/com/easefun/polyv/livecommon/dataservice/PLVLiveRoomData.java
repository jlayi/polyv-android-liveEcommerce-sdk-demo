package com.easefun.polyv.livecommon.dataservice;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.easefun.polyv.cloudclass.model.PolyvLiveClassDetailVO;
import com.easefun.polyv.cloudclass.model.commodity.PolyvCommodityVO;
import com.easefun.polyv.livecommon.config.PLVLiveChannelConfig;

/**
 * 直播间的数据，存放直播api获取的数据以及各个业务间公用的数据，因此需要每个业务模块间持有同个直播间数据对象
 */
public class PLVLiveRoomData implements IPLVLiveRoomData {
    //直播频道配置信息
    private PLVLiveChannelConfig liveChannelConfig;
    //直播详情数据
    private MutableLiveData<PolyvLiveClassDetailVO> classDetailVO = new MutableLiveData<>();
    //商品数据
    private MutableLiveData<PolyvCommodityVO> commodityVO = new MutableLiveData<>();
    //直播场次Id
    private String sessionId;

    public PLVLiveRoomData(@NonNull PLVLiveChannelConfig config) {
        this.liveChannelConfig = config;
    }

    @NonNull
    @Override
    public PLVLiveChannelConfig getConfig() {
        return liveChannelConfig;
    }

    @Override
    public void setConfig(@NonNull PLVLiveChannelConfig config) {
        this.liveChannelConfig = config;
    }

    @Override
    public MutableLiveData<PolyvLiveClassDetailVO> getClassDetailVO() {
        return classDetailVO;
    }

    @Override
    public void postClassDetailVO(PolyvLiveClassDetailVO liveClassDetailVO) {
        classDetailVO.postValue(liveClassDetailVO);
    }

    @Override
    public MutableLiveData<PolyvCommodityVO> getCommodityVO() {
        return commodityVO;
    }

    @Override
    public void postCommodityVO(PolyvCommodityVO commodityVal) {
        commodityVO.postValue(commodityVal);
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}
