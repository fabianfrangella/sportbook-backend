package ar.edu.unq.ttip.sportbook.persistence.entity.user

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class SportProfileDetail() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var playsOften: Boolean = false
    var ability: Int? = null

    constructor(playsOften: Boolean, ability: Int?) : this() {
        this.playsOften = playsOften
        this.ability = ability
    }
}
