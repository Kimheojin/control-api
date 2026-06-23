package heojin.control_api.telemetry.controller;

import heojin.control_api.telemetry.dto.RecentEventsResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.service.BusEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/events")
public class EventController {

	private final BusEventService busEventService;

	@GetMapping
	public RecentEventsResponse getRecentEvents(
			@RequestParam(required = false) BusEventType type,
			@RequestParam(defaultValue = "20") int limit
	) {
		validateLimit(limit);
		return busEventService.getRecentEvents(type, limit);
	}

	private void validateLimit(int limit) {
		if (limit < 1 || limit > 100) {
			throw new IllegalArgumentException("invalid limit");
		}
	}
}
