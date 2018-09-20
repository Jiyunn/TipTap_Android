package me.tiptap.tiptap.data

import android.net.Uri
import okhttp3.MultipartBody
import java.util.*

class Diary() {
    var id: Int = 0
    var date: Date? = null
    //real val
    var content: String = ""
    var location: String = ""
    var photo: Uri? = null
    var isSelected: Boolean = false
    var latitude: String = ""
    var longitude: String = ""
    var diaryFile: MultipartBody.Part? = null


    constructor(id: Int, date: Date, content: String, location : String, photo: Uri) : this() {
        this.id = id
        this.date = date
        this.content = content
        this.location = location
        this.photo = photo
    }

}


