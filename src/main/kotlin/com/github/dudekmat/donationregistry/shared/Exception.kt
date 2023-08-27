package com.github.dudekmat.donationregistry.shared

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.validation.ConstraintViolationException

private val log = KotlinLogging.logger {}

class NotFoundException(message: String) : RuntimeException(message)

data class ErrorResponse(val message: String, val errors: List<Any> = listOf())

data class ValidationError(
    val target: String? = null,
    val field: String? = null,
    val message: String? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleException(exception: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        log.warn { "ConstraintViolationException: ${exception.message}" }

        val errorResponse = ErrorResponse(
            message = VALIDATION_ERROR_ALERT,
            errors = exception.constraintViolations.map { violation ->
                ValidationError(
                    target = violation.propertyPath.toString(),
                    message = violation.message
                )
            }
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(BindException::class)
    fun handleException(exception: BindException): ResponseEntity<ErrorResponse> {
        log.warn { "BindException: ${exception.message}" }

        val errorResponse = ErrorResponse(
            message = VALIDATION_ERROR_ALERT,
            errors = exception.bindingResult.fieldErrors.map { fieldError ->
                ValidationError(
                    target = fieldError.objectName,
                    field = fieldError.field,
                    message = fieldError.code
                )
            } + exception.globalErrors.map { globalError ->
                ValidationError(
                    target = globalError.objectName,
                    message = globalError.code
                )
            }
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleException(exception: NotFoundException): ResponseEntity<ErrorResponse> {
        log.error { "NotFoundException: ${exception.message}" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = exception.message ?: "Result not found."))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        log.error { "Exception: ${exception.message}" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = exception.message ?: "Unknown error occurred."))
    }

    companion object {
        private const val VALIDATION_ERROR_ALERT = "Validation error"
    }
}
