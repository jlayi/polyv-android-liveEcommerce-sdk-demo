package com.easefun.polyv.liveecommerce.modules.chatroom;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.easefun.polyv.cloudclass.chat.PolyvChatManager;
import com.easefun.polyv.livecommon.modules.chatroom.holder.PLVChatMessageBaseViewHolder;
import com.easefun.polyv.livecommon.utils.span.PLVRadiusBackgroundSpan;
import com.easefun.polyv.livecommon.ui.widget.itemview.PLVBaseViewData;
import com.easefun.polyv.livecommon.ui.widget.itemview.adapter.PLVBaseAdapter;

/**
 * 聊天室信息共同viewHolder
 */
public class PLVECChatMessageCommonViewHolder<Data extends PLVBaseViewData, Adapter extends PLVBaseAdapter> extends PLVChatMessageBaseViewHolder<Data, Adapter> {
    protected SpannableStringBuilder nickSpan;

    public PLVECChatMessageCommonViewHolder(View itemView, Adapter adapter) {
        super(itemView, adapter);
    }

    private void resetParams() {
        nickSpan = null;
    }

    @Override
    public void processData(Data data, int position) {
        super.processData(data, position);
        resetParams();
        generateNickSpan();
    }

    private void generateNickSpan() {
        if (TextUtils.isEmpty(nickName)) {
            return;
        }
        nickSpan = new SpannableStringBuilder(nickName);
        if (PolyvChatManager.getInstance().userId.equals(userId)) {
            nickSpan.append("(我)");
        }
        nickSpan.append(": ");
        nickSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD16B")), 0, nickSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (PolyvChatManager.USERTYPE_TEACHER.equals(userType)) {
            insertActorToNickSpan(Color.parseColor("#F09343"));
        } else if (PolyvChatManager.USERTYPE_ASSISTANT.equals(userType)) {
            insertActorToNickSpan(Color.parseColor("#598FE5"));
        } else if (PolyvChatManager.USERTYPE_GUEST.equals(userType)) {
            insertActorToNickSpan(Color.parseColor("#EB6165"));
        } else if (PolyvChatManager.USERTYPE_MANAGER.equals(userType)) {
            insertActorToNickSpan(Color.parseColor("#33BBC5"));
        }
    }

    private void insertActorToNickSpan(int bgColor) {
        if (TextUtils.isEmpty(userType) || TextUtils.isEmpty(actor)) {
            return;
        }
        nickSpan.insert(0, userType);
        PLVRadiusBackgroundSpan radiusBackgroundSpan = new PLVRadiusBackgroundSpan(
                itemView.getContext(), bgColor, Color.parseColor("#ffffff"), actor);
        nickSpan.setSpan(radiusBackgroundSpan, 0, userType.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
