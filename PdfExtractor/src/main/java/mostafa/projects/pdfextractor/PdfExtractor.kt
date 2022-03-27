package mostafa.projects.pdfextractor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.itextpdf.text.*
import com.itextpdf.text.Font.BOLD
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


class PdfExtractor {

    var listTableContent: ArrayList<Any>? = null
    var title: String? = null
    var tableHeaders: ArrayList<String>? = null
    var docsName: String? = null
    var ExtractorDirection = 0

    companion object {
        var ExtractorRTL = 1
        var ExtractorLTR = 2
    }

    var headerColor = R.color.grey
    var cellColor = R.color.white
    var headerTextColor = R.color.white
    var cellTextColor = R.color.white
    var loadingColor = R.color.blue


    val STORAGE_CODE = 191110
    lateinit var loadingDialog: LoadingDialog


    inline fun <reified T : Any> Activity.extractPdf(
        list: ArrayList<T>,
    ) {
        this.showLoading()
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, STORAGE_CODE)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                savePdf(list = list)
            }

        }

    }

    fun Activity.showLoading() {
        loadingDialog = LoadingDialog(this, loadingColor)
        loadingDialog.startLoadingDialog()
    }

    fun hideLoading() {
        loadingDialog.dismissDialog()
    }


    inline fun <reified T : Any> Activity.savePdf(list: ArrayList<T>? = null) {
        for (i in list!!) {
            for (ex in i::class.memberProperties) {
                var type = ex.returnType.toString().replace("kotlin.", "")
                if (type?.endsWith("?"))
                    type = type.replace("?", "")
                Log.i("GadTest", "$type")
                if (type != "Int" && type != "Long" && type != "Float" && type != "Double" && type != "Boolean" && type != "String") {
                    throw PdfExtractorException("Invalid data type $type not supported in pdf extractor tables")
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

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                PdfWriter.getInstance(myDoc, FileOutputStream(mFilePath))
                myDoc.open()


                val rows: ArrayList<ArrayList<String>> = ArrayList()
                val jsonRowsString = Gson().toJson(list)
                val jsonArr = JSONArray(jsonRowsString)
                for (i in 0 until jsonArr.length()) {
                    val item = jsonArr.getJSONObject(i)

                    var row: ArrayList<String> = ArrayList()
                    for (key in item.keys()) {
                        row.add(item.get(key).toString())
                    }
                    rows.add(row)
                }
                val table = PdfPTable(getTableWidths(list)!!)
                when (ExtractorDirection) {
                    ExtractorRTL -> {
                        table.runDirection = PdfWriter.RUN_DIRECTION_RTL
                    }
                    ExtractorLTR -> {
                        table.runDirection = PdfWriter.RUN_DIRECTION_LTR
                    }
                    else -> {
                        throw PdfExtractorException("PdfExtractor table direction not detected use setTableDirection to enable it")
                    }
                }

                for (header in tableHeaders!!) {
                    var cell = createHeaderCell(header)
                    table.addCell(cell)
                }
                table.completeRow()


                readProperties(list[0])


                rows.forEachIndexed { _, element ->
                    for (data in element) {
                        if (!data.isValidUrl()) {
                            table.addCell(createTextCell(data))
                        } else {
                            table.addCell(createImageCell(data))

                        }
                    }
                    table.completeRow()
                }


                myDoc.addAuthor("PdfExtractor")
                myDoc.add(table)
                myDoc.close()


                CoroutineScope(Dispatchers.Main).launch {
                    hideLoading()
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
                }


            } catch (e: Exception) {
                throw PdfExtractorException(e.localizedMessage.toString())
            }
        } else {
            throw PdfExtractorException("Empty object")
        }
    }

    fun <T : Any> readProperties(instance: T) {
        val clazz = instance.javaClass.kotlin
        clazz.declaredMemberProperties.forEach {
            println("GadNewTest  ${it.get(instance)}")
        }
    }


    fun <T : Any> getTableWidths(list: ArrayList<T>? = null): FloatArray {
        var widths: ArrayList<Float> = ArrayList()
        val rows: ArrayList<ArrayList<String>> = ArrayList()
        val jsonRowsString = Gson().toJson(list)
        val jsonArr = JSONArray(jsonRowsString)
        for (i in 0 until jsonArr.length()) {
            val item = jsonArr.getJSONObject(i)

            var row: ArrayList<String> = ArrayList()
            for (key in item.keys()) {
                row.add(item.get(key).toString())
            }
            rows.add(row)
        }
        rows.forEachIndexed { index, element ->
            for (data in element) {
                if (!data.isValidUrl()) {
                    if (index == 0) {
                        widths.add(140f)

                    }
                } else {
                    if (index == 0) {
                        widths.add(190f)
                    }

                }
            }
        }

        Log.i("MyWidths", "$widths")

        return widths.toFloatArray()

    }


    inline fun String.isValidUrl() = URLUtil.isValidUrl(this)

    @Throws(DocumentException::class, IOException::class, NullPointerException::class)
    fun Activity.createImageCell(path: String?): PdfPCell? {
        var cell = PdfPCell()
        val myImg = Image.getInstance(path)
        myImg.scaleAbsolute(190f, 100f)
        myImg.isScaleToFitHeight = true
        myImg.isScaleToFitLineWhenOverflow = true
        if (isColorResource(cellColor)) {
            myImg.backgroundColor =
                BaseColor(ContextCompat.getColor(this, cellColor))
        } else {
            throw PdfExtractorException("Invalid cell color resource : $cellColor")
        }
        when (ExtractorDirection) {
            ExtractorRTL -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            }
            ExtractorLTR -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_LTR
            }
            else -> {
                throw PdfExtractorException("PdfExtractor cell direction not detected use setTableDirection to enable it")
            }
        }
        cell = PdfPCell(myImg, true)
        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun Activity.createTextCell(text: String?): PdfPCell? {
        val bf = BaseFont.createFont(
            "res/font/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED
        )
        val enFont = Font(bf, 10f)
        if (isColorResource(cellTextColor)) {
            enFont.color = BaseColor(ContextCompat.getColor(this, cellTextColor))
        } else {
            throw PdfExtractorException("Invalid cell text color resource : $cellTextColor")
        }
        val phrase = Phrase("$text", enFont)
        val cell = PdfPCell()
        cell.addElement(phrase)
        cell.phrase = phrase
        cell.isUseDescender = true
        if (isColorResource(cellColor)) {
            cell.backgroundColor =
                BaseColor(ContextCompat.getColor(this, cellColor))
        } else {
            throw PdfExtractorException("Invalid cell color resource : $cellColor")
        }
        cell.verticalAlignment = Element.ALIGN_CENTER
        when (ExtractorDirection) {
            ExtractorRTL -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            }
            ExtractorLTR -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_LTR
            }
            else -> {
                throw PdfExtractorException("PdfExtractor cell direction not detected use setTableDirection to enable it")
            }
        }
        cell.horizontalAlignment = Element.ALIGN_CENTER


        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun Activity.createHeaderCell(text: String?): PdfPCell? {
        val bf = BaseFont.createFont(
            "res/font/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED
        )
        val enFont = Font(bf, 12f)
        if (isColorResource(headerTextColor)) {
            enFont.color = BaseColor(ContextCompat.getColor(this, headerTextColor))
        } else {
            throw PdfExtractorException("Invalid header text color resource : $headerTextColor")
        }
        val phrase = Phrase("$text", enFont)
        val cell = PdfPCell()
        cell.addElement(phrase)
        cell.phrase = phrase
        cell.isUseDescender = true
        if (isColorResource(headerColor)) {
            cell.backgroundColor = BaseColor(ContextCompat.getColor(this, headerColor))
        } else {
            throw PdfExtractorException("Invalid header color resource : $headerColor")
        }
        cell.verticalAlignment = Element.ALIGN_CENTER
        when (ExtractorDirection) {
            ExtractorRTL -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            }
            ExtractorLTR -> {
                cell.runDirection = PdfWriter.RUN_DIRECTION_LTR
            }
            else -> {
                throw PdfExtractorException("PdfExtractor cell direction not detected use setTableDirection to enable it")
            }
        }
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
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
            apply {
                if (headers.size > 6) {
                    throw PdfExtractorException("Pdf extractor support to 6 columns only")
                } else {
                    this@PdfExtractor.tableHeaders = headers
                }
            }

        // fill rows of table
        fun setDocumentContent(content: ArrayList<*>) =
            apply { this@PdfExtractor.listTableContent = content as ArrayList<Any> }

        // Docs names
        fun setDocsName(name: String) = apply { this@PdfExtractor.docsName = name }

        fun setHeaderColor(colorHeader: Int) =
            apply { this@PdfExtractor.headerColor = colorHeader }

        fun setCellColor(colorCell: Int) = apply { this@PdfExtractor.cellColor = colorCell }

        fun setHeaderTextColor(colorTxtHeader: Int) =
            apply { this@PdfExtractor.headerTextColor = colorTxtHeader }

        fun setCellTextColor(colorTxtCell: Int) =
            apply { this@PdfExtractor.cellTextColor = colorTxtCell }

        fun setLoadingColor(colorLoading: Int) =
            apply { this@PdfExtractor.loadingColor = colorLoading }

        fun setTableDirection(direction: Int) = apply {
            if (direction != ExtractorLTR && direction != ExtractorRTL)
                throw PdfExtractorException("Pdf Extractor invalid direction , check documentation in github for valid direction ExtractorLTR , ExtractorRTL ")
            else
                ExtractorDirection = direction
        }

        fun build(activity: Activity) =
            activity.extractPdf(listTableContent!!)

    }

    private fun Activity.isColorResource(value: Int): Boolean {
        return try {
            ResourcesCompat.getColor(resources, value, null)
            true
        } catch (e: Resources.NotFoundException) {
            false
        }
    }

    inner class PdfExtractorException(message: String) : Exception(message)

}