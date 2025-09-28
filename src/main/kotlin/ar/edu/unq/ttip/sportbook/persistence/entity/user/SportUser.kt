package ar.edu.unq.ttip.sportbook.persistence.entity.user

import ar.edu.unq.ttip.sportbook.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "SPORT_USER")
class SportUser() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null
    final var username: String? = null
    final var email: String? = null
    final var name: String? = null
    final var lastName: String? = null

    @JsonFormat(pattern = "yyyy-MM-dd")
    var dateOfBirth: LocalDate? = null

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    var profiles: MutableList<SportProfile> = mutableListOf()

    constructor(
        password: String,
        username: String,
        email: String,
        name: String,
        lastName: String,
        dateOfBirth: LocalDate
    ) : this() {
        this.password = password
        this.username = username
        this.email = email
        this.name = name
        this.lastName = lastName
        this.dateOfBirth = dateOfBirth
    }
}
