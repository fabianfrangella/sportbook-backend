package ar.edu.unq.ttip.sportbook.exception

import ar.edu.unq.ttip.sportbook.controller.response.ApiErrorResponse
import ar.edu.unq.ttip.sportbook.persistence.entity.exception.DuplicatePlayerException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = ex.statusCode.value(),
            error = ex.reason?: "",
            message = ex.reason,
            path = request.requestURI
        )
        return ResponseEntity(body, ex.statusCode)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Elemento no encontrado",
            path = request.requestURI
        )
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error  = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Resource not found"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error  = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Bad request"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.CONFLICT
        val body = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "Conflicto en la solicitud"
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleDomain(ex: BusinessException): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        val body = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "Error de dominio"
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(DuplicatePlayerException::class)
    fun handleDuplicate(ex: DuplicatePlayerException): ResponseEntity<ApiErrorResponse> {
        val status = HttpStatus.CONFLICT
        val body = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = ex.message ?: "Conflicto con el estado actual"
        )
        return ResponseEntity.status(status).body(body)
    }
}
