package mostafa.projects.pdfextractor

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mostafa.projects.pdfextractor.PdfExtractor.Companion.extractPdf
import mostafa.projects.pdfextractor.PdfExtractor.Companion.savePdf

class MainActivity : AppCompatActivity() {
    var userslist: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userslist.add(User(name = "Mostafa",age =  15 ,  country = "Egypt"))
        userslist.add(User(name = "Ahmed", age = 25 ,  country = "Egypt"))
        userslist.add(User(name = "Mohammed",age =  35 ,  country = "Egypt"))
        userslist.add(User(name = "Kareem", age = 40,  country = "Egypt"))

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PdfExtractor.STORAGE_CODE)
            } else {
                extractPdf(list = userslist, onPdfExtracted = {

                })
            }

        } else {
            extractPdf(list = userslist , onPdfExtracted = {

            })
        }
    }
}