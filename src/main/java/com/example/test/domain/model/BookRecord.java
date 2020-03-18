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
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRecord {

    public BookRecord(Book book) {
        this.book = book;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_RECORD_SEQ")
    @SequenceGenerator(name = "BOOK_RECORD_SEQ", sequenceName = "BOOK_RECORD_SEQ", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    private Book book;

    @OneToMany(mappedBy = "bookRecord")
    private List<RentRecord> rentRecords = new ArrayList<>();

    private boolean damaged = false;

    private boolean invalid = false;

    @Transient
    private boolean isAvailable = true;

    @PostLoad
    public void calculateIsAvailable() {
        if (invalid) {
            isAvailable = false;
        } else {
            isAvailable = rentRecords.stream().noneMatch(rentRecord -> rentRecord.getActualReturnTime() == null);
        }
    }

}
