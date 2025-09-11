import org.springframework.http.HttpStatus

class BusinessException(
    override val message: String,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)
