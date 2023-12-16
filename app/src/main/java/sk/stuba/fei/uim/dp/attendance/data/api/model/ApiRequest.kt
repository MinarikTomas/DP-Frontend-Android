package sk.stuba.fei.uim.dp.attendance.data.api.model

data class LoginRequest(val email: String, val password: String)
data class CardRequest(val name: String, val serialNumber: String)
data class SignupRequest(val name: String, val email: String, val password: String, val card: CardRequest)