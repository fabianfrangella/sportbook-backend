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
    lateinit var password: String
    lateinit var username: String
    lateinit var email: String
    lateinit var name: String
    lateinit var lastName: String
    lateinit var dateOfBirth: LocalDate

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