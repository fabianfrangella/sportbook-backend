package ar.edu.unq.ttip.sportbook.persistence.entity.exception

import ar.edu.unq.ttip.sportbook.exception.BusinessException

class NotTeamMemberException(playerName: String) :
    BusinessException("El jugador $playerName no pertenece al equipo")