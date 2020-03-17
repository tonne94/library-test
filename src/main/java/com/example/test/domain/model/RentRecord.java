package com.example.test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RENT_RECORD_SEQ")
    @SequenceGenerator(name = "RENT_RECORD_SEQ", sequenceName = "RENT_RECORD_SEQ", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    private BookRecord bookRecord;

    @ManyToOne
    private Account account;

    private LocalDateTime rentTime;

    private LocalDateTime returnTime;

    private LocalDateTime actualReturnTime;

    private boolean overdueDaysPaid = false;

    @Transient
    private Long overdueDays = 0L;

    @PostLoad
    public void calculateOverdueDays() {
        if(actualReturnTime!=null){
            overdueDays = ChronoUnit.DAYS.between(returnTime, getActualReturnTime()) < 0 ? 0 : ChronoUnit.DAYS.between(returnTime, getActualReturnTime());
        }else{
            overdueDays = ChronoUnit.DAYS.between(returnTime, LocalDateTime.now()) < 0 ? 0 : ChronoUnit.DAYS.between(returnTime, LocalDateTime.now());
        }
    }
}
