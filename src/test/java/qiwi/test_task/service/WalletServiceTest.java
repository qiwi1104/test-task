package qiwi.test_task.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindingResult;
import qiwi.test_task.dto.UpdateWalletRequestDTO;
import qiwi.test_task.dto.WalletResponseDTO;
import qiwi.test_task.enums.OperationType;
import qiwi.test_task.exception.InvalidWalletRequestException;
import qiwi.test_task.exception.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WalletServiceTest {
    @Autowired
    private WalletService service;

    private ObjectMapper mapper;

    private UpdateWalletRequestDTO updateWalletDepositRequest;
    private WalletResponseDTO expectedUpdateWalletDepositResponse;

    private WalletResponseDTO expectedUpdateWalletWithdrawalSuccessResponse;

    private UpdateWalletRequestDTO walletRequest;
    private WalletResponseDTO expectedGetWalletSuccessResponse;

    @Test
    @Order(2)
    void updateWalletDepositSuccess() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        service.updateWallet(updateWalletDepositRequest, result);

        WalletResponseDTO response = service.getWalletByUid(updateWalletDepositRequest.getUid());

        assertEquals(expectedUpdateWalletDepositResponse.getBalance().stripTrailingZeros(),
                response.getBalance().stripTrailingZeros());
    }

    @Test
    @Order(3)
    void updateWalletWithdrawalSuccess() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setOperationType(OperationType.WITHDRAWAL);
        updateWalletDepositRequest.setAmount(new BigDecimal("2000"));

        service.updateWallet(updateWalletDepositRequest, result);

        WalletResponseDTO response = service.getWalletByUid(updateWalletDepositRequest.getUid());

        assertEquals(expectedUpdateWalletWithdrawalSuccessResponse.getBalance().stripTrailingZeros(),
                response.getBalance().stripTrailingZeros());
    }

    @Test
    void updateWalletWithdrawalFail() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setOperationType(OperationType.WITHDRAWAL);
        updateWalletDepositRequest.setAmount(new BigDecimal("10000"));

        assertThrows(InvalidWalletRequestException.class,
                () -> service.updateWallet(updateWalletDepositRequest, result));
    }

    @Test
    void updateWalletNull() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setUid(null);
        updateWalletDepositRequest.setOperationType(null);
        updateWalletDepositRequest.setAmount(null);

        assertThrows(InvalidWalletRequestException.class,
                () -> service.updateWallet(updateWalletDepositRequest, result));
    }

    @Test
    void updateWalletInvalidUid() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setUid(updateWalletDepositRequest.getUid().substring(0, 25));

        assertThrows(NotFoundException.class,
                () -> service.updateWallet(updateWalletDepositRequest, result));
    }

    @Test
    void updateWalletInvalidAmount() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setAmount(BigDecimal.ZERO);

        assertThrows(InvalidWalletRequestException.class,
                () -> service.updateWallet(updateWalletDepositRequest, result));
    }

    @Test
    void updateWalletNotFound() {
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        updateWalletDepositRequest.setUid(walletRequest.getUid().substring(0, 25));

        assertThrows(NotFoundException.class,
                () -> service.updateWallet(updateWalletDepositRequest, result));
    }

    @Test
    @Order(1)
    void getWalletSuccess() {
        WalletResponseDTO actualResponse = service.getWalletByUid(walletRequest.getUid());

        assertEquals(expectedGetWalletSuccessResponse.getBalance().stripTrailingZeros(),
                actualResponse.getBalance().stripTrailingZeros());
    }

    @Test
    void getWalletNotFound() {
        assertThrows(NotFoundException.class,
                () -> service.getWalletByUid(walletRequest.getUid().substring(0, 25)));
    }

    @Test
    void getWalletInvalidUid() {
        assertThrows(InvalidWalletRequestException.class,
                () -> service.getWalletByUid(null));
    }

    @Test
    void getWalletInvalidUid2() {
        assertThrows(InvalidWalletRequestException.class,
                () -> service.getWalletByUid("d13"));
    }

    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("update-wallet/update_wallet_request.json").getFile());
        walletRequest = mapper.readValue(file, UpdateWalletRequestDTO.class);

        file = new File(classLoader.getResource("update-wallet/update_wallet_deposit_request.json").getFile());
        updateWalletDepositRequest = mapper.readValue(file, UpdateWalletRequestDTO.class);

        file = new File(classLoader.getResource("update-wallet/update_wallet_deposit_response.json").getFile());
        expectedUpdateWalletDepositResponse = mapper.readValue(file, WalletResponseDTO.class);

        file = new File(classLoader.getResource("update-wallet/update_wallet_withdrawal_success_response.json").getFile());
        expectedUpdateWalletWithdrawalSuccessResponse = mapper.readValue(file, WalletResponseDTO.class);

        file = new File(classLoader.getResource("get-wallet/expected_get_wallet_response.json").getFile());
        expectedGetWalletSuccessResponse = mapper.readValue(file, WalletResponseDTO.class);
    }
}
