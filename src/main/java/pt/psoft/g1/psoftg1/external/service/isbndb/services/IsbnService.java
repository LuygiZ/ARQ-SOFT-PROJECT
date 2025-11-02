package pt.psoft.g1.psoftg1.external.service.isbndb.services;

import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

/**
 * ISBN lookup service using ISBNdb API
 * Requires API key for authentication
 */
public interface IsbnService {

    Isbn searchByTitle(String title);

}