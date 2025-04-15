package com.app.books.business.service;

public interface EmailService {

    void verify(Long bookId, String token);
}
