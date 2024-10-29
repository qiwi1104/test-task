package qiwi.test_task.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletResponseDTO {
    private BigDecimal balance;
}
