package com.sendbird.uikit.samples.common.extensions

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sendbird.android.SendbirdChat
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.ConnectHandler
import com.sendbird.android.handler.PushRequestCompleteHandler
import com.sendbird.android.push.SendbirdPushHelper
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.activities.FeedNotificationChannelActivity
import com.sendbird.uikit.log.Logger
import com.sendbird.uikit.providers.AdapterProviders
import com.sendbird.uikit.providers.FragmentProviders
import com.sendbird.uikit.providers.ModuleProviders
import com.sendbird.uikit.providers.ViewModelProviders
import com.sendbird.uikit.samples.R
import com.sendbird.uikit.samples.aichatbot.AIChatBotHomeActivity
import com.sendbird.uikit.samples.basic.BasicHomeActivity
import com.sendbird.uikit.samples.basic.GroupChannelMainActivity
import com.sendbird.uikit.samples.common.LoginActivity
import com.sendbird.uikit.samples.common.SelectServiceActivity
import com.sendbird.uikit.samples.common.consts.SampleType
import com.sendbird.uikit.samples.common.consts.StringSet
import com.sendbird.uikit.samples.common.preferences.PreferenceUtils
import com.sendbird.uikit.samples.common.widgets.WaitingDialog
import com.sendbird.uikit.samples.customization.CustomizationHomeActivity
import com.sendbird.uikit.samples.notification.NotificationHomeActivity
import com.sendbird.uikit.samples.notification.NotificationLoginActivity
import com.sendbird.uikit.samples.notification.NotificationMainActivity

internal fun SampleType?.getLogoDrawable(context: Context): Drawable? {
    return when (this) {
        null -> R.drawable.logo_sendbird
        SampleType.Basic -> R.drawable.logo_sendbird
        SampleType.Notification -> R.drawable.logo_notifications
        SampleType.Customization -> R.drawable.logo_sendbird
        SampleType.AiChatBot -> R.drawable.logo_sendbird
    }.let { ContextCompat.getDrawable(context, it) }
}

internal fun SampleType?.startingIntent(context: Context): Intent {
    val userId = PreferenceUtils.userId
    return when (this) {
        null -> Intent(context, SelectServiceActivity::class.java)
        SampleType.Basic -> {
            if (userId.isNotEmpty()) {
                Intent(context, BasicHomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }
        SampleType.Notification -> {
            if (userId.isNotEmpty()) {
                Intent(context, NotificationHomeActivity::class.java)
            } else {
                Intent(context, NotificationLoginActivity::class.java)
            }
        }
        SampleType.Customization -> {
            if (userId.isNotEmpty()) {
                Intent(context, CustomizationHomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }
        SampleType.AiChatBot -> {
            if (userId.isNotEmpty()) {
                Intent(context, AIChatBotHomeActivity::class.java)
            } else {
                Intent(context, LoginActivity::class.java)
            }
        }
    }
}

internal fun SampleType?.newRedirectToChannelIntent(
    context: Context,
    channelUrl: String,
    messageId: Long,
    channelType: String
): Intent {
    return when (this) {
        SampleType.Notification -> {
            val isUsingFeedOnly: Boolean = PreferenceUtils.isUsingFeedChannelOnly
            if (!isUsingFeedOnly) {
                NotificationMainActivity.newRedirectToChannelIntent(
                    context,
                    channelUrl,
                    messageId,
                    channelType,
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            } else {
                FeedNotificationChannelActivity.newIntent(context, channelUrl).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            }
        }
        else -> {
            Intent(context, GroupChannelMainActivity::class.java).apply {
                putExtra(StringSet.KEY_CHANNEL_URL, channelUrl)
                putExtra(StringSet.PUSH_REDIRECT_CHANNEL, channelUrl)
                putExtra(StringSet.PUSH_REDIRECT_MESSAGE_ID, messageId)
                putExtra(StringSet.PUSH_REDIRECT_CHANNEL_TYPE, channelType)
            }
        }
    }
}

internal fun Activity.logout() {
    WaitingDialog.show(this)
    SendbirdPushHelper.unregisterPushHandler(object : PushRequestCompleteHandler {
        override fun onComplete(isRegistered: Boolean, token: String?) {
            SendbirdUIKit.disconnect {
                WaitingDialog.dismiss()
                PreferenceUtils.clearAll()
                cleanUpPreviousSampleSettings()
                val notificationManager =
                    getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
                startActivity(Intent(this@logout, SelectServiceActivity::class.java))
                finish()
            }
        }

        override fun onError(e: SendbirdException) {
            Logger.w(e)
            WaitingDialog.dismiss()
        }
    })
}

/**
 * Clean up previous sample settings.
 */
internal fun cleanUpPreviousSampleSettings() {
    // clear providers
    AdapterProviders.resetToDefault()
    FragmentProviders.resetToDefault()
    ModuleProviders.resetToDefault()
    ViewModelProviders.resetToDefault()

    // clear custom params handler
    SendbirdUIKit.setCustomParamsHandler(null)

    // clear custom user list query handler to use default user list query
    SendbirdUIKit.setCustomUserListQueryHandler(null)
}

internal fun authenticate(handler: ConnectHandler) {
    if (PreferenceUtils.isUsingFeedChannelOnly) {
        SendbirdUIKit.authenticateFeed(handler::onConnected)
        return
    }
    SendbirdUIKit.connect(handler)
}

internal fun SendbirdUIKit.ThemeMode.isUsingDarkTheme() = this == SendbirdUIKit.ThemeMode.Dark

internal fun getFeedChannelUrl(): String {
    return SendbirdChat.appInfo?.let {
        val feedChannels = it.notificationInfo?.feedChannels
        feedChannels?.get(StringSet.feed)
    } ?: ""
}
