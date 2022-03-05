package mostafa.projects.pdfextractor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Font.BOLD
import com.itextpdf.text.Font.NORMAL
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*


class PdfExtractor {
    companion object {
        const val STORAGE_CODE = 191110
         fun <T> Activity.extractPdf(
            list: ArrayList<T>,
            onPdfExtracted: (PdfDocument) -> Unit
        ) {
            for (i in list) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, STORAGE_CODE)
                    } else {
                        savePdf(list = list)
                    }

                } else {
                    savePdf(list = list)
                }
            }

        }

        inline fun <T> Activity.savePdf(list: ArrayList<T>) {
            val headers:java.util.ArrayList<String> = ArrayList()
            val jsonString = Gson().toJson(list[0])
            val json = JSONObject(jsonString)
            for (i in 0 until json.names().length()) {
                headers.add(json.names().getString(i)).toString()

            }
            headers.reverse()

            val myDoc = Document()
            val mFileName = SimpleDateFormat(
                "yyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            val mFilePath =
                Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"
            try {
                PdfWriter.getInstance(myDoc, FileOutputStream(mFilePath))
                myDoc.open()

                val fontHeader = Font(Font.FontFamily.TIMES_ROMAN, 12F, BOLD)
                val fontRow = Font(Font.FontFamily.TIMES_ROMAN, 10F, NORMAL)



                val rows:ArrayList<java.util.ArrayList<String>> = ArrayList()
                val jsonRowsString = Gson().toJson(list)
                val jsonArr = JSONArray(jsonRowsString)
                for (i in 0 until jsonArr.length()) {
                    val item = jsonArr.getJSONObject(i)

                    var row :ArrayList<String> = ArrayList()
                    for (key in item.keys()) {
                        row.add(item.get(key).toString())
                    }
                    row.reverse()
                    rows.add(row)
                }
                val table = PdfPTable(headers.size)

                for (header in headers) {
                    val cell = PdfPCell()
                    cell.grayFill = 0.9f
                    cell.phrase = Phrase(header.lowercase(), fontHeader)
                    table.addCell(cell)
                }
                table.completeRow()

                for (row in rows) {
                    for (data in row) {
                        val phrase = Phrase(data, fontRow)
                        table.addCell(PdfPCell(phrase))
                    }
                    table.completeRow()
                }

                myDoc.addAuthor("MostafaGad1911")
                myDoc.add(table)
                myDoc.close()
                Toast.makeText(this, "$mFileName is created .. ", Toast.LENGTH_LONG).show()

                val target = Intent(Intent.ACTION_VIEW)

                var file = File(mFilePath)
                val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)

                target.setDataAndType(Uri.fromFile(file), "application/pdf")
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                val intent = Intent.createChooser(target, "Open File")
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }



}
