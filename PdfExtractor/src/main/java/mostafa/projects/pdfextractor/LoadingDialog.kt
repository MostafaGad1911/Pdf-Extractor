package mostafa.projects.pdfextractor

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager




class LoadingDialog(activity: Activity) {
    private var _activity:Activity = activity
    private lateinit var _dialog: Dialog


    fun startLoadingDialog(){
        _dialog =
            Dialog(
                _activity,
                R.style.DialogFadeAnimation
            ).initDialog(R.layout.loading_lyt)
        _dialog.show()
    }

    fun Dialog.initDialog(lyt: Int): Dialog {
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

    fun dismissDialog(){
        Log.i("DismissDialog" , "Test Test")
        _dialog.hide()
        _dialog.dismiss()
    }
}