package heojin.control_api.global.config;

import heojin.control_api.bus.entity.Bus;
import heojin.control_api.bus.repository.BusRepository;
import heojin.control_api.route.entity.Route;
import heojin.control_api.route.entity.RouteStop;
import heojin.control_api.route.repository.RouteRepository;
import heojin.control_api.route.repository.RouteStopRepository;
import heojin.control_api.stop.entity.Stop;
import heojin.control_api.stop.repository.StopRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile({"local", "prod"})
@RequiredArgsConstructor
public class SeedDataInitializer implements CommandLineRunner {

	private final RouteRepository routeRepository;
	private final StopRepository stopRepository;
	private final RouteStopRepository routeStopRepository;
	private final BusRepository busRepository;

	@Override
	@Transactional
	public void run(String... args) {
		if (hasSeedData()) {
			log.info("Skip seed data initialization because base data already exists.");
			return;
		}

		LocalDateTime now = LocalDateTime.now();
		Map<String, Route> routes = saveRoutes(now);
		Map<String, Stop> stops = saveStops(now);
		saveRouteStops(routes, stops, now);
		saveBuses(routes, now);

		log.info("Initialized seed data: routes={}, stops={}, buses={}",
				routes.size(), stops.size(), busRepository.count());
	}

	private boolean hasSeedData() {
		return routeRepository.count() > 0
				|| stopRepository.count() > 0
				|| routeStopRepository.count() > 0
				|| busRepository.count() > 0;
	}

	private Map<String, Route> saveRoutes(LocalDateTime now) {
		List<Route> savedRoutes = routeRepository.saveAll(List.of(
				Route.create("271번", "상암동-종로-고려대 축", now),
				Route.create("7016번", "은평-서대문-서울역 축", now),
				Route.create("143번", "강북-도심-강남 축", now)
		));

		Map<String, Route> routes = new LinkedHashMap<>();
		for (Route route : savedRoutes) {
			routes.put(route.getName(), route);
		}
		return routes;
	}

	private Map<String, Stop> saveStops(LocalDateTime now) {
		List<Stop> savedStops = stopRepository.saveAll(List.of(
				Stop.create("상암DMC홍보관", decimal("37.5775000"), decimal("126.8903000"), now),
				Stop.create("월드컵경기장", decimal("37.5683000"), decimal("126.8972000"), now),
				Stop.create("홍대입구역", decimal("37.5572000"), decimal("126.9237000"), now),
				Stop.create("신촌오거리", decimal("37.5551000"), decimal("126.9368000"), now),
				Stop.create("광화문", decimal("37.5716000"), decimal("126.9769000"), now),
				Stop.create("종로3가", decimal("37.5704000"), decimal("126.9919000"), now),
				Stop.create("고려대역", decimal("37.5905000"), decimal("127.0363000"), now),
				Stop.create("불광역", decimal("37.6102000"), decimal("126.9293000"), now),
				Stop.create("독립문역", decimal("37.5745000"), decimal("126.9579000"), now),
				Stop.create("서울역버스환승센터", decimal("37.5559000"), decimal("126.9723000"), now),
				Stop.create("미아사거리역", decimal("37.6133000"), decimal("127.0301000"), now),
				Stop.create("강남역", decimal("37.4979000"), decimal("127.0276000"), now)
		));

		Map<String, Stop> stops = new LinkedHashMap<>();
		for (Stop stop : savedStops) {
			stops.put(stop.getName(), stop);
		}
		return stops;
	}

	private void saveRouteStops(Map<String, Route> routes, Map<String, Stop> stops, LocalDateTime now) {
		routeStopRepository.saveAll(List.of(
				routeStop(routes, stops, "271번", "상암DMC홍보관", 1, now),
				routeStop(routes, stops, "271번", "월드컵경기장", 2, now),
				routeStop(routes, stops, "271번", "홍대입구역", 3, now),
				routeStop(routes, stops, "271번", "신촌오거리", 4, now),
				routeStop(routes, stops, "271번", "광화문", 5, now),
				routeStop(routes, stops, "271번", "종로3가", 6, now),
				routeStop(routes, stops, "271번", "고려대역", 7, now),
				routeStop(routes, stops, "7016번", "불광역", 1, now),
				routeStop(routes, stops, "7016번", "독립문역", 2, now),
				routeStop(routes, stops, "7016번", "광화문", 3, now),
				routeStop(routes, stops, "7016번", "서울역버스환승센터", 4, now),
				routeStop(routes, stops, "143번", "미아사거리역", 1, now),
				routeStop(routes, stops, "143번", "고려대역", 2, now),
				routeStop(routes, stops, "143번", "종로3가", 3, now),
				routeStop(routes, stops, "143번", "광화문", 4, now),
				routeStop(routes, stops, "143번", "서울역버스환승센터", 5, now),
				routeStop(routes, stops, "143번", "강남역", 6, now)
		));
	}

	private RouteStop routeStop(
			Map<String, Route> routes,
			Map<String, Stop> stops,
			String routeName,
			String stopName,
			int stopOrder,
			LocalDateTime now
	) {
		return RouteStop.create(routes.get(routeName).getId(), stops.get(stopName).getId(), stopOrder, now);
	}

	private void saveBuses(Map<String, Route> routes, LocalDateTime now) {
		busRepository.saveAll(List.of(
				Bus.create(routes.get("271번").getId(), "서울74사1234", now),
				Bus.create(routes.get("271번").getId(), "서울74사1235", now),
				Bus.create(routes.get("271번").getId(), "서울74사1236", now),
				Bus.create(routes.get("7016번").getId(), "서울75사2101", now),
				Bus.create(routes.get("7016번").getId(), "서울75사2102", now),
				Bus.create(routes.get("143번").getId(), "서울70사4301", now),
				Bus.create(routes.get("143번").getId(), "서울70사4302", now),
				Bus.create(routes.get("143번").getId(), "서울70사4303", now)
		));
	}

	private BigDecimal decimal(String value) {
		return new BigDecimal(value);
	}
}
