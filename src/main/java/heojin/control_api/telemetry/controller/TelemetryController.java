package heojin.control_api.telemetry.controller;

import heojin.control_api.telemetry.dto.TelemetryRequest;
import heojin.control_api.telemetry.dto.TelemetryResponse;
import heojin.control_api.telemetry.service.TelemetryService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/internal/telemetry")
public class TelemetryController {

	private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
	private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
	private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
	private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");

	private final TelemetryService telemetryService;

	@PostMapping
	public TelemetryResponse receiveTelemetry(@RequestBody TelemetryRequest request) {
		validateRequest(request);
		return telemetryService.receiveTelemetry(request);
	}

	private void validateRequest(TelemetryRequest request) {
		if (request == null
				|| request.busId() == null
				|| request.latitude() == null
				|| request.longitude() == null
				|| request.speedKph() == null
				|| request.recordedAt() == null) {
			throw new IllegalArgumentException("missing telemetry request field");
		}
		if (request.latitude().compareTo(MIN_LATITUDE) < 0 || request.latitude().compareTo(MAX_LATITUDE) > 0) {
			throw new IllegalArgumentException("invalid latitude");
		}
		if (request.longitude().compareTo(MIN_LONGITUDE) < 0 || request.longitude().compareTo(MAX_LONGITUDE) > 0) {
			throw new IllegalArgumentException("invalid longitude");
		}
		if (request.speedKph() < 0) {
			throw new IllegalArgumentException("invalid speed");
		}
		if (request.event() != null && request.event().type() == null) {
			throw new IllegalArgumentException("missing event type");
		}
	}
}
