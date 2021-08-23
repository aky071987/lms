package com.cs.lms.entity;

import com.cs.lms.constant.LmsConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "lms_book_issue")
public class BookIssue {
    @EmbeddedId
    @Column(name = "id")
    private BookIssueId id;
    @ManyToOne
    @JoinColumn(name = "book", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Book book;
    @ManyToOne
    @JoinColumn(name = "user", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Max(value = 25, message = "Book can not be issued for more than 25 days")
    @Min(value = 1, message = "Book must be issued for at least 1 day")
    @Column(name = "borrow_period", nullable = false)
    private int period;
    @Column(name = "borrowed_date")
    private Date borrowedDate;
    @Column(name = "returned_date")
    private Date returnedDate;

    public BookIssue(){}
    public BookIssue(Book b, User u, int period) {
        this.id = new BookIssueId(b.getId(), u.getId());
        this.book = b;
        this.user = u;
        this.period = period;
        this.borrowedDate = new Date();
    }

    public BookIssueId getId() {
        return id;
    }

    public void setId(BookIssueId id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Date getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(Date borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    @Embeddable
    public static class BookIssueId implements Serializable {
        @Column(name = "book")
        protected Long bookId;

        @Column(name = "user")
        protected Long userId;
        @Column(name = "issue_date")
        protected String issueDate;

        public BookIssueId() {
        }

        public BookIssueId(Long bookId, Long userId) {
            this.bookId = bookId;
            this.userId = userId;
            this.issueDate = LmsConstant.SDF_YYYYMMDDHHMMSS.format(new Date());
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(String issueDate) {
            this.issueDate = issueDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookIssueId that = (BookIssueId) o;
            return bookId.equals(that.bookId) && userId.equals(that.userId) && issueDate.equals(that.issueDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookId, userId, issueDate);
        }
    }
}
