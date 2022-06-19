package com.example.task83

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ActivityFavourites : AppCompatActivity() {

    private lateinit var data: ArrayList<Cat>
    private lateinit var adapter: Adapter
    private var dao: Dao? = null

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        data = ArrayList<Cat>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = Adapter(data)
        recyclerView.adapter = adapter

        val db: Database? = App.instance?.getDatabase()
        dao = db?.dao()
        val thread = Thread {
            val entityImages: List<EntityImages?>? = dao?.getAll()

            if (entityImages!!.isEmpty()) {
                requestToApi()
                runOnUiThread {
                    Toast.makeText(this@ActivityFavourites, "from api", Toast.LENGTH_LONG).show()
                }
            } else {

                if (App.instance!!.needUpdateData) {
                    db!!.clearAllTables()
                    App.instance?.needUpdateData = false
                    requestToApi()
                    runOnUiThread {
                        Toast.makeText(this@ActivityFavourites, "from api", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    for (i in entityImages) {
                        val catImage =
                            jsonFormat.decodeFromString<Cat>(i!!.imageCat!!)
                        data.add(catImage)
                    }
                    runOnUiThread {
                        Toast.makeText(this@ActivityFavourites, "from room", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
        thread.start()
    }

    private fun requestToApi() {
        val client = HttpClient(OkHttp)
        GlobalScope.launch(Dispatchers.IO) {
            val responseVotes: String = client.get("https://api.thecatapi.com/v1/votes") {
                headers {
                    append("x-api-key", "e7e933f7-09f6-43e3-a68a-b8e30c70e434")
                }
                parameter("sub_id", "user_id")
            }


            val votes =
                jsonFormat.decodeFromString<List<Vote>>(responseVotes)


            var counter = 0
            for (i in votes) {
                if (i.value == 1) {
                    val responseImage: String =
                        client.get("https://api.thecatapi.com/v1/images/${i.image_id}") {
                            headers {
                                append("x-api-key", "e7e933f7-09f6-43e3-a68a-b8e30c70e434")
                            }
                        }
                    val catImage =
                        jsonFormat.decodeFromString<Cat>(responseImage)
                    data.add(catImage)

                    val entityImage = EntityImages()
                    entityImage.id = counter
                    entityImage.imageCat = responseImage
                    dao!!.insert(entityImage)
                    counter += 1

                    this@ActivityFavourites.runOnUiThread {
                        adapter.notifyItemInserted(data.size - 1)
                    }
                }
            }
        }
    }
}