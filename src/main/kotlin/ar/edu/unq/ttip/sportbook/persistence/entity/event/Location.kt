package ar.edu.unq.ttip.sportbook.persistence.entity.event


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "LOCATION")
class Location() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    lateinit var x: String
    lateinit var y: String
    lateinit var placeName: String
}