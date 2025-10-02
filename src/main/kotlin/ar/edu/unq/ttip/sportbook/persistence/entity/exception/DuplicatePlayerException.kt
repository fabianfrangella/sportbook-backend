package ar.edu.unq.ttip.sportbook.persistence.entity.exception

import ar.edu.unq.ttip.sportbook.exception.BusinessException

class DuplicatePlayerException(playerName: String)
    : BusinessException("El jugador $playerName ya está en la alineación/banco")