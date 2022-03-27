package mostafa.projects.pdfextractor

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import androidx.core.content.ContextCompat


class LoadingDialog(activity: Activity, var loadingColor: Int = android.R.color.holo_blue_dark) {
    private var _activity: Activity = activity
    private var _dialog: Dialog
    private var dialogInitiated: Boolean = false

    init {
        _dialog =
            Dialog(
                _activity,
                R.style.DialogFadeAnimation
            ).initDialog(R.layout.loading_lyt)

        var documentProgressBar = _dialog.findViewById<ProgressBar>(R.id.documentProgressBar)
        documentProgressBar.indeterminateTintList
        documentProgressBar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(activity, loadingColor),
            android.graphics.PorterDuff.Mode.SRC_IN)
    }

    fun startLoadingDialog() {
        Log.i("ShowDialog", "Test Test")
        if (!dialogInitiated)
            _dialog.show()
        dialogInitiated = true
    }

    private fun Dialog.initDialog(lyt: Int): Dialog {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        this.setCancelable(true)
        this.setCanceledOnTouchOutside(true)
        this.setContentView(lyt)

        this?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        return this
    }

    fun dismissDialog() {
        Log.i("DismissDialog", "Test Test")
        _dialog.hide()
        _dialog.dismiss()
    }
}