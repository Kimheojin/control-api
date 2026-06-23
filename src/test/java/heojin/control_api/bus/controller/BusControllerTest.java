package heojin.control_api.bus.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import heojin.control_api.bus.dto.BusDetailResponse;
import heojin.control_api.bus.dto.BusListResponse;
import heojin.control_api.bus.dto.BusStatus;
import heojin.control_api.bus.service.BusService;
import heojin.control_api.global.exception.BusinessException;
import heojin.control_api.global.exception.ErrorCode;
import heojin.control_api.global.exception.GlobalExceptionHandler;
import heojin.control_api.telemetry.dto.BusEventsResponse;
import heojin.control_api.telemetry.entity.BusEventType;
import heojin.control_api.telemetry.service.BusEventService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BusControllerTest {

	@Mock
	private BusService busService;

	@Mock
	private BusEventService busEventService;

	@Test
	void getBusesReturnsPagedResponse() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(busService.getBuses(eq(BusStatus.ONLINE), eq(1001L), eq("271"), eq(0), eq(20)))
				.thenReturn(new BusListResponse(
						List.of(new BusListResponse.BusSummaryResponse(
								1L,
								"서울74사1234",
								1001L,
								"271번",
								42,
								BusStatus.ONLINE,
								Instant.parse("2026-06-23T01:20:00Z"))),
						0,
						20,
						1,
						1));

		mockMvc.perform(get("/v1/buses")
						.param("status", "ONLINE")
						.param("routeId", "1001")
						.param("keyword", "271"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].status").value("ONLINE"))
				.andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void getBusesReturnsInvalidRequestWhenSizeExceedsMax() throws Exception {
		MockMvc mockMvc = mockMvc();

		mockMvc.perform(get("/v1/buses").param("size", "101"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}

	@Test
	void getBusReturnsDetailResponse() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(busService.getBus(1L))
				.thenReturn(new BusDetailResponse(
						1L,
						"서울74사1234",
						new BusDetailResponse.RouteResponse(1001L, "271번"),
						42,
						BusStatus.ONLINE,
						Instant.parse("2026-06-23T01:20:00Z"),
						new BusDetailResponse.CurrentLocationResponse(
								new BigDecimal("37.5665"),
								new BigDecimal("126.9780"),
								Instant.parse("2026-06-23T01:20:00Z"))));

		mockMvc.perform(get("/v1/buses/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.route.name").value("271번"))
				.andExpect(jsonPath("$.currentLocation.latitude").value(37.5665));
	}

	@Test
	void getBusReturnsBusNotFound() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(busService.getBus(999L)).thenThrow(new BusinessException(ErrorCode.BUS_NOT_FOUND));

		mockMvc.perform(get("/v1/buses/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BUS_NOT_FOUND"));
	}

	@Test
	void getBusEventsReturnsEvents() throws Exception {
		MockMvc mockMvc = mockMvc();
		when(busEventService.getBusEvents(1L, 10))
				.thenReturn(new BusEventsResponse(
						1L,
						List.of(new BusEventsResponse.EventResponse(
								501L,
								BusEventType.SUDDEN_BRAKE,
								Instant.parse("2026-06-23T01:18:12Z"),
								new BusEventsResponse.EventLocationResponse(
										new BigDecimal("37.5658"),
										new BigDecimal("126.9772")),
								"급정거 감지"))));

		mockMvc.perform(get("/v1/buses/1/events"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.busId").value(1))
				.andExpect(jsonPath("$.events[0].type").value("SUDDEN_BRAKE"));
	}

	private MockMvc mockMvc() {
		return MockMvcBuilders
				.standaloneSetup(new BusController(busService, busEventService))
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}
}
