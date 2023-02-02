package com.propswift.Retrofit

import com.google.gson.GsonBuilder
import com.propswift.Shared.*
import okhttp3.MultipartBody
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
            val okHttpClient =
                OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()
            val retrofit = Retrofit.Builder().baseUrl(Constants.baseurl)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
            val apiCall = retrofit.create(MyApi::class.java)

            return apiCall

        }
    }

    @POST("api/v1/auth/register")
    suspend fun register(@Body user: User): Response<Any>

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginbody: LoginBody): Response<SuccessLogin>


    @GET("api/v1/property/list-rented-properties")
    suspend fun getrentedproperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<RentedProperties>

    @GET("api/v1/property/list-owned-properties")
    suspend fun getownedproperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<OwnedProperties>



    @GET("api/v1/property/list-rentals")
    suspend fun getRentals(
        @Query("filter") filter: String?,
        @Query("property_id") property_id: String?,
        @Query("date_from") date_from: String?,
        @Query("date_to") date_to: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<RentalObject?>


    @GET("api/v1/property/list-expenses")
    suspend fun getExpenses(
        @Query("filter") filter: String?,
        @Query("property_id") property_id: String?,
        @Query("date_from") date_from: String?,
        @Query("date_to") date_to: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<FetchExpenseObject?>


    @POST("api/v1/property/add-property")
    suspend fun createProperty(
        @Body property: CreateProperty?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<CreateProperty?>


    @GET("api/v1/users/user-details")
    suspend fun getUserProfileDetails(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<GetProfileDetails?>


    @GET("api/v1/users/get-user-details")
    suspend fun getUserDetails(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Query("user_id") user_id: String?,
    ): Response<UserDetails?>








    @GET("api/v1/owners/list-managers")
    suspend fun getPropertyManagers(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Query("property_id") property_id: String?,
    ): Response<GetPropertyManagers>


    @POST("api/v1/owners/add-manager")
    suspend fun addManager(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body user: Manager
    ): Response<success>


    @POST("api/v1/owners/remove-manager")
    suspend fun removeManager(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body managerpropertyid: RemoveManager?,
    ): Response<success>


    @GET("api/v1/property/list-managed-properties")
    suspend fun getManagedPropertiesForManager(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<MutableList<ListOfManagedProperties_Detail>>


    @POST("api/v1/tasks")
    suspend fun addToDoList(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body toDoListTask: ToDoListTask
    ): Response<success>


    @GET("api/v1/tasks")
    suspend fun getToDoList(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<GetToDoListTasks>


    @POST("api/v1/tasks/remove-task")
    suspend fun removeToDoList(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body todolistId: RemoveToDoId,
    ): Response<success>


    @Multipart
    @POST("api/v1/property/upload-file")
    suspend fun uploadImage(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Part mylist: MutableList<MultipartBody.Part>
    ): Response<ImageUploadResult?>


    @POST("api/v1/property/add-expense")
    suspend fun addExpense(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body expensePostObject: ExpenseUploadObject,
    ): Response<success>


    @GET("api/v1/property/get-total-expenses")
    suspend fun getTotalSpent(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>

    @GET("api/v1/property/item-counter?filter=expense")
    suspend fun getTotalNumberofReceipts(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>


}



