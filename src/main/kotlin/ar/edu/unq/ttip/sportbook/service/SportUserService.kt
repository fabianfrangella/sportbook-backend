package ar.edu.unq.ttip.sportbook.service;

import ar.edu.unq.ttip.sportbook.controller.request.UpdateUserDataRequest
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.AdditionalInfoJpaRepository
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository;
import org.springframework.stereotype.Service;

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

    fun getAllUsers(): List<SportUser> {
        return sportUserJpaRepository.findAll()
    }
}
