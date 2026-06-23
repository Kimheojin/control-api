package heojin.control_api.telemetry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "bus_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bus_id", nullable = false)
	private Long busId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BusEventType type;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	@Column
	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public static BusEvent record(
			Long busId,
			BusEventType type,
			BigDecimal latitude,
			BigDecimal longitude,
			LocalDateTime occurredAt,
			String description,
			LocalDateTime createdAt
	) {
		BusEvent event = new BusEvent();
		event.busId = busId;
		event.type = type;
		event.latitude = latitude;
		event.longitude = longitude;
		event.occurredAt = occurredAt;
		event.description = description;
		event.createdAt = createdAt;
		return event;
	}
}
