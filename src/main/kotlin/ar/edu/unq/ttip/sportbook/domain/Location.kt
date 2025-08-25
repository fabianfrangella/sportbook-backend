package ar.edu.unq.ttip.sportbook.domain

import ar.edu.unq.ttip.sportbook.persistence.entity.LocationJPA

data class Location(val x: String, var y: String, val placeName: String) {
    fun toEntity() : LocationJPA {
        val locationJpa = LocationJPA()
        locationJpa.x = this.x
        locationJpa.y = this.y
        locationJpa.placeName = this.placeName
        return locationJpa
    }
}