package heojin.control_api.bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "buses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "route_id", nullable = false)
	private Long routeId;

	@Column(name = "bus_number", nullable = false, unique = true)
	private String busNumber;

	@Column(name = "current_speed_kph", nullable = false)
	private Integer currentSpeedKph;

	@Column(name = "last_communicated_at")
	private LocalDateTime lastCommunicatedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
