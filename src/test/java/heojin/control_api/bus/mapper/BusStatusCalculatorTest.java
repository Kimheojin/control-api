package heojin.control_api.bus.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import heojin.control_api.bus.dto.BusStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class BusStatusCalculatorTest {

	private final BusStatusCalculator calculator = new BusStatusCalculator();
	private final Clock clock = Clock.fixed(Instant.parse("2026-06-23T01:20:00Z"), ZoneOffset.UTC);

	@Test
	void calculateReturnsOnlineWhenLastCommunicatedWithinFiveMinutes() {
		BusStatus status = calculator.calculate(LocalDateTime.of(2026, 6, 23, 1, 15), clock);

		assertThat(status).isEqualTo(BusStatus.ONLINE);
	}

	@Test
	void calculateReturnsOfflineWhenLastCommunicatedOverFiveMinutesAgo() {
		BusStatus status = calculator.calculate(LocalDateTime.of(2026, 6, 23, 1, 14, 59), clock);

		assertThat(status).isEqualTo(BusStatus.OFFLINE);
	}

	@Test
	void calculateReturnsOfflineWhenLastCommunicatedAtIsNull() {
		BusStatus status = calculator.calculate(null, clock);

		assertThat(status).isEqualTo(BusStatus.OFFLINE);
	}
}
