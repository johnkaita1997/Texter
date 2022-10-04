package paita.stream_app_final.Tafa.Retrofit.Login

import com.google.gson.GsonBuilder
import paita.stream_app_final.AppConstants.Constants
import paita.stream_app_final.Tafa.Adapters.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import paita.stream_app_final.Tafa.Adapters.UserProfileDetails
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

    @POST("api/v1/users/registration") suspend fun register(@Body user: User): Response<Any>

    @POST("api/v1/users/auth/login") suspend fun login(@Body loginbody: LoginBody): Response<SuccessLogin>

    @GET("api/v1/school/subject") suspend fun fetchSubjects(@Query("form_topic") auth: String?): Response<RetroSubjects>

    @GET("api/v1/school/unit") suspend fun fetchlistofUnits(@Query("form") form: String?, @Query("subject") subject: String): Response<MyUnit?>

    @GET("api/v1/school/video") suspend fun fetchvideosperunitname(@Query("unit") unit: String?): Response<Videosperunitname?>

    @GET("api/v1/payments/subscription/check-status") suspend fun checkunitsubscriptionStatus(
        @Query("request_id") request_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAuth") jwtauth: String?,
    ): Response<Any>

    @POST("https://dev.vdocipher.com/api/videos/{videoid}/otp") suspend fun gettokens(@Header("Authorization") apisecret: String, @Path("videoid") videoid: String): Response<VidToken>

    @GET("api/v1/payments/unit-amount") suspend fun getunitprices(
        @Query("unit") request_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAuth") jwtauth: String?,
    ): Response<UnitPricesObject?>

    @GET("api/v1/users/account/profile") suspend fun getusername(@Query("user_id") user_id: String): Response<UserDetails>

    @GET("api/v1/users/registration/list_counties") suspend fun getcounties(): Response<MyCounty>

    @GET("api/v1/users/registration/list_codes") suspend fun getagents(): Response<MyCounty>

    @GET("api/v1/school/form") suspend fun getForms(): Response<MyForms>

    @POST("api/v1/payments/subscription/check-form-subscription") suspend fun checkFormSubscription(@Body myform: FormActive?,
                                                                                                    @Header("Authorization") authorization: String?,
                                                                                                    @Header("JWTAuth") jwtauth: String?): Response<Any>

    @POST("api/v1/payments/subscription/check-subject-subscription") suspend fun checkSubjectSubscription(@Body myform: SubjectActive?,
                                                                                                          @Header("Authorization") authorization: String?,
                                                                                                          @Header("JWTAuth") jwtauth: String?): Response<Any>

    @GET("api/v1/payments/form-amounts") suspend fun getFormAmounts(
        @Query("form") form: String?,
    ): Response<FormAmount>

    @POST("api/v1/payments/checkout") suspend fun checkoutform(
        @Body checkoutformbody: CheckoutForm?,
    ): Response<InvoiceId>

    @GET("api/v1/payments/subject-amounts") suspend fun getsubjectplanlist(@Query("form") form: String?, @Query("subject") subject: String): Response<SubjectPlanList>

    @POST("api/v1/payments/checkout") suspend fun checkoutsubject(
        @Body checkoutformbody: CheckOutSubject?,
    ): Response<InvoiceId>

    @POST("api/v1/payments/checkout") suspend fun checkoutUnit(
        @Body checkoutformbody: CheckOutUnit?,
    ): Response<InvoiceId>

    @POST("api/v1/payments/checkout") suspend fun checkoutTopic(
        @Body checkouttopicBody: CheckOutTopic?,
    ): Response<InvoiceId>


    @GET("api/v1/school/video/list-active-form-videos") suspend fun getYourVideos(
        @Query("form") formid: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<YourVideos>


    @POST("api/v1/mfa/otp/verify") suspend fun verifyOtp(
        @Body otpBody: VerifyOtp?,
    ): Response<Any>


    @GET("api/v1/school/topic") suspend fun listoftopics(
        @Query("form") form: String?,
        @Query("subject") subject: String?,
    ): Response<Topics>

    @GET("api/v1/payments/topic-amounts") suspend fun topicamount(
        @Query("topic") form: String?,
    ): Response<TopicAmounts>

    @POST("api/v1/payments/subscription/check-topic-subscription") suspend fun checkTopicSubscription(
        @Body topic: MutableMap<String, String>,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<Any>

    @GET("api/v1/payments/invoice/check-invoice-status") suspend fun checkInvoiceStatus(
        @Query("request_id") invoiceid: String?,
    ): Response<PaymentCallback>


    /*@POST("posts") suspend fun createPost(@Body post: User): Response<User>

    @FormUrlEncoded  //Passes userId=23&title=New%20Title&body=%20Text
    @POST("posts") suspend fun createPostFormUrlEncodedType(
        @Field("userid") userId: Int,
        @Field("title") title: String,
        @Field("body") body: String
    ): Response<User>

    @Headers("Static-Header: 123") @PUT("posts/{id}") suspend fun putPost(@HeaderMap headers: Map<String, String>, @Path("id") id: Int, @Body post: User): Response<POST>
    @PUT("posts/{id}") suspend fun patchPost(@Header("Dynamic-Header") header: String, @Path("id") id: Int, @Body post: User): Response<POST>
    @DELETE("posts/{id}") suspend fun deletePost(@Path("id") id: Int): Response<Void>*/

    @GET("api/v1/users/account/profile") suspend fun getUserDetails( @Header("Authorization") authorization: String?, @Header("JWTAUTH") jwtauth: String?,): Response<UserProfileDetails>
    @GET("api/v1/users/user-transactions") suspend fun getTransactions( @Header("Authorization") authorization: String?, @Header("JWTAUTH") jwtauth: String?,): Response<Transactions>
    @GET("api/v1/video/get-free-videos") suspend fun getFreeVideos(@Query("subject_id") subject_Id: String): Response<FreeVideos>

}