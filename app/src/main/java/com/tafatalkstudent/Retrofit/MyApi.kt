package com.tafatalkstudent.Retrofit

import com.google.gson.GsonBuilder
import com.tafatalkstudent.Shared.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface MyApi {

    companion object {

        //https://howtodoinjava.com/retrofit2/query-path-parameters/

        operator fun invoke(): MyApi {
            /*progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
            sessionManager = SessionManager(this)*/

            val gson = GsonBuilder().serializeNulls().create()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // set connect timeout to 30 seconds
                .readTimeout(30, TimeUnit.SECONDS) // set read timeout to 30 seconds
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.baseurl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
            val apiCall = retrofit.create(MyApi::class.java)

            return apiCall

        }
    }




    @GET("api/v1/schools/standingtoken/{id}")
    suspend fun getStandingTokenForSchool(
        @Path("id") id: String,
    ): Response<Double?>

    @GET("api/v1/mobiles/id/{id}")
    suspend fun getMobileId(
        @Path("id") id: String,
    ): Response<String?>



}


