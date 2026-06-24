package heojin.control_api.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import heojin.control_api.bus.entity.Bus;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.route.repository.RouteRepository;
import heojin.control_api.route.repository.RouteStopRepository;
import heojin.control_api.stop.repository.StopRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QuerydslConfig.class)
class SeedDataInitializerTest {

	private final RouteRepository routeRepository;
	private final StopRepository stopRepository;
	private final RouteStopRepository routeStopRepository;
	private final BusRepository busRepository;
	private SeedDataInitializer initializer;

	@Autowired
	SeedDataInitializerTest(
			RouteRepository routeRepository,
			StopRepository stopRepository,
			RouteStopRepository routeStopRepository,
			BusRepository busRepository
	) {
		this.routeRepository = routeRepository;
		this.stopRepository = stopRepository;
		this.routeStopRepository = routeStopRepository;
		this.busRepository = busRepository;
	}

	@BeforeEach
	void setUp() {
		initializer = new SeedDataInitializer(
				routeRepository,
				stopRepository,
				routeStopRepository,
				busRepository);
	}

	@Test
	void runInsertsBaseSeedDataWhenTablesAreEmpty() {
		initializer.run();

		assertThat(routeRepository.count()).isEqualTo(3);
		assertThat(stopRepository.count()).isEqualTo(12);
		assertThat(routeStopRepository.count()).isEqualTo(17);
		assertThat(busRepository.count()).isEqualTo(8);

		List<Bus> buses = busRepository.findAll();
		assertThat(buses)
				.extracting(Bus::getBusNumber)
				.containsExactlyInAnyOrder(
						"서울74사1234",
						"서울74사1235",
						"서울74사1236",
						"서울75사2101",
						"서울75사2102",
						"서울70사4301",
						"서울70사4302",
						"서울70사4303");
		assertThat(buses)
				.allSatisfy(bus -> {
					assertThat(bus.getCurrentSpeedKph()).isZero();
					assertThat(bus.getLastCommunicatedAt()).isNull();
				});
	}

	@Test
	void runDoesNotDuplicateSeedDataWhenExecutedAgain() {
		initializer.run();
		initializer.run();

		assertThat(routeRepository.count()).isEqualTo(3);
		assertThat(stopRepository.count()).isEqualTo(12);
		assertThat(routeStopRepository.count()).isEqualTo(17);
		assertThat(busRepository.count()).isEqualTo(8);
	}
}
