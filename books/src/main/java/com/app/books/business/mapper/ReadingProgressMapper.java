package com.app.books.business.mapper;

import com.app.books.business.repository.model.ReadingProgressDAO;
import com.app.books.model.ReadingProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingProgressMapper {
    ReadingProgress daoToProgress(ReadingProgressDAO dao);
    ReadingProgressDAO progressToDAO(ReadingProgress progress);
}
