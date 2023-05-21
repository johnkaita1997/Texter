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

    @POST("api/v1/auth/register")
    suspend fun register(@Body user: User): Response<Any>



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
    ): Response<Success?>


    @GET("api/v1/users/user-details")
    suspend fun getUserProfileDetails(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<GetProfileDetails?>




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
    ): Response<Success>


    @POST("api/v1/owners/remove-manager")
    suspend fun removeManager(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body managerpropertyid: RemoveManager?,
    ): Response<Success>


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
    ): Response<Success>


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
    ): Response<Success>


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
    ): Response<Success>


    @GET("api/v1/property/get-total-expenses")
    suspend fun getTotalSpent(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>

    @GET("api/v1/property/get-total-expenses")
    suspend fun getTotalExpensesOnProperty(
        @Query("property_id") property_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>

    @GET("api/v1/property/item-counter?filter=expense")
    suspend fun getTotalNumberofReceipts(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>


    @POST("api/v1/property/remove-property")
    suspend fun deleteProperty(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body propoertyid: StringBody,
    ): Response<Success>


    @POST("api/v1/property/add-other-receipt")
    suspend fun addOtherReceipt(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body otherReceiptsUploadObject: OtherReceiptsUploadObject,
    ): Response<Success>


    @GET("api/v1/property/list-other-receipts")
    suspend fun getOtherReceipts(
        @Query("property_id") property_id: String?,
        @Query("date_from") date_from: String?,
        @Query("date_to") date_to: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<OtherReceiptCallback?>


    @POST("api/v1/property/add-rent-payment")
    suspend fun addRentPayment(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body rentpaymentbody: RentPaymentModel,
    ): Response<RentPaidCallback>


    @GET("api/v1/property/list-managed-properties")
    suspend fun getManagedProperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<ListManagedProperties?>


    @GET("api/v1/property/item-counter?filter=expense")
    suspend fun getNumberofReceiptsForHouse(
        @Query("property_id") property_id: String?,
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<Total>


    @GET("api/v1/property/list-my-properties")
    suspend fun getAllProperties(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
    ): Response<ListManagedProperties?>


    @POST("api/v1/property/delete-expense")
    suspend fun deleteExpense(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body request_id: ExpenseDeleteBody,
    ): Response<ExpenseDeletedCallback>


    @POST("api/v1/property/delete-other-receipt")
    suspend fun deleteOtherReceipt(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?,
        @Body request_id: DeleteOtherReceiptBody
    ): Response<ExpenseDeletedCallback>


    @GET("api/v1/tasks/list-due-tasks")
    suspend fun getToDoListDueToday(
        @Header("Authorization") authorization: String?,
        @Header("JWTAUTH") jwtauth: String?
    ): Response<GetToDoListTasks>















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


    @GET("api/v1/contacts/list")
    suspend fun getContactModelofLoggedInUser(
        @Query("contactuser") contactuser: String
    ): Response<GetContactModelOfLoggedInUser?>


    @POST("api/v1/payments/checkout")
    suspend fun checkout(
        @Header("Authorization") authorization: String?,
        @Body checkoutBody : CheckoutBody
    ): Response<Success?>


    @GET("api/v1/payments/list")
    suspend fun checkPaymentStatus(
        @Header("Authorization") authorization: String?,
        @Query("timestamp") timestamp : String
    ): Response<GetPaymentStatusResponse?>


    @GET("api/v1/users/list")
    suspend fun getUserWithNumber(
        @Header("Authorization") authorization: String?,
        @Query("mobile") mobile : String
    ): Response<UserFineDetails?>


    @GET("api/v1/users/list")
    suspend fun getUserList(
        @Header("Authorization") authorization: String?,
        @Query("mobile") mobile: String
    ): Response<GetUserListResult?>

}



