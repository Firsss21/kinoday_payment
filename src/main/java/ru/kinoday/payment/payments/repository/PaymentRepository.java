package ru.kinoday.payment.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kinoday.payment.payments.entity.Payment;
import ru.kinoday.payment.payments.entity.PaymentStatus;

import java.util.List;
@Repository
@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByPaymentStatus(PaymentStatus status);
}
