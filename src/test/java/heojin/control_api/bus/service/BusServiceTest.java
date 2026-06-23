package heojin.control_api.bus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import heojin.control_api.bus.dto.BusListResponse;
import heojin.control_api.bus.dto.BusStatus;
import heojin.control_api.bus.mapper.BusStatusCalculator;
import heojin.control_api.bus.mapper.TimeMapper;
import heojin.control_api.bus.repository.BusListRow;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.bus.repository.BusSearchCondition;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.route.repository.RouteRepository;
import heojin.control_api.telemetry.repository.BusLocationRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

	@Mock
	private BusRepository busRepository;

	@Mock
	private RouteRepository routeRepository;

	@Mock
	private BusLocationRepository busLocationRepository;

	@Captor
	private ArgumentCaptor<BusSearchCondition> conditionCaptor;

	private final Clock clock = Clock.fixed(Instant.parse("2026-06-23T01:20:00Z"), ZoneOffset.UTC);
	private BusService busService;

	@BeforeEach
	void setUp() {
		busService = new BusService(
				busRepository,
				routeRepository,
				busLocationRepository,
				new BusStatusCalculator(),
				new TimeMapper(),
				clock);
	}

	@Test
	void getBusesReturnsPagedResponseWithCalculatedStatus() {
		when(busRepository.search(conditionCaptor.capture(), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(new BusListRow(
						1L,
						"서울74사1234",
						1001L,
						"271번",
						42,
						LocalDateTime.of(2026, 6, 23, 1, 20)))));

		BusListResponse response = busService.getBuses(BusStatus.ONLINE, 1001L, "271", 0, 20);

		assertThat(response.content()).hasSize(1);
		assertThat(response.content().getFirst().status()).isEqualTo(BusStatus.ONLINE);
		assertThat(response.content().getFirst().lastCommunicatedAt()).isEqualTo(Instant.parse("2026-06-23T01:20:00Z"));
		assertThat(conditionCaptor.getValue().onlineCutoff()).isEqualTo(LocalDateTime.of(2026, 6, 23, 1, 15));
	}

	@Test
	void getBusThrowsBusinessExceptionWhenBusDoesNotExist() {
		when(busRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> busService.getBus(999L))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BUS_NOT_FOUND));
	}
}
