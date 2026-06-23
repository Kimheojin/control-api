package heojin.control_api.bus.service;

import heojin.control_api.bus.dto.BusDetailResponse;
import heojin.control_api.bus.dto.BusListResponse;
import heojin.control_api.bus.dto.BusListResponse.BusSummaryResponse;
import heojin.control_api.bus.dto.BusStatus;
import heojin.control_api.bus.entity.Bus;
import heojin.control_api.bus.mapper.BusStatusCalculator;
import heojin.control_api.bus.mapper.TimeMapper;
import heojin.control_api.bus.repository.BusListRow;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.bus.repository.BusSearchCondition;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.route.entity.Route;
import heojin.control_api.route.repository.RouteRepository;
import heojin.control_api.telemetry.entity.BusLocation;
import heojin.control_api.telemetry.repository.BusLocationRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusService {

	private final BusRepository busRepository;
	private final RouteRepository routeRepository;
	private final BusLocationRepository busLocationRepository;
	private final BusStatusCalculator busStatusCalculator;
	private final TimeMapper timeMapper;
	private final Clock clock;

	public BusListResponse getBuses(BusStatus status, Long routeId, String keyword, int page, int size) {
		BusSearchCondition condition = new BusSearchCondition(
				status,
				routeId,
				keyword,
				busStatusCalculator.onlineCutoff(clock));
		Page<BusListRow> buses = busRepository.search(condition, PageRequest.of(page, size));

		return new BusListResponse(
				buses.getContent().stream()
						.map(this::toSummaryResponse)
						.toList(),
				buses.getNumber(),
				buses.getSize(),
				buses.getTotalElements(),
				buses.getTotalPages());
	}

	public BusDetailResponse getBus(Long busId) {
		Bus bus = busRepository.findById(busId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BUS_NOT_FOUND));
		Route route = routeRepository.findById(bus.getRouteId()).orElse(null);
		BusLocation location = busLocationRepository.findFirstByBusIdOrderByRecordedAtDesc(busId).orElse(null);

		return new BusDetailResponse(
				bus.getId(),
				bus.getBusNumber(),
				new BusDetailResponse.RouteResponse(bus.getRouteId(), route == null ? null : route.getName()),
				bus.getCurrentSpeedKph(),
				busStatusCalculator.calculate(bus.getLastCommunicatedAt(), clock),
				timeMapper.toUtcInstant(bus.getLastCommunicatedAt()),
				toCurrentLocationResponse(location));
	}

	private BusSummaryResponse toSummaryResponse(BusListRow row) {
		return new BusSummaryResponse(
				row.id(),
				row.busNumber(),
				row.routeId(),
				row.routeName(),
				row.currentSpeedKph(),
				busStatusCalculator.calculate(row.lastCommunicatedAt(), clock),
				timeMapper.toUtcInstant(row.lastCommunicatedAt()));
	}

	private BusDetailResponse.CurrentLocationResponse toCurrentLocationResponse(BusLocation location) {
		if (location == null) {
			return null;
		}
		return new BusDetailResponse.CurrentLocationResponse(
				location.getLatitude(),
				location.getLongitude(),
				timeMapper.toUtcInstant(location.getRecordedAt()));
	}
}
