package com.example.test.interfaces.web.rest;

import com.example.test.domain.model.Author;
import com.example.test.domain.repository.AuthorRepository;
import com.example.test.dto.AuthorDTO;
import com.example.test.interfaces.web.rest.JsonView.AuthorView;
import com.example.test.interfaces.web.rest.mapper.AuthorMapper;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    Logger log = LoggerFactory.getLogger(AccountController.class);

    private AuthorRepository authorRepository;
    private AuthorMapper authorMapper;

    public AuthorController(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    /**
     * Get all authors with books they are associated with
     * @return List of AuthorDTO
     */
    @GetMapping
    @JsonView(AuthorView.class)
    public ResponseEntity<List<AuthorDTO>> getAuthors() {
        log.info("Getting all authors");
        return new ResponseEntity<>(authorMapper.authorsToAuthorDTOsFromAuthor(authorRepository.findAll()), HttpStatus.OK);
    }

    /**
     * Get author by id with books they are associated with
     * @param id id of an author
     * @return AuthorDTO
     */
    @GetMapping("/{id}")
    @JsonView(AuthorView.class)
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable Long id) {
        log.info("Getting author by id: " + id);
        Optional<Author> optionalAuthor = authorRepository.findById(id);

        return optionalAuthor.map(author -> new ResponseEntity<>(authorMapper.authorToAuthorDTOFromAuthor(author), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create new author
     * @param authorDTO
     * @return AccountDTO of newly created Author
     */
    @PostMapping
    @JsonView(AuthorView.class)
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        log.info("Creating new author");
        Author result = authorRepository.save(authorMapper.authorDTOToAuthor(authorDTO));
        return new ResponseEntity<>(authorMapper.authorToAuthorDTOFromAuthor(result), HttpStatus.OK);
    }
}
