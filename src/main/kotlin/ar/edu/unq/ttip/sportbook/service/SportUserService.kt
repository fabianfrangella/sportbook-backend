package ar.edu.unq.ttip.sportbook.service;

import ar.edu.unq.ttip.sportbook.controller.request.UpdateUserDataRequest
import ar.edu.unq.ttip.sportbook.exception.BusinessException
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.AdditionalInfoJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional

@Service
class SportUserService(val sportUserJpaRepository: SportUserJpaRepository, val additionalInfoJpaRepository: AdditionalInfoJpaRepository) {

    fun updateSportUser(userId: Long, userData: UpdateUserDataRequest): SportUser {
        val existentUser = sportUserJpaRepository.findById(userId).orElseThrow({ NotFoundException("User not found") })
        existentUser.updateWith(userData)
        if (existentUser.additionalInfo != null) {
            additionalInfoJpaRepository.save(existentUser.additionalInfo!!)
        }
        return sportUserJpaRepository.save(existentUser)
    }

    @Transactional(readOnly = true)
    fun searchUsersByUsername(query: String): List<SportUser> {
        if (query.trim().length < 2) {
            throw BusinessException("El término de búsqueda debe tener al menos 2 caracteres")
        }

        return sportUserJpaRepository.findByUsernameContainingIgnoreCase(query.trim())
            .take(10)
    }
}
