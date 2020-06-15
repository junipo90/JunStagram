package me.junipo.junstagram.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import me.junipo.junstagram.model.PushDTO
import okhttp3.*
import java.io.IOException

class FcmPush() {
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val url = "https://fcm.googleapis.com/fcm/send"
    val serverKey = "AAAAOpgnSzs:APA91bG832bw05W3JjyyY8Yw7dtQVcU5NA0Fy1NV9xMdlZgO9b0hcMuK0WClXc07xtkgIhQP-3w-33i0uGiJhoL-jfg4xdAh7oSNwq5z00KtJVQrnqGwsdiB-HHezXY3jPbKX72jDdoj"

    companion object{
        var instance = FcmPush()
    }
    var okHttpClient: OkHttpClient? = null
    var gson: Gson? = null
    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens")
            .document(destinationUid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var token = task?.result?.get("pushtoken").toString()
                println(token)
                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification?.title = title
                pushDTO.notification?.body = message

                var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                var request = Request
                    .Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=" + serverKey)
                    .url(url)
                    .post(body)
                    .build()
                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                    }
                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.string())
                    }
                })
            }
        }
    }
}