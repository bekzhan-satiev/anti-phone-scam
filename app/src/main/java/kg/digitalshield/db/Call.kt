package kg.digitalshield.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Call(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phoneNumber: String,
    val callDate: Date,
    val callStatus: CallStatus = CallStatus.SAFE,
    val suspiciousPhrases: String,
)