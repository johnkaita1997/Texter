package com.propswift.Shared

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity data class Gender(val gender: String, val videoLabel: String) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class User(
    var email: String,
    var first_name: String,
    var last_name: String,
    var middle_name: String,
    var username: String,
    var password: String,
    var confirm_password: String,
)

data class LoginBody(var username: String, var password: String)
data class SuccessLogin(val details: Success_Login_Details)
data class Success_Login_Details(val access_token: String, val expires_in: Int, val jwt_token: String, val refresh_token: String, val token_type: String)

data class MyAuth(var authToken: String, var jwttoken: String)


class RentedProperties(
    val details: List<RentedDetail>?
)

data class RentedDetail(
    val area: Double,
    val area_unit: String,
    val created_at: String,
    val deleted_at: Any,
    val files: List<String>,
    val id: String,
    val location: String,
    val name: String,
    val rent_amount: Any,
    val updated_at: String
)


class OwnedProperties(
    val details: List<OwnedDetail>?
)
data class OwnedDetail(
    val area: Double,
    val area_unit: String,
    val created_at: String,
    val deleted_at: Any,
    val files: List<String>,
    val id: String,
    val location: String,
    val name: String,
    val rent_amount: Any,
    val updated_at: String
)



class RentObject(
    val details: List<RentDetail>?
)
data class RentDetail(
    val amount: String,
    val amount_paid: String,
    val date_paid: String,
    val due_date: String,
    val id: String,
    val `property`: Property,
    val rent_status: String,
    val start_date: String
)


class ExpenseObject(
    val details: List<ExpenseDetail>?
)
data class ExpenseDetail(
    val amount: String,
    val created_at: String,
    val date_incurred: String,
    val deleted_at: Any,
    val description: String,
    val expense_type: String,
    val id: String,
    val `property`: ExpenseProperty,
    val updated_at: String
)
data class ExpenseProperty(
    val area: Double,
    val area_unit: String,
    val created_at: String,
    val deleted_at: Any,
    val files: List<String>,
    val id: String,
    val location: String,
    val name: String,
    val rent_amount: Any,
    val updated_at: String
)