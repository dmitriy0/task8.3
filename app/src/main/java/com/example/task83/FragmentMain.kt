package com.example.task83

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class FragmentMain : Fragment() {

    var imageId = ""
    private val jsonFormat = Json { ignoreUnknownKeys = true }
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Fresco.initialize(requireContext())
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val like = view.findViewById<ImageView>(R.id.like)
        val dislike = view.findViewById<ImageView>(R.id.dislike)
        val favourites = view.findViewById<Button>(R.id.favourites)
        imageView = view.findViewById<SimpleDraweeView>(R.id.image)
        val about = view.findViewById<Button>(R.id.buttonAbout)

        val client = HttpClient(OkHttp)

        getRandomImage(client)

        like.setOnClickListener {
            App.instance?.needUpdateData = true

            GlobalScope.launch(Dispatchers.IO) {
                val response: String = client.post("https://api.thecatapi.com/v1/votes") {
                    headers {
                        append("Content-Type", "application/json")
                        append("x-api-key", "e7e933f7-09f6-43e3-a68a-b8e30c70e434")
                    }
                    body = Json.encodeToString(Vote(imageId, "user_id", 1))
                }
            }
            getRandomImage(client)
        }

        dislike.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val response: String = client.post("https://api.thecatapi.com/v1/votes") {
                    headers {
                        append("Content-Type", "application/json")
                        append("x-api-key", "e7e933f7-09f6-43e3-a68a-b8e30c70e434")
                    }
                    body = Json.encodeToString(Vote(imageId, "something_id", 0))
                }
            }
            getRandomImage(client)
        }

        favourites.setOnClickListener {
            findNavController().navigate(R.id.favouritesFragment)
        }

        about.setOnClickListener {
            findNavController().navigate(R.id.fragmentAbout)
        }

        return view
    }

    private fun getRandomImage(client: HttpClient) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = client.get<String>("https://api.thecatapi.com/v1/images/search")
            val catImage = jsonFormat.decodeFromString<List<Cat>>(response)
            requireActivity().runOnUiThread {
                val uri: Uri =
                    Uri.parse(catImage[0].url)

                imageView.setImageURI(uri)
                imageId = catImage[0].id
            }

        }
    }

}