package qiwi.test_task.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import qiwi.test_task.dto.ErrorMessageDTO;
import qiwi.test_task.dto.UpdateWalletRequestDTO;
import qiwi.test_task.dto.WalletResponseDTO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WalletControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper mapper;

    private UpdateWalletRequestDTO updateWalletSuccessRequest;
    private WalletResponseDTO expectedUpdateWalletSuccessResponse;

    private UpdateWalletRequestDTO expectedUpdateWalletNotFoundRequest;

    private WalletResponseDTO expectedGetWalletSuccessResponse;

    @Test
    void testUpdateBalanceSuccess() {
        ResponseEntity<WalletResponseDTO> updateWalletResponse = restTemplate.postForEntity(
                "/api/v1/wallet",
                updateWalletSuccessRequest,
                WalletResponseDTO.class
        );

        BigDecimal expected = expectedUpdateWalletSuccessResponse.getBalance().stripTrailingZeros();
        BigDecimal actual = updateWalletResponse.getBody().getBalance().stripTrailingZeros();

        assertTrue(updateWalletResponse.getStatusCode().is2xxSuccessful());
        assertEquals(expected, actual);
    }

    @Test
    void testUpdateBalanceNotFound() {
        ResponseEntity<ErrorMessageDTO> updateWalletResponse = restTemplate.postForEntity(
                "/api/v1/wallet",
                expectedUpdateWalletNotFoundRequest,
                ErrorMessageDTO.class
        );

        ErrorMessageDTO expected = new ErrorMessageDTO("Error on fields: [uid]");
        ErrorMessageDTO actual = updateWalletResponse.getBody();

        assertTrue(updateWalletResponse.getStatusCode().is4xxClientError());
        assertEquals(expected, actual);
    }

    @Test
    void testGetWalletSuccess() {
        ResponseEntity<WalletResponseDTO> getWalletResponse = restTemplate.getForEntity(
                "/api/v1/wallets/" + updateWalletSuccessRequest.getUid(),
                WalletResponseDTO.class
        );

        assertTrue(getWalletResponse.getStatusCode().is2xxSuccessful());
        assertEquals(expectedGetWalletSuccessResponse, getWalletResponse.getBody());
    }

    @Test
    void testGetWalletNotFound() {
        ResponseEntity<ErrorMessageDTO> getWalletResponse = restTemplate.getForEntity(
                "/api/v1/wallets/" + updateWalletSuccessRequest.getUid().substring(0, 25),
                ErrorMessageDTO.class
        );

        ErrorMessageDTO expectedGetWalletNotFoundResponse = new ErrorMessageDTO("Wallet not found.");

        assertTrue(getWalletResponse.getStatusCode().is4xxClientError());
        assertEquals(expectedGetWalletNotFoundResponse, getWalletResponse.getBody());
    }

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("update-wallet/update_wallet_request.json").getFile());
        updateWalletSuccessRequest = mapper.readValue(file, UpdateWalletRequestDTO.class);

        file = new File(classLoader.getResource("update-wallet/update_wallet_response.json").getFile());
        expectedUpdateWalletSuccessResponse = mapper.readValue(file, WalletResponseDTO.class);

        file = new File(classLoader.getResource("update-wallet/update_wallet_not_found_request.json").getFile());
        expectedUpdateWalletNotFoundRequest = mapper.readValue(file, UpdateWalletRequestDTO.class);

        file = new File(classLoader.getResource("get-wallet/expected_get_wallet_response.json").getFile());
        expectedGetWalletSuccessResponse = mapper.readValue(file, WalletResponseDTO.class);
    }
}