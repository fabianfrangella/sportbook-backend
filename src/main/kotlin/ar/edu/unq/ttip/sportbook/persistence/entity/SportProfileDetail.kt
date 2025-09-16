package ar.edu.unq.ttip.sportbook.persistence.entity

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class SportProfileDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
