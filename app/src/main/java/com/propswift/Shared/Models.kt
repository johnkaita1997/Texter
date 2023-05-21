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

data class LoginBody(var email: String?, var password: String?)
data class SuccessLogin(
    val access: String?,
    val refresh: String?
)

data class Success_Login_Details(
    val access: String?,
    val refresh: String?
)

data class MyAuth(var access: String?, var refresh: String?)


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

data class Success(
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


data class Photo(
    val name: String?,
    val image: String?,
)


class UserFineDetails : ArrayList<UserFineDetailsItem?>()
data class UserFineDetailsItem(
    val contact: List<Int>,
    val date_created: String,
    val date_deleted: Any,
    val date_joined: Any,
    val date_updated: String,
    val email: String,
    val first_name: Any,
    val fullname: String,
    val groups: List<Any>,
    val id: String,
    val is_active: Boolean,
    val is_agent: Boolean,
    val is_staff: Boolean,
    val is_superuser: Boolean,
    val isadmin: Boolean,
    val isagent: Boolean,
    val isparent: Boolean,
    val isstudent: Boolean,
    val last_login: Any,
    val last_name: Any,
    val phone: String,
    val school: School,
    val user_permissions: List<Any>,
    val username: String
)


class GetStudentResult : ArrayList<GetStudentResultItem>()

data class GetStudentResultItem(
    val active: Boolean,
    val activefromdate: Any,
    val calls: MutableList<Mobile>,
    val confirmpassword: String,
    val contacts: MutableList<Contact>,
    val date_created: String,
    val date_updated: String,
    val email: Any,
    val fullname: String,
    val id: Int,
    val kcpeindexnumber: Any,
    val password: String,
    val phonenumber: Any,
    val registrationnumber: Any,
    val school: School,
    val tokenbalance: Float,
    val totalnumberofcalls: Double,
    val user: String,
    val username: Any
)

data class Contact(
    val date_created: String,
    val date_updated: String,
    val email: String,
    val id: Int,
    val mobile: String,
    val mobiletwo: Any,
    val name: String,
    val relationship: String,
    val students: List<Student>,
    val useractive: String
)

data class School(
    val agents: List<String>,
    val date_created: String,
    val date_updated: String,
    val email: String,
    val id: Int,
    val location: String,
    val mobile: Mobile,
    val name: String,
    val students: List<Int>
): Serializable



data class CreateCallLog(
    val callstamp: String,
    val duration: String,
    val minutesused: Float,
    val mobilecalled: String,
    val student: Int,
    val tokensused: Float,
    val mobileused: String,
    val username: String
)

class GetConstantsResult : ArrayList<GetConstantsResultItem>()
data class GetConstantsResultItem(
    val activationamount: Int,
    val date_created: String,
    val date_updated: String,
    val id: Int,
    val minutepershilling: Double,
    val minutespertokenOrequivalentminutes: Float,
    val shillingspertokenOrequivalentshillings: Double,
    val tokennumber: Int
)


data class GetStudentDetailResult(
    val active: Boolean,
    val activefromdate: Any,
    val calls: List<Int>,
    val confirmpassword: String,
    val contacts: List<Contact>,
    val date_created: String,
    val date_updated: String,
    val email: Any,
    val fullname: String,
    val id: Int,
    val kcpeindexnumber: Any,
    val password: String,
    val phonenumber: Any,
    val registrationnumber: Any,
    val school: School,
    val tokenbalance: Double,
    val totalnumberofcalls: Double,
    val user: String,
    val username: Any
)

data class UpdateTokenBalanceObject(
    val tokenbalance: Float
)

data class UpdateMobileBody(
    val standingminutes: Float,
    val standingtoken: Float
)



class GetContactModelOfLoggedInUser : ArrayList<GetContactModelOfLoggedInUserItem>()

data class GetContactModelOfLoggedInUserItem(
    val contactuser: String,
    val date_created: String,
    val date_updated: String,
    val email: String,
    val id: Int,
    val mobile: String,
    val mobiletwo: Any,
    val name: String,
    val relationship: String,
    val students: MutableList<Student>
)


class People : Serializable {
    // your stuff
}
class Student (
    val active: Boolean,
    val activefromdate: Any,
    val calls: MutableList<Call>,
    val confirmpassword: String,
    val contacts: List<Int>,
    val date_created: String,
    val date_updated: String,
    val email: Any,
    val fullname: String,
    val id: Int,
    val kcpeindexnumber: Any,
    val password: String,
    val phonenumber: Any,
    val registrationnumber: Any,
    val school: School,
    val tokenbalance: Double,
    val totalnumberofcalls: Double,
    val user: String,
    val username: Any
): Serializable

data class Call(
    val callstamp: String,
    val date_created: String,
    val date_updated: String,
    val duration: String,
    val id: Int,
    val minutesused: Double,
    val mobilecalled: String,
    val mobileused: String,
    val student: Int,
    val tokensused: Double
)


data class Mobile(
    val date_created: String,
    val date_updated: String,
    val id: Int,
    val mobile: String,
    val school: Int,
    val standingminutes: Double,
    val standingtoken: Double
): Serializable


data class CheckoutBody(
    val amount: Int,
    val mobile: String,
    val purpose: String,
    val studentid: String,
    val timestamp: String,
)


class GetPaymentStatusResponse : ArrayList<GetPaymentStatusResponseItem>()

data class GetPaymentStatusResponseItem(
    val amount: Double,
    val checkoutid: String,
    val date_created: String,
    val date_updated: String,
    val description: Any,
    val id: Int,
    val mobile: String,
    val purpose: String,
    val receiptnumber: Any,
    val reference: Any,
    val status: String,
    val student: Int,
    val studentid: String,
    val timestamp: String,
    val user: String
)



class GetUserWithMobileResult : ArrayList<GetUserWithMobileResultItem>()

data class GetUserWithMobileResultItem(
    val contact: MutableList<Int>,
    val date_created: String,
    val date_deleted: Any,
    val date_joined: Any,
    val date_updated: String,
    val email: String,
    val first_name: Any,
    val fullname: String,
    val groups: MutableList<Any>,
    val id: String,
    val is_active: Boolean,
    val is_agent: Boolean,
    val is_staff: Boolean,
    val is_superuser: Boolean,
    val isadmin: Boolean,
    val isagent: Boolean,
    val isparent: Boolean,
    val isstudent: Boolean,
    val last_login: Any,
    val last_name: Any,
    val phone: String,
    val school: Int,
    val user_permissions: MutableList<Any>,
    val username: String
)


class GetUserListResult : ArrayList<GetUserListResultItem>()
data class GetUserListResultItem(
    val contact: List<Int>,
    val date_created: String,
    val date_deleted: Any,
    val date_joined: Any,
    val date_updated: String,
    val email: String,
    val first_name: Any,
    val fullname: String,
    val groups: List<Any>,
    val id: String,
    val is_active: Boolean,
    val is_agent: Boolean,
    val is_staff: Boolean,
    val is_superuser: Boolean,
    val isadmin: Boolean,
    val isagent: Boolean,
    val isparent: Boolean,
    val isstudent: Boolean,
    val last_login: Any,
    val last_name: Any,
    val phone: String,
    val school: Int,
    val user_permissions: List<Any>,
    val username: String
)