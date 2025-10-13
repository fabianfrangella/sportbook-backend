package ar.edu.unq.ttip.sportbook.persistence.repository

import ar.edu.unq.ttip.sportbook.persistence.entity.user.ProfilePicture
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfilePictureRepository : JpaRepository<ProfilePicture, Long> {
    fun findByUser(user: SportUser): ProfilePicture?
}
