package com.mycompany.booknova.service.impl;

import com.mycompany.booknova.domain.Member;
import com.mycompany.booknova.exceptions.*;
import com.mycompany.booknova.repository.MemberRepository;
import com.mycompany.booknova.repository.jdbc.MemberRepositoryJdbc;
import com.mycompany.booknova.service.MemberService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of MemberService.
 * Handles member business logic and validations.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    
    public MemberServiceImpl() {
        this.memberRepository = new MemberRepositoryJdbc();
    }
    
    @Override
    public Member registerMember(Member member) throws DatabaseException {
        validateMember(member);
        
        // Generate unique member number
        if (member.getMemberNumber() == null || member.getMemberNumber().trim().isEmpty()) {
            member.setMemberNumber(generateMemberNumber());
        }
        
        // Set registration date if not set
        if (member.getRegistrationDate() == null) {
            member.setRegistrationDate(LocalDate.now());
        }
        
        // Set active by default
        if (member.getActive() == null) {
            member.setActive(true);
        }
        
        return memberRepository.save(member);
    }
    
    @Override
    public Member updateMember(Member member) throws MemberNotFoundException, DatabaseException {
        validateMember(member);
        
        // Verify member exists
        memberRepository.findById(member.getId())
                .orElseThrow(() -> new MemberNotFoundException(member.getId()));
        
        return memberRepository.update(member);
    }
    
    @Override
    public void deleteMember(Long id) throws MemberNotFoundException, DatabaseException {
        // Verify member exists
        memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        
        memberRepository.delete(id);
    }
    
    @Override
    public Member findMemberById(Long id) throws MemberNotFoundException, DatabaseException {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }
    
    @Override
    public Member findMemberByNumber(String memberNumber) throws MemberNotFoundException, DatabaseException {
        return memberRepository.findByMemberNumber(memberNumber)
                .orElseThrow(() -> new MemberNotFoundException(memberNumber));
    }
    
    @Override
    public List<Member> getAllMembers() throws DatabaseException {
        return memberRepository.findAll();
    }
    
    @Override
    public List<Member> getActiveMembers() throws DatabaseException {
        return memberRepository.findAllActive();
    }
    
    @Override
    public void activateMember(Long memberId) throws MemberNotFoundException, DatabaseException {
        Member member = findMemberById(memberId);
        member.setActive(true);
        memberRepository.update(member);
    }
    
    @Override
    public void deactivateMember(Long memberId) throws MemberNotFoundException, DatabaseException {
        Member member = findMemberById(memberId);
        member.setActive(false);
        memberRepository.update(member);
    }
    
    @Override
    public boolean canBorrowBooks(Long memberId) throws MemberNotFoundException, DatabaseException {
        Member member = findMemberById(memberId);
        return member.isActive();
    }
    
    /**
     * Generates a unique member number.
     * Format: MEM-YYYYMMDD-XXXX
     * 
     * @return the generated member number
     */
    private String generateMemberNumber() {
        String date = LocalDate.now().toString().replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "MEM-" + date + "-" + random;
    }
    
    /**
     * Validates member data.
     * 
     * @param member the member to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (member.getFirstName() == null || member.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (member.getLastName() == null || member.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (member.getDocumentId() == null || member.getDocumentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Document ID is required");
        }
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (member.getMembershipType() == null) {
            throw new IllegalArgumentException("Membership type is required");
        }
    }
}