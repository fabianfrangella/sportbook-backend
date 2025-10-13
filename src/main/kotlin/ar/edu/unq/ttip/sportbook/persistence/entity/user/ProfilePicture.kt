package ar.edu.unq.ttip.sportbook.persistence.entity.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.web.multipart.MultipartFile

@Entity
@Table(name = "PROFILE_PICTURE")
class ProfilePicture() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0

    @Lob
    lateinit var binaryData: ByteArray

    @JoinColumn(name = "user_id")
    @OneToOne(targetEntity = SportUser::class)
    var user: SportUser? = null

    constructor(file: MultipartFile, user: SportUser) : this() {
        this.binaryData = file.bytes
        this.user = user
    }
}