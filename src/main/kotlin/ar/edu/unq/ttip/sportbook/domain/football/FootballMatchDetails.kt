package ar.edu.unq.ttip.sportbook.domain.football

import ar.edu.unq.ttip.sportbook.domain.MatchDetails
import ar.edu.unq.ttip.sportbook.domain.Team

class FootballMatchDetails(
    val pitchSize: PitchSize,
    val firstTeam: Team,
    val secondTeam: Team
) : MatchDetails
