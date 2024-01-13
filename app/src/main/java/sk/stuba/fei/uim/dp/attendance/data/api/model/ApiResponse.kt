package sk.stuba.fei.uim.dp.attendance.data.api.model

data class AuthResponse(val accessToken: String)
data class GetActivityResponse(
    val id: Number,
    val name: String,
    val location: String,
    val time: String,
    val createdBy: Number,
    val startTime: String,
    val endTime: String
)