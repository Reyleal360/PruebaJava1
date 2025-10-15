package com.mycompany.booknova.service;

import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.exceptions.*;
import java.util.List;

/**
 * Service interface for Member business logic.
 * Handles member management operations with business validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public interface MemberService {
    
    /**
     * Registers a new member in the system.
     * Generates a unique member number.
     * 
     * @param member the member to register
     * @return the registered member
     * @throws DatabaseException if database error occurs
     */
    Member registerMember(Member member) throws DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    Member updateMember(Member member) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the member ID
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    void deleteMember(Long id) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return the found member
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    Member findMemberById(Long id) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param memberNumber the member number
     * @return the found member
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    Member findMemberByNumber(String memberNumber) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Retrieves all members.
     * 
     * @return list of all members
     * @throws DatabaseException if database error occurs
     */
    List<Member> getAllMembers() throws DatabaseException;
    
    /**
     * Retrieves all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database error occurs
     */
    List<Member> getActiveMembers() throws DatabaseException;
    
    /**
     * Activates a member.
     * 
     * @param memberId the member ID
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    void activateMember(Long memberId) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Deactivates a member.
     * 
     * @param memberId the member ID
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    void deactivateMember(Long memberId) throws MemberNotFoundException, DatabaseException;
    
    /**
     * Validates if a member can borrow books.
     * Checks if member is active.
     * 
     * @param memberId the member ID
     * @return true if member can borrow books
     * @throws MemberNotFoundException if member not found
     * @throws DatabaseException if database error occurs
     */
    boolean canBorrowBooks(Long memberId) throws MemberNotFoundException, DatabaseException;
}