package ar.edu.unq.ttip.sportbook.controller

import ar.edu.unq.ttip.sportbook.security.UserDetailsImpl
import ar.edu.unq.ttip.sportbook.service.ProfilePictureService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/profile-picture")
@CrossOrigin
class ProfilePictureController(
    private val profilePictureService: ProfilePictureService
) {
    @PostMapping("/upload")
    @Operation(
        summary = "Subir foto de perfil",
        description = "Sube o actualiza la foto de perfil del usuario autenticado.")
    fun uploadProfilePicture(
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): ResponseEntity<String> {
        profilePictureService.uploadProfilePicture(file, userDetails.sportUser)
        return ResponseEntity.ok("Foto de perfil actualizada exitosamente")
    }

    @GetMapping
    @Operation(
        summary = "Obtener foto de perfil",
        description = "Obtiene la foto de perfil del usuario autenticado."
    )
    fun getProfilePicture(@AuthenticationPrincipal userDetails: UserDetailsImpl): ResponseEntity<ByteArray> {
        val pictureData = profilePictureService.getProfilePicture(userDetails.sportUser)
        return if (pictureData != null) {
            ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(pictureData)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
