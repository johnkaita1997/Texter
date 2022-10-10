package paita.stream_app_final.Tafa.Adapters

import com.google.android.gms.common.Feature
import com.google.gson.annotations.SerializedName

data class User(var email: String,
                var first_name: String,
                var last_name: String,
                var phone: String,
                var password: String,
                var confirm_password: String,
                var county: String,
                var code: String,
                var school: String)

data class theResponse(var details: LoginResponse)

data class LoginResponse(@SerializedName("status_code") var statusCode: Int,
                         @SerializedName("access_token") var authToken: String,
                         @SerializedName("refreshtoken") var refreshtoken: String,
                         @SerializedName("jwt_token") var jwt_token: String,
                         @SerializedName("expires_in") var expires_in: String)

data class LoginBody(var email: String, var password: String)


data class SuccessLogin(val details: Success_Login_Details)


data class Success_Login_Details(val access_token: String, val expires_in: Int, val jwt_token: String, val refresh_token: String, val token_type: String)


class RetroSubjects : ArrayList<RetroSubjectsItem>()
data class RetroSubjectsItem(val description: String, val id: String, val name: String, val thumbnail: String, val color_codes: String, val background: String?)


class MyUnit : ArrayList<Detail>()


data class Detail(val form: Form?, val id: String?, val name: String?, val subject: SubUnits?)

data class Form(val id: String, val name: String)

data class SubUnits(val description: String, val id: String, val name: String)


data class Videosperunitname(@SerializedName("details") val thedetails: List<Thedetail?>)
data class Thedetail(val id: String, val index: Int, val label: String, val unit: Unit, val videoid: String)


data class VidToken(val otp: String?, val playbackInfo: String?)


data class UnitSubscriptionStatus(val details: String)


class UnitPrices : ArrayList<UnitPricesItem>()
data class UnitPricesItem(val amount: String, val date_created: String, val date_deleted: Any, val date_updated: String, val id: String, val period: String, val period_type: String, val unit: Unit)

data class Unit(val form: String, val id: String, val name: String, val subject: String)

data class Subject(val description: String?, val id: String?, val name: String, val thumbnail: String?, val color_codes: String?, val background: String?)


data class PriceControl(
    val amount: String,
    val period: String,
)


data class UserDetails(@SerializedName("details") val details: UserDetailed)

data class UserDetailed(val account_status: String, val id: String, val name: String, val username: String)


data class MyCounty(val details: List<MyDetail>)

data class MyDetail(val id: String, val name: String, val code: String?, val users: String?)


class MyForms : ArrayList<MyFormsItem>()
data class MyFormsItem(val id: String, val name: String)
data class MyAuth(var authToken: String, var jwttoken: String)


data class FormSub(var form: String, var subject: String)

data class CheckoutForm(val amount: String, val phone_number: String, val form: String, val user: String)


class FormAmount : ArrayList<FormAmountItem>()
data class FormAmountItem(val amount: String, val form: String, val period: String)

data class FormActive(val form: String)

data class SubjectActive(val form: String, val subject: String)

class SubjectPlanList : ArrayList<SubjectPlanListItem>()
data class SubjectPlanListItem(val amount: String, val form: String, val period: String, val subject: String)

data class CheckOutSubject(val amount: Int, val phone_number: String, val subject: String, val form: String, val user: String)

data class CheckOutUnit(val amount: Int, val phone_number: String, val unit: String, val user: String)

data class CheckOutTopic(val amount: Int, val phone_number: String, val topic: String, val user: String)


class UnitPricesObject : ArrayList<UnitPricesObjectItem>()
data class UnitPricesObjectItem(val amount: String, val period: String, val unit: TheUnit)
data class TheUnit(val form: Form, val id: String, val name: String, val subject: Subject)

class Topics : ArrayList<TopicsItem>()
data class TopicsItem(val amount: Double, val form: Form, val id: String, val name: String, val subject: Subject, val subtopics: List<Subtopic>)
data class Subtopic(val amount: Double, val id: String, val name: String)

class TopicAmounts : ArrayList<TopicAmountsItem?>()
data class TopicAmountsItem(val amount: String, val period: String, val topic: String)

class InvoiceId(val details: String)

data class PaymentCallback(val details: callBackDetails)
data class callBackDetails(val invoice: String, val status: String)


class YourVideos(val details: ArrayList<YoursDetail>)
data class YoursDetail(val id: String, val name: String, val topic: YourTopic, val videos: List<YourVideo>)

data class VerifyOtp(val otp: String, val send_to: String)

data class YourVideo(val id: String, val index: Int, val label: String, val unit: String, val videoid: String)
data class YourTopic(val amount: Double, val form: Form, val id: String, val name: String, val subject: Subject, val subtopics: List<Subtopic>)


class UserProfileDetails(val details: Details_UserProfile?)
data class Details_UserProfile(val account_status: String, val county: String, val date_created: String, val id: String, val name: String, val phone: String, val school: String, val username: String)

data class Transactions(val details: List<Detail_Transaction>?)
data class Detail_Transaction(val id: String, val status: String, val transaction_date: String, val units: List<String>)


data class FreeVideos(val details: List<Detail_FreeVideos>?)
data class Detail_FreeVideos(val id: String, val label: String, val thumbnail: String, val videoid: String)


class Polygon(val features: List<Feature>?, val type: String?)

data class Feature(val geometry: Geometry, val properties: Properties, val type: String)

data class Geometry(val coordinates: List<List<List<Double>>>, val type: String)

class Properties

class TrendingVideos(val details: List<TrendingVideoDetail>?)
class TrendingVideoDetail(val thumbnail: String, val videoid: String)


class PaidVideos(
    val count: Int?,
    val next: String?,
    val previous: Any?,
    val results: List<PaidVideoResult>?
)
data class PaidVideoResult(
    val label: String,
    val videoid: String
)










