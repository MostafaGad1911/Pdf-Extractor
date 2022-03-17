package mostafa.projects.pdfextractor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Font.BOLD
import com.itextpdf.text.Font.NORMAL
import com.itextpdf.text.Paragraph
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
import kotlin.reflect.full.memberProperties


class PdfExtractor constructor() {
    var listTableContent: ArrayList<Any>? = null
    var title: String? = null
    var tableHeaders: ArrayList<String>? = null
    var docsName: String? = null

    private val STORAGE_CODE = 191110
    fun <T : Any> Activity.extractPdf(
        list: ArrayList<T>
    ) {
        for (i in list) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, STORAGE_CODE)
            } else {
                savePdf(list = list)
            }
        }
    }

    private fun <T : Any> Activity.savePdf(list: ArrayList<T>? = null) {
        for (i in list!!) {
            for (ex in i::class.memberProperties) {
                var type = ex.returnType.toString().replace("kotlin.", "")
                if (type?.endsWith("?"))
                    type = type.replace("?", "")
                Log.i("GadTest", "$type")
                if (type != "Int" && type != "String" && type != "Long" && type != "Double" && type != "Boolean") {
                    throw PdfExtractorException("Invalid data type $type Pdf Extractor support only primitive data types for table columns")
                    break

                }
            }

        }

        val jsonString = Gson().toJson(list?.get(0))
        val json = JSONObject(jsonString)
        if (json.length() > 0) {
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


                val rows: ArrayList<ArrayList<String>> = ArrayList()
                val jsonRowsString = Gson().toJson(list)
                val jsonArr = JSONArray(jsonRowsString)
                for (i in 0 until jsonArr.length()) {
                    val item = jsonArr.getJSONObject(i)

                    var row: ArrayList<String> = ArrayList()
                    for (key in item.keys()) {
                        row.add(item.get(key).toString())
                    }
                    row.reverse()
                    rows.add(row)
                }
                val table = PdfPTable(tableHeaders?.size!!)

                for (header in tableHeaders!!) {
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
                val m: Method =
                    StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)

                target.setDataAndType(Uri.fromFile(file), "application/pdf")
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                val intent = Intent.createChooser(target, "Open File")
                startActivity(intent)


            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Empty object", Toast.LENGTH_LONG).show()
        }
    }


    fun String.getLastWord(): String {
        val parts: Array<String> = this.split(" ").toTypedArray()
        return parts[parts.size - 1]
    }

    inner class Builder {


        // Document title
        fun setDocumentTitle(title: String) = apply {
            this@PdfExtractor.title = title
            val p1 = Paragraph(title)
            val titleFont = Font(Font.FontFamily.COURIER, 15F, BOLD)
            p1.alignment = Paragraph.ALIGN_CENTER
            p1.font = titleFont

        }

        // Document table headers
        fun setHeaders(headers: ArrayList<String>) =
            apply { this@PdfExtractor.tableHeaders = headers }

        // fill rows of table
        fun setDocumentContent(content: ArrayList<*>) =
            apply { this@PdfExtractor.listTableContent = content as ArrayList<Any> }

        // Docs names
        fun setDocsName(name: String) = apply { this@PdfExtractor.docsName = name }

        fun build(activity: Activity) =
            activity.extractPdf(listTableContent!!)

    }

    inner class PdfExtractorException(message: String) : Exception(message)

}