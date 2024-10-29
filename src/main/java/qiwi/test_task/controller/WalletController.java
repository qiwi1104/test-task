package qiwi.test_task.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import qiwi.test_task.dto.UpdateWalletRequestDTO;
import qiwi.test_task.dto.WalletResponseDTO;
import qiwi.test_task.service.WalletService;

@RestController
public class WalletController {
    @Autowired
    private WalletService service;

    @PostMapping("/api/v1/wallet")
    public ResponseEntity<WalletResponseDTO> updateBalance(
            @Valid @RequestBody UpdateWalletRequestDTO updateWalletRequestDTO,
            BindingResult result) {
        service.updateWallet(updateWalletRequestDTO, result);

        return ResponseEntity.ok(service.getWalletByUid(updateWalletRequestDTO.getUid()));
    }

    @GetMapping("/api/v1/wallets/{walletUid}")
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable String walletUid) {
        return ResponseEntity.ok(service.getWalletByUid(walletUid));
    }
}
