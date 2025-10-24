package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.user.ProfilePicture
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.ProfilePictureRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import jakarta.transaction.Transactional

@Service
class ProfilePictureService(
    private val profilePictureRepository: ProfilePictureRepository
) {
    @Transactional
    fun uploadProfilePicture(file: MultipartFile, user: SportUser) {
        val existingPicture = profilePictureRepository.findByUser(user)

        if (existingPicture != null) {
            existingPicture.binaryData = file.bytes
            profilePictureRepository.save(existingPicture)
        } else {
            val profilePicture = ProfilePicture(file, user)
            profilePictureRepository.save(profilePicture)
        }
    }

    @Transactional
    fun getProfilePicture(user: SportUser): ByteArray? {
        return profilePictureRepository.findByUser(user)?.binaryData
    }
}
