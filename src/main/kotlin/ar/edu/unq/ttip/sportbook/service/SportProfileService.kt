package ar.edu.unq.ttip.sportbook.service

import ar.edu.unq.ttip.sportbook.persistence.entity.event.football.FootballProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.paddle.PaddleProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.event.volley.VolleyProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.user.Sport
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfile
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportProfileDetail
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.persistence.repository.SportProfileRepository
import ar.edu.unq.ttip.sportbook.service.command.FootballProfileUpdate
import ar.edu.unq.ttip.sportbook.service.command.PaddleProfileUpdate
import ar.edu.unq.ttip.sportbook.service.command.VolleyProfileUpdate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SportProfileService(
    private val sportProfileRepository: SportProfileRepository
) {

    @Transactional
    fun updateFootballProfile(user: SportUser, cmd: FootballProfileUpdate): SportProfile =
        upsertProfile(
            user = user,
            sport = Sport.FOOTBALL,
            buildDetails = {
                FootballProfileDetail(
                    positions = cmd.positions.toMutableList(),
                    favoritePosition = cmd.favoritePosition,
                    ability = cmd.ability,
                    playsOften = cmd.playsOften
                )
            }
        )

    @Transactional
    fun updateVolleyProfile(user: SportUser, cmd: VolleyProfileUpdate): SportProfile =
        upsertProfile(
            user = user,
            sport = Sport.VOLLEY,
            buildDetails = {
                VolleyProfileDetail(
                    positions = cmd.positions.toMutableList(),
                    favoritePosition = cmd.favoritePosition,
                    ability = cmd.ability,
                    playsOften = cmd.playsOften,
                    blockHeight = cmd.blockHeight,
                    rolePreference = cmd.rolePreference,
                    serveType = cmd.serveType,
                    offensiveLevel = cmd.offensiveLevel,
                    defensiveLevel = cmd.defensiveLevel
                )
            }
        )

    @Transactional
    fun updatePaddleProfile(user: SportUser, cmd: PaddleProfileUpdate): SportProfile =
        upsertProfile(
            user = user,
            sport = Sport.PADDLE,
            buildDetails = {
                PaddleProfileDetail(
                    preferredSide = cmd.preferredSide,
                    ability = cmd.ability,
                    playsOften = cmd.playsOften,
                    playStyle = cmd.playStyle,
                    playedTournaments = cmd.playedTournaments
                )
            }
        )

    @Transactional(readOnly = true)
    fun getProfiles(user: SportUser): List<SportProfile> {
        val userId = requireNotNull(user.id) { "SportUser.id no puede ser null" }
        return sportProfileRepository.findAllByUserId(userId)
    }

    @Transactional
    fun upsertProfile(
        user: SportUser,
        sport: Sport,
        buildDetails: () -> SportProfileDetail
    ): SportProfile {
        val userId = requireNotNull(user.id) { "SportUser.id no puede ser null" }

        val existing = sportProfileRepository.findByUserIdAndSport(userId, sport)

        val profile = if (existing != null) {
            existing.updateDetails(buildDetails())
            existing
        } else {
            val newDetail = buildDetails()
            val created = SportProfile(user = user, sport = sport, details = newDetail)

            user.addProfile(created)
            created
        }

        return sportProfileRepository.save(profile)
    }
}
