/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.booknova.domain;

/**
 *
 * @author Coder
 */
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a Library Member.
 * Members can borrow books from the library.
 * 
 * @author LibroNova Team
 * @version 1.0
 */
public class Member {
    private Long id;
    private String memberNumber;
    private String firstName;
    private String lastName;
    private String documentId;
    private String email;
    private String phone;
    private String address;
    private LocalDate registrationDate;
    private Boolean active;
    private MembershipType membershipType;
    
    public enum MembershipType {
        BASIC(3),
        PREMIUM(5),
        VIP(10);
        
        private final int maxLoans;
        
        MembershipType(int maxLoans) {
            this.maxLoans = maxLoans;
        }
        
        public int getMaxLoans() {
            return maxLoans;
        }
    }
    
    public Member() {
    }
    
    public Member(Long id, String memberNumber, String firstName, String lastName,
                  String documentId, String email, String phone, String address,
                  LocalDate registrationDate, Boolean active, MembershipType membershipType) {
        this.id = id;
        this.memberNumber = memberNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = documentId;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.active = active;
        this.membershipType = membershipType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { 
        this.registrationDate = registrationDate; 
    }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public MembershipType getMembershipType() { return membershipType; }
    public void setMembershipType(MembershipType membershipType) { 
        this.membershipType = membershipType; 
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isActive() {
        return active != null && active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(memberNumber, member.memberNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(memberNumber);
    }
}
