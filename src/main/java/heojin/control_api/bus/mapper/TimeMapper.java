package heojin.control_api.bus.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class TimeMapper {

	public Instant toUtcInstant(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.toInstant(ZoneOffset.UTC);
	}
}
