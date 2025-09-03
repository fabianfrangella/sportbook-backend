package ar.edu.unq.ttip.sportbook.controller

import BusinessException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val body = ApiError(
            status = ex.httpStatus.value(),
            error = ex.httpStatus.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity(body, ex.httpStatus)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val body = ApiError(
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
    ): ResponseEntity<ApiError> {
        val body = ApiError(
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
    ): ResponseEntity<ApiError> {
        val body = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

data class ApiError(
    val status: Int,
    val error: String,
    val message: String?,
    val path: String? = null
)
