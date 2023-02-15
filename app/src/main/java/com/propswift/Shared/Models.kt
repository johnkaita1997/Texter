package com.propswift.Shared

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Gender(val gender: String?, val videoLabel: String?) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class User(
    var email: String?,
    var first_name: String?,
    var last_name: String?,
    var middle_name: String?,
    var username: String?,
    var password: String?,
    var confirm_password: String?,
)

data class LoginBody(var username: String?, var password: String?)
data class SuccessLogin(val details: Success_Login_Details)
data class Success_Login_Details(val access_token: String?, val expires_in: Int, val jwt_token: String?, val refresh_token: String?, val token_type: String?)

data class MyAuth(var authToken: String?, var jwttoken: String?)


class RentedProperties(
    val details: List<RentedDetail>?
)

data class RentedDetail(
    val area: Double?,
    val area_unit: String?,
    val created_at: String?,
    val deleted_at: Any,
    val files: List<String?>,
    val id: String?,
    val location: String?,
    val name: String?,
    val rent_amount: Any,
    val updated_at: String?
)


class OwnedProperties(
    val details: List<OwnedDetail>?
)

data class OwnedDetail(
    val area: Double?,
    val area_unit: String?,
    val created_at: String?,
    val deleted_at: Any,
    val files: List<String?>,
    val id: String?,
    val location: String?,
    val name: String?,
    val rent_amount: Any,
    val updated_at: String?
)


class RentalObject(
    val details: List<RentDetail>?
)

data class RentDetail(
    val amount: String?,
    val amount_paid: String?,
    val date_paid: String?,
    val due_date: String?,
    val id: String?,
    val `property`: Property,
    val rent_status: String?,
    val receipt: String?,
    val payment_files: MutableList<String>,
    val start_date: String?
)

data class RentFilter(
    val property_id: String?,
    val filter: String?,
    val date_from: String?,
    val date_to: String?,
)


class ExpenseObject(
    val details: List<ExpenseDetail>?
)

data class ExpenseDetail(
    val amount: String?,
    val created_at: String?,
    val date_incurred: String?,
    val receipt: String?,
    val deleted_at: Any,
    val description: String?,
    val expense_type: String?,
    val id: String?,
    val `property`: ExpenseProperty,
    val updated_at: String?
)

data class ExpenseFilter(
    val property_id: String?,
    val filter: String?,
    val date_from: String?,
    val date_to: String?,
)


data class ExpenseProperty(
    val area: Double?,
    val area_unit: String?,
    val created_at: String?,
    val deleted_at: Any,
    val files: List<String?>,
    val id: String?,
    val location: String?,
    val name: String?,
    val rent_amount: Any,
    val updated_at: String?
)


data class Property(
    val area: Double?,
    val area_unit: String?,
    val created_at: String?,
    val deleted_at: Any,
    val files: List<Any>,
    val id: String?,
    val location: String?,
    val managers: List<String?>,
    val name: String?,
    val rent_amount: Any,
    val updated_at: String?
)


data class CreateProperty(
    val name: String?,
    val location: String?,
    val area: Double?,
    val area_unit: Any,
    val rent_amount: String?,
    val is_owner: Boolean,
    val files: List<String?>
)


data class UserDetails(
    val details: DetailsUser?
)

data class DetailsUser(
    val first_name: String?,
    val last_name: String?,
    val middle_name: String?,
    val user_id: String?,
    val username: String?
)


data class GetPropertyManagers(
    val details: MutableList<GetPropertyManagerDetails_Details>
)

data class GetPropertyManagerDetails_Details(
    val first_name: String?,
    val last_name: String?,
    val middle_name: String?,
    val user_id: String?,
    val username: String?
)


data class Manager(
    val confirm_password: String?,
    val first_name: String?,
    val last_name: String?,
    val middle_name: String?,
    val password: String?,
    val property_id: String?,
    val username: String?
)

data class success(
    val details: String?
)


data class ListOfManagedProperties(
    val details: MutableList<ListOfManagedProperties_Detail?>?
)

data class ListOfManagedProperties_Detail(
    val area: Double?,
    val area_unit: String?,
    val created_at: String?,
    val deleted_at: Any,
    val files: List<Any>,
    val id: String?,
    val location: String?,
    val managers: List<String?>,
    val name: String?,
    val rent_amount: Any,
    val updated_at: String?
)


data class ToDoListTask(
    val due_date: String?,
    val title: String?
)



data class RemoveToDoId(
    val request_id: String?
)


data class GetProfileDetails(
    val details: GetProfileDetails_Details?
)

data class GetProfileDetails_Details(
    val first_name: String?,
    val last_name: String?,
    val middle_name: String?,
    val user_id: String?,
    val username: String?,
    val is_manager: Boolean
)


data class FetchExpenseObject(
    val details: MutableList<FetchExpenseObject_Detail>?
)

data class FetchExpenseObject_Detail(
    val amount: String?,
    val created_at: String?,
    val date_incurred: String?,
    val deleted_at: Any,
    val description: String?,
    val expense_type: String?,
    val files: List<String?>,
    val id: String?,
    val `property`: Property,
    val updated_at: String?
)


data class RemoveManager(
    val manager: String,
    val property_id: String
)

data class ImageUploadResult(
    val details: List<String>
)


data class ExpenseUploadObject(
    val amount: Int,
    val date_incurred: String,
    val description: String,
    val expense_type: String,
    val files: List<String>,
    val property_id: String
)


data class OtherReceiptsUploadObject(
    val amount: Int,
    val date_incurred: String,
    val description: String,
    val receipt: String,
    val files: List<String>,
    val property_id: String
)


class Total(
    val details: Double?
)

data class StringBody(
    val request_id: String
)


data class OtherReceiptCallback(
    val details: MutableList<OtherReceiptCallbackDetails>
)

data class OtherReceiptCallbackDetails(
    val amount: String,
    val created_at: String,
    val date_incurred: String,
    val property_name: String,
    val deleted_at: Any,
    val description: String,
    val files: List<String>,
    val id: String,
    val `property`: String,
    val receipt: String,
    val updated_at: String
)


data class OtherReceiptFilter(
    val property_id: String?,
    val date_from: String?,
    val date_to: String?,
)


data class RentPaymentModel(
    val amount: Int,
    val files: List<String>,
    val payment_date: String,
    val receipt: String,
    val request_id: String
)

data class RentPaidCallback(
    val details: String
)


data class ListManagedProperties(
    val details: List<ListManagedPropertiesDetail>
)

data class ListManagedPropertiesDetail(
    val area: Double,
    val area_unit: String,
    val created_at: String,
    val deleted_at: Any,
    val files: List<Any>,
    val id: String,
    val location: String,
    val managers: List<String>,
    val name: String,
    val rent_amount: Any,
    val updated_at: String
)

data class ExpenseDeleteBody(
    val request_id: String?
)
data class ExpenseDeletedCallback(
    val details: String?
)

data class DeleteOtherReceiptBody(
    val request_id: String?
)




data class GetToDoListTasks(
    val details: MutableList<GetToDoListTasks_Details>?
)

data class GetToDoListTasks_Details(
    val actor: String?,
    val created_at: String?,
    val deleted_at: Any,
    val due_date: String?,
    val id: Int,
    val title: String?,
    val updated_at: String?
)