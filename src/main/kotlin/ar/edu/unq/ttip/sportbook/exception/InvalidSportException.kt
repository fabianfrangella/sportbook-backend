package ar.edu.unq.ttip.sportbook.exception

class InvalidSportException(value: String) : BusinessException("Deporte inválido: '$value'")