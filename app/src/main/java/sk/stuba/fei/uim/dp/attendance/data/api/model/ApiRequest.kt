package sk.stuba.fei.uim.dp.attendance.data.api.model

data class LoginRequest(val email: String, val password: String)
data class CardRequest(val name: String, val serialNumber: String)
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val card: CardRequest
)
data class AddActivityRequest(
    val uid: Int,
    val name: String,
    val location: String,
    val time: String,
    val weeks: Int
)
data class AddParticipantRequest(
    val serialNumber: String
)
data class NameRequest(
    val name: String
)
data class UpdateActivityRequest(
    val name: String,
    val location: String,
    val time: String,
    val editAll: Boolean
)

data class ChangePasswordRequest(
    val newPassword: String
)
data class GoogleLoginRequest(
    val googleToken: String
)
data class EmailRequest(
    val email: String
)