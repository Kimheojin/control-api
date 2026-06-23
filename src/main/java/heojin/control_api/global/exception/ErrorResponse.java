package heojin.control_api.global.exception;

public record ErrorResponse(
		String code,
		String message
) {

	public static ErrorResponse from(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage());
	}
}
