package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "SPORT_USER")
class SportUserJPA() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var password: String? = null
    var username: String? = null
    var email: String? = null
    var name: String? = null
    var lastName: String? = null
    var dateOfBirth: LocalDate? = null

    constructor(password: String,
                username: String,
                email: String,
                name: String,
                lastName: String,
                dateOfBirth: LocalDate) : this() {
        this.password = password
        this.username = username
        this.email = email
        this.name = name
        this.lastName = lastName
        this.dateOfBirth = dateOfBirth
    }
}