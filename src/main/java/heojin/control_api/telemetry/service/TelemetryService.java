package heojin.control_api.telemetry.service;

import heojin.control_api.bus.entity.Bus;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.telemetry.dto.TelemetryRequest;
import heojin.control_api.telemetry.dto.TelemetryResponse;
import heojin.control_api.telemetry.entity.BusEvent;
import heojin.control_api.telemetry.entity.BusLocation;
import heojin.control_api.telemetry.repository.BusEventRepository;
import heojin.control_api.telemetry.repository.BusLocationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelemetryService {

	private final BusRepository busRepository;
	private final BusLocationRepository busLocationRepository;
	private final BusEventRepository busEventRepository;
	private final Clock clock;

	@Transactional
	public TelemetryResponse receiveTelemetry(TelemetryRequest request) {
		Bus bus = busRepository.findById(request.busId())
				.orElseThrow(() -> new BusinessException(ErrorCode.BUS_NOT_FOUND));
		LocalDateTime recordedAt = LocalDateTime.ofInstant(request.recordedAt(), ZoneOffset.UTC);
		LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);

		busLocationRepository.save(BusLocation.record(
				request.busId(),
				request.latitude(),
				request.longitude(),
				request.speedKph(),
				recordedAt,
				now));
		bus.applyTelemetry(request.speedKph(), recordedAt, now);

		if (request.event() != null) {
			busEventRepository.save(BusEvent.record(
					request.busId(),
					request.event().type(),
					request.latitude(),
					request.longitude(),
					recordedAt,
					request.event().description(),
					now));
		}

		return new TelemetryResponse(request.busId(), true);
	}
}
