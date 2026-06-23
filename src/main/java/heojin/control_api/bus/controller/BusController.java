package heojin.control_api.bus.controller;

import heojin.control_api.bus.dto.BusDetailResponse;
import heojin.control_api.bus.dto.BusListResponse;
import heojin.control_api.bus.dto.BusStatus;
import heojin.control_api.bus.service.BusService;
import heojin.control_api.telemetry.dto.BusEventsResponse;
import heojin.control_api.telemetry.service.BusEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/buses")
public class BusController {

	private final BusService busService;
	private final BusEventService busEventService;

	@GetMapping
	public BusListResponse getBuses(
			@RequestParam(required = false) BusStatus status,
			@RequestParam(required = false) Long routeId,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size
	) {
		validatePage(page, size);
		return busService.getBuses(status, routeId, keyword, page, size);
	}

	@GetMapping("/{busId}")
	public BusDetailResponse getBus(@PathVariable Long busId) {
		return busService.getBus(busId);
	}

	@GetMapping("/{busId}/events")
	public BusEventsResponse getBusEvents(
			@PathVariable Long busId,
			@RequestParam(defaultValue = "10") int limit
	) {
		validateLimit(limit);
		return busEventService.getBusEvents(busId, limit);
	}

	private void validatePage(int page, int size) {
		if (page < 0 || size < 1 || size > 100) {
			throw new IllegalArgumentException("invalid page request");
		}
	}

	private void validateLimit(int limit) {
		if (limit < 1 || limit > 100) {
			throw new IllegalArgumentException("invalid limit");
		}
	}
}
