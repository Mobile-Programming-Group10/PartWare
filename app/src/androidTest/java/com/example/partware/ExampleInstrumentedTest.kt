package com.example.partware

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestApiRequest {

    private val client = OkHttpClient()

    fun jsonResponse(urlRequest: Request, urlResponse: Response){
        println("Results: $urlResponse")

        // Receive the JSON body response for specific item
        client.newCall(urlRequest).execute().use { response ->

            val body = response.body?.string()

            println("Status: ${response.code}")

            // Formatting JSON body for a clean output
            if (body != null) {
                val json = JSONObject(body)

                println(json.toString(4))
            }
        }

    }

    @Test
    fun googleTest() {

        println("Testing googleTest")

        val request = Request.Builder()
            .url("https://google-search-master.p.rapidapi.com/shopping?autocorrect=true&num=10&hl=en&gl=us&q=Apple%20watch%20&page=1")
            .get()
            .addHeader("x-rapidapi-key", "e7bb9367a1mshc804b648e983d42p171eb2jsn20dde69c1efb")
            .addHeader("x-rapidapi-host", "google-search-master.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()

        jsonResponse(request, response)

        println("googleTest Test Finished")
    }

    @Test
    fun amazonTest() {

        println("Testing amazonTest")

        val request = Request.Builder()
            .url("https://real-time-amazon-data.p.rapidapi.com/search?brand=Asus&is_prime=false&country=US&page=1&four_stars_and_up=true&query=Phone")
            .get()
            .addHeader("x-rapidapi-key", "e7bb9367a1mshc804b648e983d42p171eb2jsn20dde69c1efb")
            .addHeader("x-rapidapi-host", "real-time-amazon-data.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()

        println("Results: $response")

        jsonResponse(request, response)

        println("amazonTest Test Finished")

    }

    @Test
    fun ebayTest() {
        println("Testing ebayTest")
        val request = Request.Builder()
            .url("https://real-time-ebay-data.p.rapidapi.com/search_more?tld=com&query=iphone%20x")
            .get()
            .addHeader("x-rapidapi-key", "e7bb9367a1mshc804b648e983d42p171eb2jsn20dde69c1efb")
            .build()

        val response = client.newCall(request).execute()

        jsonResponse(request, response)

        println("ebayTest Test Finished")
    }

}