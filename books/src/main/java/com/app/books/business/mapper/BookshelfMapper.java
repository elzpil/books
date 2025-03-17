package com.app.books.business.mapper;

import com.app.books.business.repository.model.BookshelfDAO;
import com.app.books.model.BookshelfEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookshelfMapper {
    BookshelfEntry bookshelfDAOToBookshelfEntry(BookshelfDAO bookshelfDAO);
    BookshelfDAO bookshelfEntryToBookshelfDAO(BookshelfEntry bookshelfEntry);
}
