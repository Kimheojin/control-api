package heojin.control_api.telemetry.service;

import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.bus.mapper.TimeMapper;
import heojin.control_api.telemetry.dto.BusEventsResponse;
import heojin.control_api.telemetry.dto.RecentEventsResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.repository.BusEventRepository;
import heojin.control_api.telemetry.repository.BusEventRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusEventService {

	private final BusRepository busRepository;
	private final BusEventRepository busEventRepository;
	private final TimeMapper timeMapper;

	public BusEventsResponse getBusEvents(Long busId, int limit) {
		if (!busRepository.existsById(busId)) {
			throw new BusinessException(ErrorCode.BUS_NOT_FOUND);
		}

		return new BusEventsResponse(
				busId,
				busEventRepository.findRecentByBusId(busId, limit).stream()
						.map(this::toBusEventResponse)
						.toList());
	}

	public RecentEventsResponse getRecentEvents(BusEventType type, int limit) {
		return new RecentEventsResponse(
				busEventRepository.findRecentEvents(type, limit).stream()
						.map(this::toRecentEventResponse)
						.toList());
	}

	private BusEventsResponse.EventResponse toBusEventResponse(BusEventRow row) {
		return new BusEventsResponse.EventResponse(
				row.id(),
				row.type(),
				timeMapper.toUtcInstant(row.occurredAt()),
				new BusEventsResponse.EventLocationResponse(row.latitude(), row.longitude()),
				row.description());
	}

	private RecentEventsResponse.EventResponse toRecentEventResponse(BusEventRow row) {
		return new RecentEventsResponse.EventResponse(
				row.id(),
				new RecentEventsResponse.EventBusResponse(row.busId(), row.busNumber(), row.routeName()),
				row.type(),
				timeMapper.toUtcInstant(row.occurredAt()),
				new RecentEventsResponse.EventLocationResponse(row.latitude(), row.longitude()),
				row.description());
	}
}
