package com.easefun.polyv.liveecommerce.scenes.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easefun.polyv.businesssdk.model.video.PolyvMediaPlayMode;
import com.easefun.polyv.cloudclass.chat.PolyvChatManager;
import com.easefun.polyv.cloudclass.chat.PolyvLocalMessage;
import com.easefun.polyv.cloudclass.chat.event.PolyvChatImgEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvCloseRoomEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvKickEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvLikesEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvLoginEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvLoginRefuseEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvLogoutEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvReloginEvent;
import com.easefun.polyv.cloudclass.chat.event.PolyvSpeakEvent;
import com.easefun.polyv.cloudclass.chat.send.custom.PolyvCustomEvent;
import com.easefun.polyv.cloudclass.model.PolyvLiveClassDetailVO;
import com.easefun.polyv.cloudclass.model.bulletin.PolyvBulletinVO;
import com.easefun.polyv.cloudclass.model.commodity.PolyvCommodityVO;
import com.easefun.polyv.livecommon.modules.chatroom.PLVCustomGiftBean;
import com.easefun.polyv.livecommon.modules.chatroom.contract.IPLVChatroomContract;
import com.easefun.polyv.livecommon.modules.chatroom.holder.PLVChatMessageItemType;
import com.easefun.polyv.livecommon.modules.chatroom.view.PLVAbsChatroomView;
import com.easefun.polyv.livecommon.modules.player.live.model.PLVLivePlayerData;
import com.easefun.polyv.livecommon.ui.widget.PLVMessageRecyclerView;
import com.easefun.polyv.livecommon.ui.widget.itemview.PLVBaseViewData;
import com.easefun.polyv.livecommon.ui.window.PLVBaseFragment;
import com.easefun.polyv.livecommon.ui.window.PLVInputWindow;
import com.easefun.polyv.livecommon.utils.PLVToastUtils;
import com.easefun.polyv.livecommon.utils.span.PLVTextFaceLoader;
import com.easefun.polyv.liveecommerce.R;
import com.easefun.polyv.liveecommerce.modules.chatroom.PLVECChatMessageAdapter;
import com.easefun.polyv.liveecommerce.modules.chatroom.widget.PLVECBulletinView;
import com.easefun.polyv.liveecommerce.modules.chatroom.widget.PLVECChatImgScanPopupView;
import com.easefun.polyv.liveecommerce.modules.chatroom.widget.PLVECChatInputWindow;
import com.easefun.polyv.liveecommerce.modules.chatroom.widget.PLVECGreetingView;
import com.easefun.polyv.liveecommerce.modules.chatroom.widget.PLVECLikeIconView;
import com.easefun.polyv.liveecommerce.modules.commodity.PLVECCommodityAdapter;
import com.easefun.polyv.liveecommerce.modules.commodity.PLVECCommodityPopupView;
import com.easefun.polyv.liveecommerce.modules.reward.PLVECRewardGiftAdapter;
import com.easefun.polyv.liveecommerce.modules.reward.PLVECRewardPopupView;
import com.easefun.polyv.liveecommerce.modules.reward.widget.PLVECRewardGiftAnimView;
import com.easefun.polyv.liveecommerce.scenes.fragments.widget.PLVECMorePopupView;
import com.easefun.polyv.liveecommerce.scenes.fragments.widget.PLVECWatchInfoView;
import com.easefun.polyv.thirdpart.blankj.utilcode.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 直播首页：主持人信息、聊天室、点赞、更多、商品、打赏
 */
public class PLVECLiveHomeFragment extends PLVBaseFragment implements View.OnClickListener {
    // <editor-fold defaultstate="collapsed" desc="成员变量">
    //观看信息布局
    private PLVECWatchInfoView watchInfoLy;
    private int currentWatchCount;
    //公告布局
    private PLVECBulletinView bulletinLy;
    //欢迎语
    private PLVECGreetingView greetLy;
    //聊天区域
    private PLVMessageRecyclerView chatMsgRv;
    private PLVECChatMessageAdapter chatMessageAdapter;
    private TextView sendMsgTv;
    private PLVECChatImgScanPopupView chatImgScanPopupView;
    //聊天室presenter
    private IPLVChatroomContract.IChatroomPresenter chatroomPresenter;
    //点赞区域
    private PLVECLikeIconView likeBt;
    private TextView likeCountTv;
    private int currentLikesCount;
    //更多
    private ImageView moreIv;
    private PLVECMorePopupView morePopupView;
    private int currentRoutePos;
    private Rect videoViewRect;
    //商品
    private ImageView commodityIv;
    private PLVECCommodityPopupView commodityPopupView;
    //打赏
    private ImageView rewardIv;
    private PLVECRewardPopupView rewardPopupView;
    private PLVECRewardGiftAnimView rewardGiftAnimView;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="生命周期方法">
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.plvec_live_page_home_fragment, null);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (onViewActionListener != null) {
            onViewActionListener.onViewCreated();
        }
        //初始化并登录聊天室
        chatroomPresenter.init();
        //直播带货需要允许使用子房间功能
        chatroomPresenter.setAllowChildRoom(true);
        chatroomPresenter.login();

        calculateCloudClassVideoViewRect();
        startLikeAnimationTask(5000);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化view">
    private void initView() {
        watchInfoLy = findViewById(R.id.watch_info_ly);
        bulletinLy = findViewById(R.id.bulletin_ly);
        greetLy = findViewById(R.id.greet_ly);
        chatMsgRv = findViewById(R.id.chat_msg_rv);
        PLVMessageRecyclerView.setLayoutManager(chatMsgRv).setStackFromEnd(true);
        chatMsgRv.addItemDecoration(new PLVMessageRecyclerView.SpacesItemDecoration(ConvertUtils.dp2px(4)));
        chatMsgRv.setAdapter(chatMessageAdapter = new PLVECChatMessageAdapter());
        chatMessageAdapter.setOnViewActionListener(onChatMsgViewActionListener);
        sendMsgTv = findViewById(R.id.send_msg_tv);
        sendMsgTv.setOnClickListener(this);
        likeBt = findViewById(R.id.like_bt);
        likeBt.setOnButtonClickListener(this);
        likeCountTv = findViewById(R.id.like_count_tv);
        moreIv = findViewById(R.id.more_iv);
        moreIv.setOnClickListener(this);
        commodityIv = findViewById(R.id.commodity_iv);
        commodityIv.setOnClickListener(this);
        rewardIv = findViewById(R.id.reward_iv);
        rewardIv.setOnClickListener(this);
        rewardGiftAnimView = findViewById(R.id.reward_ly);

        morePopupView = new PLVECMorePopupView();
        commodityPopupView = new PLVECCommodityPopupView();
        rewardPopupView = new PLVECRewardPopupView();
        chatImgScanPopupView = new PLVECChatImgScanPopupView();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="观看信息 - 数据处理">
    public void setClassDetailVO(PolyvLiveClassDetailVO liveClassDetailVO) {
        if (liveClassDetailVO == null) {
            return;
        }
        PolyvLiveClassDetailVO.DataBean dataBean = liveClassDetailVO.getData();
        String hasFormatLikes = dataBean.getLikes();
        likeCountTv.setText(hasFormatLikes);
        currentLikesCount = hasFormatLikes.endsWith("w")
                ? Integer.valueOf(hasFormatLikes.substring(0, hasFormatLikes.length() - 1)) * 10000
                : Integer.valueOf(hasFormatLikes);
        likeCountTv.setVisibility(View.VISIBLE);
        watchInfoLy.updateWatchInfo(dataBean.getCoverImage(), dataBean.getPublisher(), currentWatchCount = (dataBean.getPageView() + 1));
        watchInfoLy.setVisibility(View.VISIBLE);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="公告 - 显示、隐藏">
    private void acceptBulletinMessage(final PolyvBulletinVO bulletinVO) {
        bulletinLy.acceptBulletinMessage(bulletinVO);
    }

    private void removeBulletin() {
        bulletinLy.removeBulletin();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="欢迎语、观看热度 - 显示，更新">
    private void acceptLoginMessage(PolyvLoginEvent loginEvent) {
        //收到用户login后，增加观看热度
        if (!PolyvChatManager.getInstance().userId.equals(loginEvent.getUser().getUserId())) {//过滤自己，已在初始化时+1
            watchInfoLy.updateWatchCount(++currentWatchCount);
        }
        //显示欢迎语
        greetLy.acceptGreetingMessage(loginEvent);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="聊天室 - 添加信息至列表">
    private void addChatMessageToList(final List<PLVBaseViewData> chatMessageDataList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                chatMessageAdapter.addDataListChanged(chatMessageDataList);
                chatMsgRv.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="聊天室 - view层事件处理">
    private PLVAbsChatroomView chatroomView = new PLVAbsChatroomView() {
        @Override
        public void handleLoginIng(boolean isReconnect) {
            super.handleLoginIng(isReconnect);
            if (isReconnect) {
                PLVToastUtils.showShort("聊天室重连中");
            } else {
                PLVToastUtils.showShort("聊天室登录中");
            }
        }

        @Override
        public void handleLoginSuccess(boolean isReconnect) {
            super.handleLoginSuccess(isReconnect);
            if (isReconnect) {
                PLVToastUtils.showShort("聊天室重连成功");
            } else {
                PLVToastUtils.showShort("聊天室登录成功");
            }
        }

        @Override
        public void handleLoginFailed(@NonNull Throwable throwable) {
            super.handleLoginFailed(throwable);
            PLVToastUtils.showShort("聊天室连接失败: " + throwable.getMessage());
        }

        @Override
        public void onSpeakEvent(@NonNull PolyvSpeakEvent speakEvent) {
            super.onSpeakEvent(speakEvent);
        }

        @Override
        public int onSpeakTextSize() {
            return ConvertUtils.dp2px(12);//聊天列表里的文本信息textSize
        }

        @Override
        public void onImgEvent(@NonNull PolyvChatImgEvent chatImgEvent) {
            super.onImgEvent(chatImgEvent);
        }

        @Override
        public void onLikesEvent(@NonNull PolyvLikesEvent likesEvent) {
            super.onLikesEvent(likesEvent);
            acceptLikesMessage(likesEvent.getCount());
        }

        @Override
        public void onLoginEvent(@NonNull PolyvLoginEvent loginEvent) {
            super.onLoginEvent(loginEvent);
            acceptLoginMessage(loginEvent);
        }

        @Override
        public void onLogoutEvent(@NonNull PolyvLogoutEvent logoutEvent) {
            super.onLogoutEvent(logoutEvent);
        }

        @Override
        public void onBulletinEvent(@NonNull PolyvBulletinVO bulletinVO) {
            super.onBulletinEvent(bulletinVO);
            acceptBulletinMessage(bulletinVO);
        }

        @Override
        public void onRemoveBulletinEvent() {
            super.onRemoveBulletinEvent();
            removeBulletin();
        }

        @Override
        public void onCloseRoomEvent(@NonNull PolyvCloseRoomEvent closeRoomEvent) {
            super.onCloseRoomEvent(closeRoomEvent);
        }

        @Override
        public void onKickEvent(@NonNull PolyvKickEvent kickEvent, boolean isOwn) {
            super.onKickEvent(kickEvent, isOwn);
            if (isOwn) {
                PLVToastUtils.showShort("您已被管理员踢出聊天室！");
            }
        }

        @Override
        public void onLoginRefuseEvent(@NonNull PolyvLoginRefuseEvent loginRefuseEvent) {
            super.onLoginRefuseEvent(loginRefuseEvent);
            PLVToastUtils.showShort("您已被管理员踢出聊天室！");
        }

        @Override
        public void onReloginEvent(@NonNull PolyvReloginEvent reloginEvent) {
            super.onReloginEvent(reloginEvent);
            PLVToastUtils.showShort("该账号已在其他设备登录！");
        }

        @Override
        public void onCustomGiftEvent(@NonNull PolyvCustomEvent.UserBean userBean, @NonNull PLVCustomGiftBean customGiftBean) {
            showRewardGiftAnimView(userBean.getNick(), customGiftBean);
        }

        @Override
        public void onLocalMessage(PolyvLocalMessage localMessage) {
            super.onLocalMessage(localMessage);
            if (localMessage != null) {
                List<PLVBaseViewData> dataList = new ArrayList<>();
                dataList.add(new PLVBaseViewData<>(localMessage, PLVChatMessageItemType.ITEMTYPE_SPEAK));
                addChatMessageToList(dataList);
            }
        }

        @Override
        public void onChatMessageDataList(List<PLVBaseViewData> chatMessageDataList) {
            super.onChatMessageDataList(chatMessageDataList);
            addChatMessageToList(chatMessageDataList);
        }

        @Override
        public void setPresenter(IPLVChatroomContract.IChatroomPresenter presenter) {
            super.setPresenter(presenter);
            chatroomPresenter = presenter;
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="聊天室 - 获取view层实例">
    public IPLVChatroomContract.IChatroomView getChatroomView() {
        return chatroomView;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="聊天室 - 图片信息点击事件处理">
    PLVECChatMessageAdapter.OnViewActionListener onChatMsgViewActionListener = new PLVECChatMessageAdapter.OnViewActionListener() {
        @Override
        public void onChatImgClick(View view, String imgUrl) {
            chatImgScanPopupView.showImgScanLayout(view, imgUrl);
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="聊天室 - 输入窗口显示，信息发送">
    private void showInputWindow() {
        PLVECChatInputWindow.show(getActivity(), PLVECChatInputWindow.class, new PLVInputWindow.InputListener() {
            @Override
            public boolean onSendMsg(String message) {
                //sendValue > 0 时为发送成功，sendValue < 0 时为发送失败，发送失败的情况如下
                //-1	发送的信息为空
                //-2	sdk内部处理信息异常
                //-3	未连接上聊天室
                //-4	聊天室房间已关闭
                //-5	用户被踢
                //-6	用户被禁言
                //-7	非法参数
                //-8	聊天室未初始化
                PolyvLocalMessage localMessage = new PolyvLocalMessage(message);
                int sendValue = chatroomPresenter.sendTextMessage(localMessage);
                if (sendValue > 0 || sendValue == PolyvLocalMessage.SENDVALUE_BANIP) {//被禁言后也认为发送成功，但不会广播给其他用户
                    //把带表情的信息解析保存下来
                    localMessage.setObjects(PLVTextFaceLoader.messageToSpan(localMessage.getSpeakMessage(), ConvertUtils.dp2px(14), getContext()));
                    //发送成功
                    PLVToastUtils.showShort("发言成功");
                    return true;
                } else {
                    //发送失败
                    PLVToastUtils.showShort("发言失败: " + sendValue);
                    return false;
                }
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="点赞 - 数据处理，定时显示飘心任务">
    private void acceptLikesMessage(final int likesCount) {
        handler.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                startAddLoveIconTask(200, Math.min(5, likesCount));
                currentLikesCount = currentLikesCount + likesCount;
                String likesString = currentLikesCount > 10000 ?
                        String.format("%.1f", (double) currentLikesCount / 10000) + "w" : currentLikesCount + "";
                likeCountTv.setText(likesString);
            }
        });
    }

    private void startLikeAnimationTask(long ts) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int randomLikeCount = new Random().nextInt(5) + 1;
                startAddLoveIconTask(200, randomLikeCount);
                startLikeAnimationTask((new Random().nextInt(6) + 5) * 1000);
            }
        }, ts);
    }

    private void startAddLoveIconTask(final long ts, final int count) {
        if (count >= 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    likeBt.addLoveIcon(1);
                    startAddLoveIconTask(ts, count - 1);
                }
            }, ts);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="更多 - 布局显示，布局里的view事件处理">
    private void showMorePopupWindow(View v) {
        boolean isCurrentVideoMode = onViewActionListener == null || onViewActionListener.onGetMediaPlayModeAction() == PolyvMediaPlayMode.MODE_VIDEO;
        morePopupView.showLiveMoreLayout(v, isCurrentVideoMode, new PLVECMorePopupView.OnLiveMoreClickListener() {
            @Override
            public boolean onPlayModeClick(View view) {
                if (onViewActionListener != null) {
                    onViewActionListener.onChangeMediaPlayModeClick(view, view.isSelected() ? PolyvMediaPlayMode.MODE_VIDEO : PolyvMediaPlayMode.MODE_AUDIO);
                    return true;
                }
                return false;
            }

            @Override
            public int[] onShowRouteClick(View view) {
                return new int[]{onViewActionListener == null ? 1 : onViewActionListener.onGetRouteCountAction(), currentRoutePos};
            }

            @Override
            public void onRouteChangeClick(View view, int routePos) {
                if (currentRoutePos != routePos) {
                    currentRoutePos = routePos;
                    if (onViewActionListener != null) {
                        onViewActionListener.onChangeRouteClick(view, routePos);
                    }
                }
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="商品 - 数据处理，布局显示、商品链接跳转">
    public void setCommodityVO(PolyvCommodityVO commodityVO) {
        commodityPopupView.setCommodityVO(commodityVO);
    }

    private void showCommodityLayout(View v) {
        //清空旧数据
        commodityPopupView.setCommodityVO(null);
        //每次弹出都调用一次接口获取商品信息
        if (onViewActionListener != null) {
            onViewActionListener.onGetCommodityVOAction();
        }
        commodityPopupView.showCommodityLayout(v, new PLVECCommodityAdapter.OnViewActionListener() {
            @Override
            public void onBuyCommodityClick(View view, PolyvCommodityVO.DataBean.ContentsBean contentsBean) {
                commodityPopupView.hide();
                Uri uri = Uri.parse(contentsBean.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="打赏 - 布局显示，动画view显示">
    private void showRewardLayout(View v) {
        rewardPopupView.showRewardLayout(v, new PLVECRewardGiftAdapter.OnViewActionListener() {
            @Override
            public void onRewardClick(View view, PLVCustomGiftBean giftBean) {
                rewardPopupView.hide();
                String nickName = PolyvChatManager.getInstance().nickName;
                showRewardGiftAnimView(nickName, giftBean);
                //通过自定义信息事件发送礼物信息至聊天室
                chatroomPresenter.sendCustomGiftMessage(giftBean, nickName + " 赠送了" + giftBean.getGiftName());
            }
        });
    }

    private void showRewardGiftAnimView(String userName, PLVCustomGiftBean giftBean) {
        int giftDrawableId = getResources().getIdentifier("plvec_gift_" + giftBean.getGiftType(), "drawable", getContext().getPackageName());
        rewardGiftAnimView.acceptRewardGiftMessage(
                new PLVECRewardGiftAnimView.RewardGiftInfo(userName, giftBean.getGiftName(), giftDrawableId)
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="播放器 - 状态变化处理，切换线路变化处理">
    public void setPlayerState(PLVLivePlayerData.PlayerState state) {
        if (state == PLVLivePlayerData.PlayerState.Prepared) {
            morePopupView.updatePlayStateView(View.VISIBLE);
        } else if (state == PLVLivePlayerData.PlayerState.NoLive || state == PLVLivePlayerData.PlayerState.LiveEnd) {
            morePopupView.hide();
            morePopupView.updatePlayStateView(View.GONE);
        }
    }

    public void setPlayRoutePos(Integer integer) {
        currentRoutePos = integer == null ? 0 : integer;
        morePopupView.updateRouteView(new int[]{onViewActionListener == null ? 1 : onViewActionListener.onGetRouteCountAction(), currentRoutePos});
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="播放器 - 计算直播播放器横屏视频、音频模式的播放器区域位置">
    private void calculateCloudClassVideoViewRect() {
        watchInfoLy.post(new Runnable() {
            @Override
            public void run() {
                acceptVideoViewRectParams(watchInfoLy.getBottom(), 0);
            }
        });
        greetLy.post(new Runnable() {
            @Override
            public void run() {
                acceptVideoViewRectParams(0, greetLy.getTop());
            }
        });
    }

    private void acceptVideoViewRectParams(int top, int bottom) {
        if (videoViewRect == null) {
            videoViewRect = new Rect(0, top, 0, bottom);
        } else {
            videoViewRect = new Rect(0, Math.max(videoViewRect.top, top), 0, Math.max(videoViewRect.bottom, bottom));
            if (onViewActionListener != null) {
                onViewActionListener.onSetVideoViewRectAction(videoViewRect);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="点击事件">
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send_msg_tv) {
            showInputWindow();
        } else if (id == R.id.more_iv) {
            showMorePopupWindow(v);
        } else if (id == R.id.like_bt) {
            chatroomPresenter.sendLikeMessage();
            acceptLikesMessage(1);
        } else if (id == R.id.commodity_iv) {
            showCommodityLayout(v);
        } else if (id == R.id.reward_iv) {
            showRewardLayout(v);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="view事件监听器">
    private OnViewActionListener onViewActionListener;

    public void setOnViewActionListener(OnViewActionListener listener) {
        this.onViewActionListener = listener;
    }

    public interface OnViewActionListener {
        //切换播放模式
        void onChangeMediaPlayModeClick(View view, int mediaPlayMode);

        //切换线路
        void onChangeRouteClick(View view, int routePos);

        //获取播放模式
        int onGetMediaPlayModeAction();

        //获取线路数
        int onGetRouteCountAction();

        //设置播放器的位置
        void onSetVideoViewRectAction(Rect videoViewRect);

        //获取商品信息
        void onGetCommodityVOAction();

        void onViewCreated();
    }
    // </editor-fold>
}
