package paengbeom.syono.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import paengbeom.syono.dto.ErrorResult;

@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<ErrorResult> handleCustomException(CustomException ex) {
        logger.warn("CustomException : " + ex);
        return new ResponseEntity<>(new ErrorResult(ex.getCode(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResult> handleAllExceptions(Exception ex) {
        logger.warn("Exception : " + ex);
        return new ResponseEntity<>(
                new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상하지 못한 문제가 발생했습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("handleHttpRequestMethodNotSupported : ", ex);
        return new ResponseEntity<>(new ErrorResult(status.value(), "적합한 HTTP Method로 요청해주세요."),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("handleMethodArgumentNotValid : ", ex);
        return new ResponseEntity<>(new ErrorResult(status.value(), "요청값 유효성 검사 실패."),
                HttpStatus.BAD_REQUEST
        );
    }
}
