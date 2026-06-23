package heojin.control_api.telemetry.dto;

import heojin.control_api.telemetry.entity.BusEventType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RecentEventsResponse(
		List<EventResponse> events
) {

	public record EventResponse(
			Long id,
			EventBusResponse bus,
			BusEventType type,
			Instant occurredAt,
			EventLocationResponse location,
			String description
	) {
	}

	public record EventBusResponse(
			Long id,
			String busNumber,
			String routeName
	) {
	}

	public record EventLocationResponse(
			BigDecimal latitude,
			BigDecimal longitude
	) {
	}
}
