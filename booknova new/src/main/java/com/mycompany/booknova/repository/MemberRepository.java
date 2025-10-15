package com.mycompany.booknova.repository;

import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Member entity.
 * Defines CRUD operations for members.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface MemberRepository {
    
    /**
     * Saves a new member in the database.
     * 
     * @param member the member to save
     * @return the saved member with generated ID
     * @throws DatabaseException if database error occurs
     */
    Member save(Member member) throws DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws DatabaseException if database error occurs
     */
    Member update(Member member) throws DatabaseException;
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the member ID
     * @throws DatabaseException if database error occurs
     */
    void delete(Long id) throws DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return Optional containing the member if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Member> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param memberNumber the member number
     * @return Optional containing the member if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Member> findByMemberNumber(String memberNumber) throws DatabaseException;
    
    /**
     * Finds a member by document ID.
     * 
     * @param documentId the document ID
     * @return Optional containing the member if found
     * @throws DatabaseException if database error occurs
     */
    Optional<Member> findByDocumentId(String documentId) throws DatabaseException;
    
    /**
     * Finds all members.
     * 
     * @return list of all members
     * @throws DatabaseException if database error occurs
     */
    List<Member> findAll() throws DatabaseException;
    
    /**
     * Finds all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database error occurs
     */
    List<Member> findAllActive() throws DatabaseException;
}