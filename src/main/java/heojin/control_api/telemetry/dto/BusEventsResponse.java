package heojin.control_api.telemetry.dto;

import heojin.control_api.telemetry.entity.BusEventType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BusEventsResponse(
		Long busId,
		List<EventResponse> events
) {

	public record EventResponse(
			Long id,
			BusEventType type,
			Instant occurredAt,
			EventLocationResponse location,
			String description
	) {
	}

	public record EventLocationResponse(
			BigDecimal latitude,
			BigDecimal longitude
	) {
	}
}
