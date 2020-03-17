package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.Author;
import com.example.test.dto.AuthorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface AuthorMapper {

    Author authorDTOToAuthor(AuthorDTO authorDTO);

    AuthorDTO authorToAuthorDTO(Author author);

    List<AuthorDTO> authorsToAuthorDTOs(List<Author> authors);

    @FromBook
    @FromAccount
    @Mapping(target = "books", ignore = true)
    AuthorDTO authorToAuthorDTOFromAccount(Author author);

    @FromBook
    @FromAccount
    default List<AuthorDTO> authorsToAuthorDTOsFromAccount(List<Author> authors) {
        if (authors == null) {
            return null;
        }
        List<AuthorDTO> resultList = new ArrayList<>();
        for (Author author : authors) {
            resultList.add(authorToAuthorDTOFromAccount(author));
        }
        return resultList;
    }

    @FromAuthor
    @Mapping(target = "books", qualifiedBy = FromAuthor.class)
    AuthorDTO authorToAuthorDTOFromAuthor(Author author);

    @FromAuthor
    default List<AuthorDTO> authorsToAuthorDTOsFromAuthor(List<Author> authors) {
        if (authors == null) {
            return null;
        }
        List<AuthorDTO> resultList = new ArrayList<>();
        for (Author author : authors) {
            resultList.add(authorToAuthorDTOFromAuthor(author));
        }
        return resultList;
    }
}
