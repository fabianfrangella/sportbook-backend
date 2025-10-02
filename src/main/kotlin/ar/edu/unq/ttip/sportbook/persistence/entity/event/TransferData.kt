package ar.edu.unq.ttip.sportbook.persistence.entity.event

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "TRANSFER_DATA")
class TransferData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    var cbu: String? = null
    var alias: String? = null
}