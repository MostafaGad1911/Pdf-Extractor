package mostafa.projects.pdfextractor

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

class User (
    @SerializedName("name")var _1name:String? = null,
    @SerializedName("age")var _2age:Int? = null,
    @SerializedName("country")var _3country:String? = null,
    @SerializedName("image")var _4image:String? = null,
)