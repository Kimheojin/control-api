package heojin.control_api.telemetry.dto;

public record TelemetryResponse(
		Long busId,
		boolean received
) {
}
