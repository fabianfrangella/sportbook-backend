package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "PLAYER")
class PlayerJPA() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    lateinit var name: String
    @OneToOne(cascade = [CascadeType.ALL])
    lateinit var user: SportUserJPA

    constructor(name: String, user: SportUserJPA) : this() {
        this.name = name
        this.user = user
    }
}
