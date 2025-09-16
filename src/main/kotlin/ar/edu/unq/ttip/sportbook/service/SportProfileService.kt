package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.dto.*
import ar.edu.unq.ttip.sportbook.persistence.entity.*
import ar.edu.unq.ttip.sportbook.persistence.repository.SportProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SportProfileService(
    private val sportProfileRepository: SportProfileRepository
) {
    @Transactional
    fun updateFootballProfile(user: SportUser, req: UpdateFootballProfileRequest): FootballProfileDTO {
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

        val saved = sportProfileRepository.save(profile)
        val savedDetails = saved.details as FootballProfileDetail
        return FootballProfileDTO(
            positions = savedDetails.positions,
            favoritePosition = savedDetails.favoritePosition,
            ability = savedDetails.ability,
            playsOften = savedDetails.playsOften,
        )
    }

    @Transactional
    fun updateVolleyProfile(user: SportUser, req: UpdateVolleyProfileRequest): VolleyProfileDTO {
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

        val saved = sportProfileRepository.save(profile)
        val savedDetails = saved.details as VolleyProfileDetail
        return VolleyProfileDTO(
            positions = savedDetails.positions,
            favoritePosition = savedDetails.favoritePosition,
            ability = savedDetails.ability,
            playsOften = savedDetails.playsOften,
            blockHeight = savedDetails.blockHeight,
            rolePreference = savedDetails.rolePreference
        )
    }

    @Transactional
    fun updatePaddleProfile(user: SportUser, req: UpdatePaddleProfileRequest): PaddleProfileDTO {
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

        val saved = sportProfileRepository.save(profile)
        val savedDetails = saved.details as PaddleProfileDetail
        return PaddleProfileDTO(
            preferredSide = savedDetails.preferredSide,
            ability = savedDetails.ability,
            playsOften = savedDetails.playsOften,
            playStyle = savedDetails.playStyle,
            playedTournaments = savedDetails.playedTournaments
        )
    }

    fun getProfiles(user: SportUser): List<SportProfileDTO> {
        val profiles = sportProfileRepository.findAllByUser(user)

        return profiles.map { profile ->
            when (profile.sport) {
                Sport.FOOTBALL -> {
                    val details = profile.details as FootballProfileDetail
                    SportProfileDTO(
                        sport = profile.sport,
                        details = FootballProfileDTO(
                            positions = details.positions,
                            favoritePosition = details.favoritePosition,
                            ability = details.ability,
                            playsOften = details.playsOften,
                        )
                    )
                }
                Sport.VOLLEY -> {
                    val details = profile.details as VolleyProfileDetail
                    SportProfileDTO(
                        sport = profile.sport,
                        details = VolleyProfileDTO(
                            positions = details.positions,
                            favoritePosition = details.favoritePosition,
                            ability = details.ability,
                            playsOften = details.playsOften,
                            blockHeight = details.blockHeight,
                            rolePreference = details.rolePreference
                        )
                    )
                }
                Sport.PADDLE -> {
                    val details = profile.details as PaddleProfileDetail
                    SportProfileDTO(
                        sport = profile.sport,
                        details = PaddleProfileDTO(
                            preferredSide = details.preferredSide,
                            ability = details.ability,
                            playsOften = details.playsOften,
                            playStyle = details.playStyle,
                            playedTournaments = details.playedTournaments
                        )
                    )
                }
            }
        }
    }
}
