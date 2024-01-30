package sk.stuba.fei.uim.dp.attendance.data.model

data class Activity (
    val id: Int,
    val name: String,
    val location: String,
    val date: String,
    val time: String,
    val createdBy: Number,
    val participants: List<User?>?,
    val startTime: String,
    val endTime: String
)