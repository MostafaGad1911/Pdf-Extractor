package mostafa.projects.pdfextractor

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    var userslist: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userslist.add(User(name = "Mostafa", age = 15, country = "Egypt"))
        userslist.add(User(name = "Ahmed", age = 25, country = "Egypt"))
        userslist.add(User(name = "Mohammed", age = 35, country = "Egypt"))
        userslist.add(User(name = "Kareem", age = 40, country = "Egypt"))

        var headers:ArrayList<String> = ArrayList()
        headers.add("Name")
        headers.add("Age")
        headers.add("Address")



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 212)
            } else {
                PdfExtractor().Builder()
                    .setDocsName("Gad")
                    .setDocsName("Gad15")
                    .setDocumentTitle("Gad Title")
                    .setHeaders(headers)
                    .setDocumentContent(userslist!! as ArrayList<Any>)
                    .build(this, onPdfExtracted = {
                    })
            }
        } else {
            PdfExtractor().Builder()
                .setDocsName("Gad")
                .setDocumentTitle("Gad Title")
                .setHeaders(headers)
                .setDocumentContent(userslist!! as ArrayList<Any>)
                .build(this, onPdfExtracted = {

                })
        }
    }
}