package heojin.control_api.telemetry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bus_locations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bus_id", nullable = false)
	private Long busId;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(name = "speed_kph", nullable = false)
	private Integer speedKph;

	@Column(name = "recorded_at", nullable = false)
	private LocalDateTime recordedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public static BusLocation record(
			Long busId,
			BigDecimal latitude,
			BigDecimal longitude,
			Integer speedKph,
			LocalDateTime recordedAt,
			LocalDateTime createdAt
	) {
		BusLocation location = new BusLocation();
		location.busId = busId;
		location.latitude = latitude;
		location.longitude = longitude;
		location.speedKph = speedKph;
		location.recordedAt = recordedAt;
		location.createdAt = createdAt;
		return location;
	}
}
