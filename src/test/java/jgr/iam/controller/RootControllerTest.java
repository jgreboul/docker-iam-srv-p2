package jgr.iam.controller;

// External Objects
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Internal Objects
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.constant.iamServiceResponseCode;

// Root Controller (MockMvc) Test
@SpringBootTest
@AutoConfigureMockMvc
public class RootControllerTest {

	// Logger
	private final static Logger logger = LogManager.getLogger(RootControllerTest.class.getCanonicalName());

	@Autowired
	private MockMvc mockMvc;

	// Test "/" Index
	@Test
	public void smokeTestIndexRoot() {
		try {
			_ShouldReturnNotFoundResponse("/");
		}
		catch(Exception e)
		{
			ExceptionHandlerTestUtil.Handle(logger, "smokeTestIndexRoot", e);
		}
	}

	// Test "/user" Index
	@Test
	public void smokeTestIndexUser() {
		try {
			_ShouldReturnNotFoundResponse("/user");
		}
		catch(Exception e)
		{
			ExceptionHandlerTestUtil.Handle(logger, "smokeTestIndexUser", e);
		}
	}

	// Test "/manage" Index
	@Test
	void smokeTestIndexManage() {
		try {
			_ShouldReturnNotFoundResponse("/manage");
		}
		catch(Exception e)
		{
			ExceptionHandlerTestUtil.Handle(logger, "smokeTestIndexManage", e);
		}
	}

	// (private function) Should Return Not Found Response
	private void _ShouldReturnNotFoundResponse(String urlTemplate) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(iamServiceResponseCode.CONTENT_NOT_FOUND))
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NOT_FOUND"));
	}

	// Test "/error" Index
	@Test
	public void smokeTestIndexError() throws Exception {
		try {
			mockMvc.perform(MockMvcRequestBuilders.get("/error"))
					.andExpect(MockMvcResultMatchers.status().isNoContent())
					.andExpect(MockMvcResultMatchers.jsonPath("$.count").value(0))
					.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(iamServiceResponseCode.NO_CONTENT))
					.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NOT_FOUND"));
		}
		catch(Exception e)
		{
			ExceptionHandlerTestUtil.Handle(logger, "smokeTestIndexError", e);
		}
	}
}
