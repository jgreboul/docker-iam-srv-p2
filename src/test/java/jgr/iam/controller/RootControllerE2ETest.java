package jgr.iam.controller;

// External Objects
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Internal Objects
import jgr.iam.constant.iamServiceResponseCode;
import jgr.iam.payload.response.RootDefaultResponse;

// RootController Test
public class RootControllerE2ETest {

    private RootController rootController;

    @BeforeEach
    void Setup() {
        rootController = new RootController();
    }

    @Test
    void smokeTestIndexRoot() {
        _ShouldReturnNotFoundResponse(rootController.indexRoot());
    }

    @Test
    void smokeTestIndexUser() {
        _ShouldReturnNotFoundResponse(rootController.indexUser());
    }

    @Test
    void smokeTestIndexManage() {
        _ShouldReturnNotFoundResponse(rootController.indexManage());
    }

    // (private function) Should Return Not Found Response
    private void _ShouldReturnNotFoundResponse(ResponseEntity<RootDefaultResponse> response)
    {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(iamServiceResponseCode.CONTENT_NOT_FOUND, response.getBody().getResponse());
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().getStatus());
    }
}
