package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.dto.request.UpdateUserDataRequest
import ar.edu.unq.ttip.sportbook.persistence.entity.user.SportUser
import ar.edu.unq.ttip.sportbook.service.SportUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/user-data"])
@CrossOrigin(origins = ["http://localhost:5173"])
@Tag(
    name = "User Data",
    description = "Endpoints de modificación de datos de usuarios"
)
class UserDataController(val sportUserService: SportUserService) {

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Actualizar datos del usuario",
        method = "PUT",
        description = "Endpoint para actualizar los datos de un usuario.")
    fun updateSportUser(
        @PathVariable("userId") userId: Long,
        @RequestBody userData: UpdateUserDataRequest
    ): SportUser = sportUserService.updateSportUser(userId, userData)
}