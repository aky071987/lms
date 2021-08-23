package com.cs.lms.repository;

import com.cs.lms.entity.BookIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, BookIssue.BookIssueId> {
    @Query("select bi from BookIssue bi join bi.user u where u.phoneNumber = ?1 ")
    public List<BookIssue> findBorrowedBookForUser(String phoneNumber);

    @Query("select COUNT(b) from BookIssue b where b.returnedDate is null")
    public Integer findNumberOfBorrowedBooks();

    @Query("select COUNT(b) from BookIssue b join b.user u where u.id = ?1 and b.returnedDate is null")
    public Integer findNumberOfBorrowedBooksByUser(Long userId);
}
