package ru.kinoday.payment.payments.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Transactional
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private Timestamp created;

    @Column
    private Integer price;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "payment_ticket_ids", joinColumns = @JoinColumn(name = "id"))
    private Set<Long> ticketIds;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus paymentStatus;

    @Column
    private Long uid;

    @Column
    private String email;

    public Payment(Integer price, Set<Long> ticketIds, PaymentStatus paymentStatus, Long uid, String email) {
        this.price = price;
        this.ticketIds = ticketIds;
        this.paymentStatus = paymentStatus;
        this.uid = uid;
        this.email = email;
        this.created = new Timestamp(System.currentTimeMillis());
    }

    public PaymentDTO toDto() {
        return new PaymentDTO(
          uid,
          created,
          price,
          getTicketIds(),
          email
        );
    }

    public Set<Long> getTicketIds() {
        Hibernate.initialize(ticketIds);
        return ticketIds;
    }
}

