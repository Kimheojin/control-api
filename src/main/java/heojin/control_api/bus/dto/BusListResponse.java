package heojin.control_api.bus.dto;

import java.time.Instant;
import java.util.List;

public record BusListResponse(
		List<BusSummaryResponse> content,
		int page,
		int size,
		long totalElements,
		int totalPages
) {

	public record BusSummaryResponse(
			Long id,
			String busNumber,
			Long routeId,
			String routeName,
			Integer currentSpeed,
			BusStatus status,
			Instant lastCommunicatedAt
	) {
	}
}
