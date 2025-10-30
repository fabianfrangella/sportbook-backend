package ar.edu.unq.ttip.sportbook.persistence.entity.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ADDITIONAL_USER_INFO")
class AdditionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var phoneNumber: String? = null
    var address: String? = null
    var city: String? = null
    var country: String? = null
    var gender: Gender? = null
    var languages: MutableList<String> = mutableListOf()

}

enum class Gender {
    MAN, WOMAN, NON_BINARY, PREFER_NOT_TO_SAY
}