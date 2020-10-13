package com.king.basemodel.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Gravity
import android.view.WindowManager
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.king.basemodel.R
import kotlinx.android.synthetic.main.layout_dialog_info.*


class InfoDialog(context: Context) : Dialog(context), View.OnClickListener {
    var yesListener: (() -> Unit)? = null
    var noListener: (() -> Unit)? = null

    init {
        initalize()
    }

    private fun initalize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_dialog_info)
        initWindow()
        tvOk.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }

    /**
     * 添加黑色半透明背景
     */
    private fun initWindow() {
        val dialogWindow = window
        dialogWindow!!.setBackgroundDrawable(ColorDrawable(0))//设置window背景
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)//设置输入法显示模式
        val lp = dialogWindow.attributes
        val d = context.resources.displayMetrics//获取屏幕尺寸
        lp.width = (d.widthPixels).toInt()
        lp.gravity = Gravity.CENTER  //中央居中
        dialogWindow.attributes = lp
    }

    fun show(title: String) {
        tvTitle.text = title
        tvContent.visibility = View.GONE
        vSplit.visibility = View.GONE
        tvOk.visibility = View.GONE
        tvCancel.visibility = View.VISIBLE
        tvCancel.text = "确定"
        super.show()
    }

    fun show(title: String, content: String, yesListener: (() -> Unit)?, noListener: (() -> Unit)?) {
        tvTitle.text = title
        tvContent.text = content
        this.yesListener = yesListener
        this.noListener = noListener
        if (content.isEmpty()) {
            tvContent.visibility = View.GONE
        }
        super.show()
    }

    fun show(title: String, btnTxt: String, noListener: (() -> Unit)?) {
        tvTitle.text = title
        tvContent.visibility = View.GONE
        vSplit.visibility = View.GONE
        tvOk.visibility = View.GONE
        tvCancel.visibility = View.VISIBLE
        tvCancel.text = btnTxt
        this.noListener = noListener
        super.show()
    }

    fun show(title: String, content: String, btnTxt1: String, btnTxt2: String, yesListener: (() -> Unit)?, noListener: (() -> Unit)?) {
        tvTitle.text = title
        tvContent.text = content
        tvCancel.text = btnTxt1
        tvOk.text = btnTxt2
        this.yesListener = yesListener
        this.noListener = noListener
        if (content.isEmpty()) {
            tvContent.visibility = View.GONE
        }
        if (btnTxt1.isEmpty()) {
            tvCancel.visibility = View.GONE
            vSplit.visibility = View.GONE
        }
        super.show()
    }

    fun show(title: String, content: String, btnTxt1: String, btnTxt2: String, cancelable: Boolean, yesListener: (() -> Unit)?, noListener: (() -> Unit)?) {
        show(title, content, btnTxt1, btnTxt2, yesListener, noListener)
        setCancelable(cancelable)
        super.show()
    }

    fun show(title: String, content: String, btnTxt1: String, btnTxt2: String, btnColor2: String, yesListener: (() -> Unit)?, noListener: (() -> Unit)?) {
        tvOk.setTextColor(Color.parseColor(btnColor2))
        show(title, content, btnTxt1, btnTxt2, yesListener, noListener)
    }

    override fun onClick(v: View) {
        when (v) {
            tvOk -> {
                dismiss()
                yesListener?.let {
                    yesListener!!.invoke()
                }
            }
            tvCancel -> {
                dismiss()
                noListener?.let {
                    noListener!!.invoke()
                }
            }
        }
    }

}