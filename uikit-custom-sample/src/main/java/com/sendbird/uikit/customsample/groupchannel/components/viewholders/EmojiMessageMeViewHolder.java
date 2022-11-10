package com.sendbird.uikit.customsample.groupchannel.components.viewholders;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sendbird.android.channel.BaseChannel;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.Reaction;
import com.sendbird.android.message.SendingStatus;
import com.sendbird.uikit.activities.viewholder.GroupChannelMessageViewHolder;
import com.sendbird.uikit.consts.ClickableViewIdentifier;
import com.sendbird.uikit.customsample.databinding.ViewEmojiMessageMeHolderBinding;
import com.sendbird.uikit.customsample.utils.DrawableUtils;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.interfaces.OnItemLongClickListener;
import com.sendbird.uikit.model.MessageListUIParams;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ViewHolder to draw the emoji message sent from current user in the <code>GroupChannel</code>.
 */
public class EmojiMessageMeViewHolder extends GroupChannelMessageViewHolder {
    private final ViewEmojiMessageMeHolderBinding binding;

    public EmojiMessageMeViewHolder(@NonNull ViewEmojiMessageMeHolderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull BaseChannel channel, @NonNull BaseMessage message, @NonNull MessageListUIParams params) {
        Context context = binding.getRoot().getContext();
        boolean sendingState = message.getSendingStatus() == SendingStatus.SUCCEEDED;

        binding.tvSentAt.setVisibility(sendingState ? View.VISIBLE : View.GONE);
        String sentAt = DateUtils.formatDateTime(context, message.getCreatedAt(), DateUtils.FORMAT_SHOW_TIME);
        binding.tvSentAt.setText(sentAt);
        DrawableUtils.drawStatus(binding.ivStatus, message);
        Glide.with(itemView)
                .load(message.getMessage())
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivEmoji);

        int paddingTop = context.getResources().getDimensionPixelSize(com.sendbird.uikit.R.dimen.sb_size_8);
        int paddingBottom = context.getResources().getDimensionPixelSize(com.sendbird.uikit.R.dimen.sb_size_8);
        binding.root.setPadding(binding.root.getPaddingLeft(), paddingTop, binding.root.getPaddingRight(), paddingBottom);
    }

    @Override
    public void setEmojiReaction(@NonNull List<Reaction> reactionList, @Nullable OnItemClickListener<String> emojiReactionClickListener, @Nullable OnItemLongClickListener<String> emojiReactionLongClickListener, @Nullable View.OnClickListener moreButtonClickListener) {
    }

    @NonNull
    @Override
    public Map<String, View> getClickableViewMap() {
        return new ConcurrentHashMap<String, View>() {{
            put(ClickableViewIdentifier.Chat.name(), binding.ivEmoji);
        }};
    }
}
