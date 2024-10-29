package qiwi.test_task.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import qiwi.test_task.dto.UpdateWalletRequestDTO;
import qiwi.test_task.dto.WalletResponseDTO;
import qiwi.test_task.entity.Wallet;
import qiwi.test_task.enums.OperationType;
import qiwi.test_task.exception.InvalidWalletRequestException;
import qiwi.test_task.exception.NotFoundException;
import qiwi.test_task.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WalletService {
    @Autowired
    private WalletRepository repository;

    private boolean isUpdateWalletRequestValid(UpdateWalletRequestDTO updateWalletRequestDTO, BindingResult result) {
        List<String> fields = result.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        if (updateWalletRequestDTO.getUid() == null) {
            fields.add("uid");
        } else {
            try {
                UUID.fromString(updateWalletRequestDTO.getUid());
            } catch (Exception e) {
                fields.add("uid");
            }
        }

        if (updateWalletRequestDTO.getOperationType() == null) {
            fields.add("operationType");
        } else {
            String operationTypeString = updateWalletRequestDTO.getOperationType().toString();
            if (!(operationTypeString.equalsIgnoreCase(OperationType.WITHDRAWAL.toString())
                    || operationTypeString.equalsIgnoreCase(OperationType.DEPOSIT.toString()))) {
                fields.add("operationType");
            }
        }

        if (updateWalletRequestDTO.getAmount() == null) {
            fields.add("amount");
        } else {
            try {
                if (updateWalletRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    fields.add("amount");
                }
            } catch (Exception e) {
                fields.add("amount");
            }
        }

        if (!fields.isEmpty()) {
            log.trace("Error on fields: {}", fields);

            throw new InvalidWalletRequestException("Error on fields: "
                    + Arrays.toString(fields.toArray()));
        }

        return true;
    }

    public void updateWallet(UpdateWalletRequestDTO updateWalletRequestDTO, BindingResult result) {
        if (isUpdateWalletRequestValid(updateWalletRequestDTO, result)) {
            Optional<Wallet> wallet = repository.findById(UUID.fromString(updateWalletRequestDTO.getUid()));

            if (wallet.isPresent()) {
                switch (updateWalletRequestDTO.getOperationType()) {
                    case DEPOSIT -> wallet.get().setBalance(
                            wallet.get().getBalance().add(updateWalletRequestDTO.getAmount()));
                    case WITHDRAWAL -> {
                        BigDecimal balance = wallet.get().getBalance();

                        if (balance.subtract(updateWalletRequestDTO.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new InvalidWalletRequestException("Not enough money on balance.");
                        }

                        wallet.get().setBalance(
                                balance.subtract(updateWalletRequestDTO.getAmount()));
                    }
                }

                repository.save(wallet.get());
            } else {
                throw new NotFoundException("Wallet not found.");
            }
        }
    }

    public WalletResponseDTO getWalletByUid(String walletUid) {
        if (walletUid == null) {
            throw new InvalidWalletRequestException("Invalid UUID.");
        } else {
            try {
                UUID.fromString(walletUid);
            } catch (Exception e) {
                throw new InvalidWalletRequestException("Invalid UUID.");
            }
        }

        Optional<Wallet> wallet = repository.findById(UUID.fromString(walletUid));

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO();

        if (wallet.isPresent()) {
            walletResponseDTO.setBalance(wallet.get().getBalance());
        } else {
            throw new NotFoundException("Wallet not found.");
        }

        return walletResponseDTO;
    }
}
