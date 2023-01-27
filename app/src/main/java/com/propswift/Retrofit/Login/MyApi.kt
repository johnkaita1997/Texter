package com.propswift.Retrofit.Login

import com.google.gson.GsonBuilder
import com.propswift.Shared.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface MyApi {

    companion object {

        operator fun invoke(): MyApi {
            /*progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
            sessionManager = SessionManager(this)*/

            val gson = GsonBuilder().serializeNulls().create()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()
            val retrofit = Retrofit.Builder().baseUrl(Constants.baseurl).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
            val apiCall = retrofit.create(MyApi::class.java)

            return apiCall

        }
    }

    @POST("api/v1/auth/register") suspend fun register(@Body user: User): Response<Any>
    @POST("api/v1/auth/login") suspend fun login(@Body loginbody: LoginBody): Response<SuccessLogin>


    @GET("api/v1/property/list-rented-properties") suspend fun getrentedproperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?): Response<RentedProperties>

    @GET("api/v1/property/list-owned-properties") suspend fun getownedproperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?): Response<OwnedProperties>


    @GET("api/v1/property/list-rentals") suspend fun getpaidrentAll(
        @Query("filter") filter: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<RentObject?>

    @GET("api/v1/property/list-rentals") suspend fun getunpaidrentAll(
        @Query("filter") request_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<RentObject?>



    @GET("api/v1/property/list-expenses") suspend fun getExpensesGeneralAll(
        @Query("filter") filter: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<ExpenseObject?>

    @GET("api/v1/property/list-expenses") suspend fun getExpensesIncurredAll(
        @Query("filter") request_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<ExpenseObject?>


}
