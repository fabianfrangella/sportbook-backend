package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.controller.request.UpdateFootballProfileRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdatePaddleProfileRequest
import ar.edu.unq.ttip.sportbook.controller.request.UpdateVolleyProfileRequest
import ar.edu.unq.ttip.sportbook.persistence.entity.*
import ar.edu.unq.ttip.sportbook.persistence.repository.SportProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SportProfileService(
    private val sportProfileRepository: SportProfileRepository
) {
    @Transactional
    fun updateFootballProfile(user: SportUser, req: UpdateFootballProfileRequest): SportProfile {
        val detail = FootballProfileDetail(
            positions = req.positions ?: mutableListOf(),
            favoritePosition = req.favoritePosition,
            ability = req.ability,
            playsOften = req.playsOften
        )

        val profile = sportProfileRepository.findByUserAndSport(user, Sport.FOOTBALL)
            ?.apply { this.details = detail }
            ?: SportProfile(user = user, sport = Sport.FOOTBALL, details = detail)

        if (!user.profiles.contains(profile)) {
            user.profiles.add(profile)
        }

        return sportProfileRepository.save(profile)
    }

    @Transactional
    fun updateVolleyProfile(user: SportUser, req: UpdateVolleyProfileRequest): SportProfile {
        val detail = VolleyProfileDetail(
            positions = req.positions ?: mutableListOf(),
            favoritePosition = req.favoritePosition,
            ability = req.ability,
            playsOften = req.playsOften,
            blockHeight = req.blockHeight,
            rolePreference = req.rolePreference
        )

        val profile = sportProfileRepository.findByUserAndSport(user, Sport.VOLLEY)
            ?.apply { this.details = detail }
            ?: SportProfile(user = user, sport = Sport.VOLLEY, details = detail)

        if (!user.profiles.contains(profile)) {
            user.profiles.add(profile)
        }

        return sportProfileRepository.save(profile)
    }

    @Transactional
    fun updatePaddleProfile(user: SportUser, req: UpdatePaddleProfileRequest): SportProfile {
        val detail = PaddleProfileDetail(
            preferredSide = req.preferredSide,
            ability = req.ability,
            playsOften = req.playsOften,
            playStyle = req.playStyle,
            playedTournaments = req.playedTournaments
        )

        val profile = sportProfileRepository.findByUserAndSport(user, Sport.PADDLE)
            ?.apply { this.details = detail }
            ?: SportProfile(user = user, sport = Sport.PADDLE, details = detail)

        if (!user.profiles.contains(profile)) {
            user.profiles.add(profile)
        }

        return sportProfileRepository.save(profile)
    }

    fun getProfiles(user: SportUser): List<SportProfile> {
        return sportProfileRepository.findAllByUser(user)
    }
}
