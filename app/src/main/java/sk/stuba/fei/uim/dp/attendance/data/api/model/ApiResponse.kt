package sk.stuba.fei.uim.dp.attendance.data.api.model

data class AuthResponse(val accessToken: String, val refreshToken: String)
data class ActivityResponse(
    val id: Int,
    val name: String,
    val location: String,
    val time: String,
    val createdBy: Int,
    val startTime: String,
    val endTime: String
)
data class UserResponse(
    val id: Int,
    val fullName: String,
    val email: String
)
data class ActivityWithParticipantsResponse(
    val id: Int,
    val name: String,
    val location: String,
    val time: String,
    val createdBy: Int,
    val participants: List<UserResponse?>,
    val startTime: String,
    val endTime: String
)
data class CardResponse(
    val id: Int,
    val name: String
)