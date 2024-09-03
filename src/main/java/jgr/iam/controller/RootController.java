package jgr.iam.controller;

// External Objects
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Internal Objects
import jgr.iam.constant.iamServiceResponseCode;
import jgr.iam.payload.response.RootDefaultResponse;

@RestController
public class RootController {

	// Logger
	private final static Logger logger = LogManager.getLogger(RootController.class.getCanonicalName());

	// As a User, I want to access the Service Root
	@GetMapping("/")
	public ResponseEntity<RootDefaultResponse> indexRoot() {
		return getNoContent();
	}

	// As a User, I want to access the User Root
	@GetMapping("/user")
	public ResponseEntity<RootDefaultResponse> indexUser() {
		return getNoContent();
	}

	// As a User, I want to access the Administration Root
	@GetMapping("/manage")
	public ResponseEntity<RootDefaultResponse> indexManage() {
		return getNoContent();
	}

	// Return No Content
	private ResponseEntity<RootDefaultResponse> getNoContent() {
		return new ResponseEntity<>(new RootDefaultResponse(iamServiceResponseCode.CONTENT_NOT_FOUND), HttpStatus.NOT_FOUND);
	}

	// As a User, I want to get an Error message
	@GetMapping("/error")
	public ResponseEntity<RootDefaultResponse> index() {
		return new ResponseEntity<>(new RootDefaultResponse(iamServiceResponseCode.NO_CONTENT), HttpStatus.NO_CONTENT);
	}
}
