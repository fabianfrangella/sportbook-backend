package ar.edu.unq.ttip.sportbook.controller.request

import ar.edu.unq.ttip.sportbook.persistence.entity.user.AdditionalInfo
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Gender
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Role
import java.time.LocalDate

class UpdateUserDataRequest(
    var username: String?,
    var email: String?,
    var name: String?,
    var lastName: String?,
    var dateOfBirth: LocalDate?,
    var role: Role?,
    var phoneNumber: String?,
    var address: String?,
    var city: String?,
    var country: String?,
    var gender: Gender?,
    var languages: MutableList<String> = mutableListOf()
)