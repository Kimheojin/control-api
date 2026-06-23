package heojin.control_api.telemetry.repository;

import heojin.control_api.telemetry.entity.BusEventType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BusEventRow(
		Long id,
		Long busId,
		String busNumber,
		String routeName,
		BusEventType type,
		BigDecimal latitude,
		BigDecimal longitude,
		LocalDateTime occurredAt,
		String description
) {
}
