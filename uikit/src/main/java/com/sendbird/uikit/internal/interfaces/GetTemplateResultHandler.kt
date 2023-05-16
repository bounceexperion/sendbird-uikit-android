package com.sendbird.uikit.internal.interfaces

import com.sendbird.android.exception.SendbirdException

internal interface GetTemplateResultHandler {
    fun onResult(templateKey: String, jsonTemplate: String?, e: SendbirdException?)
}
