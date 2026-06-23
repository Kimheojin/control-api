package heojin.control_api.telemetry.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import heojin.control_api.bus.mapper.TimeMapper;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.telemetry.dto.BusEventsResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.repository.BusEventRepository;
import heojin.control_api.telemetry.repository.BusEventRow;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusEventServiceTest {

	@Mock
	private BusRepository busRepository;

	@Mock
	private BusEventRepository busEventRepository;

	private BusEventService busEventService;

	@BeforeEach
	void setUp() {
		busEventService = new BusEventService(
				busRepository,
				busEventRepository,
				new TimeMapper());
	}

	@Test
	void getBusEventsReturnsRecentEvents() {
		when(busRepository.existsById(1L)).thenReturn(true);
		when(busEventRepository.findRecentByBusId(1L, 10))
				.thenReturn(List.of(new BusEventRow(
						501L,
						1L,
						"서울74사1234",
						"271번",
						BusEventType.SUDDEN_BRAKE,
						new BigDecimal("37.5658"),
						new BigDecimal("126.9772"),
						LocalDateTime.of(2026, 6, 23, 1, 18, 12),
						"급정거 감지")));

		BusEventsResponse response = busEventService.getBusEvents(1L, 10);

		assertThat(response.busId()).isEqualTo(1L);
		assertThat(response.events()).hasSize(1);
		assertThat(response.events().getFirst().occurredAt()).isEqualTo(Instant.parse("2026-06-23T01:18:12Z"));
	}

	@Test
	void getBusEventsThrowsBusinessExceptionWhenBusDoesNotExist() {
		when(busRepository.existsById(999L)).thenReturn(false);

		assertThatThrownBy(() -> busEventService.getBusEvents(999L, 10))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BUS_NOT_FOUND));
	}
}
