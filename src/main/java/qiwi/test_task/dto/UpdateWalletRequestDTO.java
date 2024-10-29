package qiwi.test_task.dto;

import lombok.Data;
import qiwi.test_task.enums.OperationType;

import java.math.BigDecimal;

@Data
public class UpdateWalletRequestDTO {
    private String uid;
    private OperationType operationType;
    private BigDecimal amount;
}
