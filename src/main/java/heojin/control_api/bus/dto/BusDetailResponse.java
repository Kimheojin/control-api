package heojin.control_api.bus.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BusDetailResponse(
		Long id,
		String busNumber,
		RouteResponse route,
		Integer currentSpeed,
		BusStatus status,
		Instant lastCommunicatedAt,
		CurrentLocationResponse currentLocation
) {

	public record RouteResponse(
			Long id,
			String name
	) {
	}

	public record CurrentLocationResponse(
			BigDecimal latitude,
			BigDecimal longitude,
			Instant recordedAt
	) {
	}
}
