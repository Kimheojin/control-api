package heojin.control_api.telemetry.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import heojin.control_api.bus.entity.Bus;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.telemetry.dto.TelemetryRequest;
import heojin.control_api.telemetry.dto.TelemetryResponse;
import heojin.control_api.telemetry.entity.BusEvent;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.entity.BusLocation;
import heojin.control_api.telemetry.repository.BusEventRepository;
import heojin.control_api.telemetry.repository.BusLocationRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

	@Mock
	private BusRepository busRepository;

	@Mock
	private BusLocationRepository busLocationRepository;

	@Mock
	private BusEventRepository busEventRepository;

	@Mock
	private Bus bus;

	@Captor
	private ArgumentCaptor<BusLocation> locationCaptor;

	@Captor
	private ArgumentCaptor<BusEvent> eventCaptor;

	private final Clock clock = Clock.fixed(Instant.parse("2026-06-23T01:21:00Z"), ZoneOffset.UTC);
	private TelemetryService telemetryService;

	@BeforeEach
	void setUp() {
		telemetryService = new TelemetryService(
				busRepository,
				busLocationRepository,
				busEventRepository,
				clock);
	}

	@Test
	void receiveTelemetryStoresLocationUpdatesBusAndStoresEvent() {
		when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

		TelemetryResponse response = telemetryService.receiveTelemetry(new TelemetryRequest(
				1L,
				new BigDecimal("37.5665"),
				new BigDecimal("126.9780"),
				42,
				Instant.parse("2026-06-23T01:20:00Z"),
				new TelemetryRequest.EventRequest(BusEventType.SUDDEN_BRAKE, "급정거 감지")));

		assertThat(response.busId()).isEqualTo(1L);
		assertThat(response.received()).isTrue();
		verify(busLocationRepository).save(locationCaptor.capture());
		BusLocation location = locationCaptor.getValue();
		assertThat(location.getBusId()).isEqualTo(1L);
		assertThat(location.getLatitude()).isEqualByComparingTo("37.5665");
		assertThat(location.getLongitude()).isEqualByComparingTo("126.9780");
		assertThat(location.getSpeedKph()).isEqualTo(42);
		assertThat(location.getRecordedAt()).isEqualTo(LocalDateTime.of(2026, 6, 23, 1, 20));
		assertThat(location.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 6, 23, 1, 21));
		verify(bus).applyTelemetry(
				42,
				LocalDateTime.of(2026, 6, 23, 1, 20),
				LocalDateTime.of(2026, 6, 23, 1, 21));

		verify(busEventRepository).save(eventCaptor.capture());
		BusEvent event = eventCaptor.getValue();
		assertThat(event.getBusId()).isEqualTo(1L);
		assertThat(event.getType()).isEqualTo(BusEventType.SUDDEN_BRAKE);
		assertThat(event.getOccurredAt()).isEqualTo(LocalDateTime.of(2026, 6, 23, 1, 20));
		assertThat(event.getDescription()).isEqualTo("급정거 감지");
	}

	@Test
	void receiveTelemetryDoesNotStoreEventWhenEventIsMissing() {
		when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

		telemetryService.receiveTelemetry(new TelemetryRequest(
				1L,
				new BigDecimal("37.5665"),
				new BigDecimal("126.9780"),
				42,
				Instant.parse("2026-06-23T01:20:00Z"),
				null));

		verify(busLocationRepository).save(locationCaptor.capture());
		verify(busEventRepository, never()).save(org.mockito.ArgumentMatchers.any(BusEvent.class));
	}

	@Test
	void receiveTelemetryThrowsBusinessExceptionWhenBusDoesNotExist() {
		when(busRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> telemetryService.receiveTelemetry(new TelemetryRequest(
				999L,
				new BigDecimal("37.5665"),
				new BigDecimal("126.9780"),
				42,
				Instant.parse("2026-06-23T01:20:00Z"),
				null)))
				.isInstanceOfSatisfying(BusinessException.class, exception ->
						assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BUS_NOT_FOUND));
		verifyNoInteractions(busLocationRepository, busEventRepository);
	}
}
