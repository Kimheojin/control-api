package heojin.control_api.bus.repository;

import java.time.LocalDateTime;

public record BusListRow(
		Long id,
		String busNumber,
		Long routeId,
		String routeName,
		Integer currentSpeedKph,
		LocalDateTime lastCommunicatedAt
) {
}
