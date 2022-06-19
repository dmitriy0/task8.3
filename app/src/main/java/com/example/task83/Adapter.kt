package com.example.task83

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Adapter(private val data: ArrayList<Cat>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.name)!!
        val temperament = view.findViewById<TextView>(R.id.temperament)!!
        val image = view.findViewById<SimpleDraweeView>(R.id.image)!!
        val dislike = view.findViewById<ImageView>(R.id.dislike)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = "Name: ${data[position].breeds[0].name}"
        holder.temperament.text = "Temperament: ${data[position].breeds[0].temperament}"
        val uri: Uri = Uri.parse(data[position].url)
        holder.image.setImageURI(uri)

        holder.dislike.setOnClickListener {
            App.instance?.needUpdateData = true

            val client = HttpClient(OkHttp)
            GlobalScope.launch(Dispatchers.IO) {
                val response: String = client.post("https://api.thecatapi.com/v1/votes") {
                    headers {
                        append("Content-Type", "application/json")
                        append("x-api-key", "e7e933f7-09f6-43e3-a68a-b8e30c70e434")
                    }
                    body = Json.encodeToString(Vote(data[position].id, "user_id", 0))
                    data.removeAt(position)
                }
            }
            this@Adapter.notifyItemRemoved(position)
            this@Adapter.notifyItemRangeChanged(position, data.size)
        }
    }

    override fun getItemCount(): Int = data.size
}