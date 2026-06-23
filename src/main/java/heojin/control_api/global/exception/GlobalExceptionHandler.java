package heojin.control_api.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(ErrorResponse.from(errorCode));
	}

	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			BindException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class,
			HttpMessageNotReadableException.class,
			HttpRequestMethodNotSupportedException.class,
			HttpMediaTypeNotSupportedException.class,
			IllegalArgumentException.class
	})
	public ResponseEntity<ErrorResponse> handleInvalidRequestException(Exception exception) {
		return createErrorResponse(ErrorCode.INVALID_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
		return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(ErrorResponse.from(errorCode));
	}
}
