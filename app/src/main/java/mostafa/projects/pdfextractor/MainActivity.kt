package mostafa.projects.pdfextractor

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var usersList: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersList.add(User(name = "Mostafa", age = 15, country = "Egypt"))
        usersList.add(User(name = "Ahmed", age = 25, country = "Egypt"))
        usersList.add(User(name = "Mohammed", age = 35, country = "Egypt"))
        usersList.add(User(name = "Kareem", age = 40, country = "Egypt"))

        val headers: ArrayList<String> = ArrayList()
        headers.add("Name")
        headers.add("Age")
        headers.add("Address")



        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 212)
        } else {
            PdfExtractor().Builder()
                .setDocsName("Gad")
                .setDocumentTitle("Gad Title")
                .setHeaders(headers)
                .setDocumentContent(usersList)
                .build(this)

        }
    }
}