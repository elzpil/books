package com.app.books.business.mapper;

import com.app.books.business.repository.model.BookDAO;
import com.app.books.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book bookDAOToBook(BookDAO bookDAO);
    BookDAO bookToBookDAO(Book book);
}
