package com.edwardlee259.reflectapp.ui.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.edwardlee259.reflectapp.R

class TimeDialogPreference : DialogPreference {

    private var mTime = 0

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, R.attr.dialogPreferenceStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    fun getTime(): Int = mTime

    fun setTime(time: Int) {
        mTime = time
        persistInt(time)

        val hours = when (time / 60) {
            0 -> 12
            else -> time / 60
        }.toString()
        val minutes = (time % 60).toString()
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInt(index, 0) ?: 0
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        setTime(getPersistedInt(defaultValue as Int? ?: mTime))
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.pref_time
    }

}