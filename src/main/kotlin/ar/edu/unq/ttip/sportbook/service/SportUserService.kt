package ar.edu.unq.ttip.sportbook.service;

import ar.edu.unq.ttip.sportbook.controller.request.UpdateUserDataRequest
import ar.edu.unq.ttip.sportbook.exception.NotFoundException
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportUserJpaRepository;
import org.springframework.stereotype.Service;

@Service
class SportUserService(val sportUserJpaRepository: SportUserJpaRepository) {

    fun updateSportUser(userId: Long, userData: UpdateUserDataRequest): SportUser {
        val existentUser = sportUserJpaRepository.findById(userId).orElseThrow({ NotFoundException("User not found") })
        existentUser.updateWith(userData)
        return sportUserJpaRepository.save(existentUser)
    }
}
