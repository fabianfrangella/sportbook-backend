package ar.edu.unq.ttip.sportbook.domain.paddel

import ar.edu.unq.ttip.sportbook.domain.MatchDetails
import ar.edu.unq.ttip.sportbook.domain.Team

class PaddelMatchDetails(
    val teams: List<Team>,
) : MatchDetails
