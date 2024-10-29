package qiwi.test_task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qiwi.test_task.entity.Wallet;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
