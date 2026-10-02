package com.example.partware

import org.junit.Assert.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testHttpRequest() {

        println("Printing test")

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://google-search-master.p.rapidapi.com/shopping?autocorrect=true&num=10&hl=en&gl=us&q=Apple%20watch%20&page=1")
            .get()
            .addHeader("x-rapidapi-key", "e7bb9367a1mshc804b648e983d42p171eb2jsn20dde69c1efb")
            .addHeader("x-rapidapi-host", "google-search-master.p.rapidapi.com")
            .build()

        val response = client.newCall(request).execute()

        println("Results: $response")
        println("Test finished")
    }
}