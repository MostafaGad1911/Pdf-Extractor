package mostafa.projects.pdfextractor

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mostafa.projects.pdfextractor.PdfExtractor.Companion.ExtractorLTR

class MainActivity : AppCompatActivity() {
    private var usersList: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var user = User()
        user._1name = "Mostafa"
        user._2age = 28
        user._3country = "Egypt"
        user._4image = "https://png.pngtree.com/png-clipart/20210418/original/pngtree-lettering-ramadan-calligraphy-sticker-arabic-marhaban-ya-ramadhan-kareem-text-png-image_6237398.jpg"


        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)

        val headers: ArrayList<String> = ArrayList()
        headers.add("الإســم")
        headers.add("العمر")
        headers.add("العـنوان")
        headers.add("الصورة")



        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 212)
        } else {
            PdfExtractor().Builder()
                .setDocsName("Gad")
                .setDocumentTitle("Gad Title")
                .setHeaders(headers)
                .setDocumentContent(usersList)
                .setCellColor(R.color.orange)
                .setHeaderColor(R.color.blue)
                .setCellTextColor(R.color.blue)
                .setHeaderTextColor(R.color.white)
                .setTableDirection(ExtractorLTR)
                .setLoadingColor(R.color.blue)
                .build(this)

        }
    }
}