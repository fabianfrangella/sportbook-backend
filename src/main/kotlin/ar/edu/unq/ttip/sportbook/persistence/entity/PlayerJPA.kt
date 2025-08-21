package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYER")
class PlayerJPA() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    lateinit var name: String

    constructor(name: String) : this() {
        this.name = name
    }
}
