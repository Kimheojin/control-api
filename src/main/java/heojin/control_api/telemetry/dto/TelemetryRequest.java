package heojin.control_api.telemetry.dto;

import heojin.control_api.telemetry.entity.BusEventType;
import java.math.BigDecimal;
import java.time.Instant;

public record TelemetryRequest(
		Long busId,
		BigDecimal latitude,
		BigDecimal longitude,
		Integer speedKph,
		Instant recordedAt,
		EventRequest event
) {

	public record EventRequest(
			BusEventType type,
			String description
	) {
	}
}
