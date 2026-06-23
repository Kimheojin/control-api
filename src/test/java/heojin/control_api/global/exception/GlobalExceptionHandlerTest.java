package heojin.control_api.global.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

	private final MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new TestController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

	@Test
	void businessExceptionReturnsErrorCodeResponse() throws Exception {
		mockMvc.perform(get("/test/business-exception"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BUS_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("버스를 찾을 수 없습니다."));
	}

	@Test
	void invalidPathVariableReturnsInvalidRequest() throws Exception {
		mockMvc.perform(get("/test/path/not-number"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
				.andExpect(jsonPath("$.message").value("요청 값이 올바르지 않습니다."));
	}

	@Test
	void unknownExceptionReturnsInternalServerError() throws Exception {
		mockMvc.perform(get("/test/unknown-exception"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."));
	}

	@RestController
	static class TestController {

		@GetMapping("/test/business-exception")
		void businessException() {
			throw new BusinessException(ErrorCode.BUS_NOT_FOUND);
		}

		@GetMapping("/test/path/{id}")
		long path(@PathVariable long id) {
			return id;
		}

		@GetMapping("/test/unknown-exception")
		void unknownException() {
			throw new RuntimeException("unexpected");
		}
	}
}
