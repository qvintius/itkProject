package mainpackage.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //неизвестная общая ошибка
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        log.error("Ошибка: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("Ошибка", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    //обработка ошибки попытки содания уже имеющейся сущности
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityExists(EntityExistsException e) {
        log.error("Сущность уже существует: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("Сущность уже существует", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    //обработка нарушений целостности данных (уникальность, внешние ключи)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Произошла ошибка целостности данных: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto("Произошла ошибка целостности данных", e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //сущность не найдена или пустой результат, когда ожидался не пустой
    @ExceptionHandler(exception = {EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(Exception e) {
        log.error("Сущность не найдена: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("Сущность не найдена", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    //обработка валидации, неверных аргументов, нарушении ограничений
    @ExceptionHandler(exception = {IllegalArgumentException.class, IllegalStateException.class,
            MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e) {
        String errorMessage = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError:  exception.getFieldErrors())
                sb.append(String.format("%s %s; ", fieldError.getField(), fieldError.getDefaultMessage()));
            errorMessage = sb.toString();
        }
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exception = (ConstraintViolationException) e;
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation constraintViolation: exception.getConstraintViolations())
                sb.append(String.format("%s %s; ", constraintViolation.getInvalidValue(), constraintViolation.getMessage()));
            errorMessage = sb.toString();
        }
        log.error("Неверный аргумент/состояние: {}", errorMessage);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("Неверный аргумент/состояние", errorMessage, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    //обработка всех остальных исключений JPA
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ErrorResponseDto> handleJpaSystemException(JpaSystemException e) {
        log.error("Произошла внутренняя ошибка при работе с бд: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto("Произошла внутренняя ошибка при работе с бд", e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
