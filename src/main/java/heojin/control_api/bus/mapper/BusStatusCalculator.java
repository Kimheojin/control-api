package heojin.control_api.bus.mapper;

import heojin.control_api.bus.dto.BusStatus;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class BusStatusCalculator {

	private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

	public BusStatus calculate(LocalDateTime lastCommunicatedAt, Clock clock) {
		if (lastCommunicatedAt == null) {
			return BusStatus.OFFLINE;
		}

		LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
		Duration elapsed = Duration.between(lastCommunicatedAt, now);
		return elapsed.compareTo(ONLINE_THRESHOLD) <= 0 ? BusStatus.ONLINE : BusStatus.OFFLINE;
	}

	public LocalDateTime onlineCutoff(Clock clock) {
		return LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC).minus(ONLINE_THRESHOLD);
	}
}
