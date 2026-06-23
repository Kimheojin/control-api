package heojin.control_api.bus.repository;

import heojin.control_api.bus.dto.BusStatus;
import java.time.LocalDateTime;

public record BusSearchCondition(
		BusStatus status,
		Long routeId,
		String keyword,
		LocalDateTime onlineCutoff
) {
}
