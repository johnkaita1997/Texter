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


    @POST("api/v1/users/login")
    suspend fun login(@Body loginbody: LoginBody): Response<SuccessLogin>

    @GET("api/v1/users/userdetails")
    suspend fun getuserfinedetails(@Header("Authorization") authorization: String?): Response<UserFineDetails?>

    @GET("api/v1/students/list")
    suspend fun getstudentlist(
        @Header("Authorization") authorization: String?,
        @Query("user") userid: String?,
    ): Response<GetStudentResult?>

    @POST("api/v1/calls/create")
    suspend fun createCallLog(
        @Header("Authorization") authorization: String?,
        @Body createCallLog: CreateCallLog
    ): Response<Success?>


    @GET("api/v1/constants/list")
    suspend fun getConstants(
        @Query("school") school: String
    ): Response<GetConstantsResult?>


    @PATCH("api/v1/students/{id}")
    suspend fun studentDetail(
        @Path("id") id: Int,
        @Body updateTokenBalanceObject: UpdateTokenBalanceObject
    ): Response<Success?>


    @PUT("api/v1/mobiles/{id}")
    suspend fun getMobiles(
        @Path("id") id: Int,
        @Body updateMobileBody: UpdateMobileBody
    ): Response<Success?>


    @GET("api/v1/users/list")
    suspend fun getUserWithNumber(
        @Header("Authorization") authorization: String?,
        @Query("mobile") mobile: String
    ): Response<UserFineDetails?>


    @GET("api/v1/schools/{id}")
    suspend fun getSchoolDetails(
        @Path("id") id: String,
    ): Response<SchoolTwo?>

    @GET("api/v1/schools/standingtoken/{id}")
    suspend fun getStandingTokenForSchool(
        @Path("id") id: String,
    ): Response<Double?>

    @GET("api/v1/mobiles/id/{id}")
    suspend fun getMobileId(
        @Path("id") id: String,
    ): Response<String?>

    @GET("api/v1/constants/global/list")
    suspend fun getGlobalSettings(): Response<GetGlobalSettings?>

    @GET("api/v1/mobiles/balance/{id}")
    suspend fun getDeviceBalance(
        @Path("id") id: String,
    ): Response<GetDeviceBalance?>

    @GET("api/v1/mobiles/{id}")
    suspend fun getMobile(
        @Path("id") id: String,
    ): Response<GetMobile?>


}



