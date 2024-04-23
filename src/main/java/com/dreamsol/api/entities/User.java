package com.dreamsol.api.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "User")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    @Column(length = 20, nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departmentId")
    private Department department;
    @Column(nullable = false, unique = true)
    private long mobile;
    @Column(length = 50, nullable = false, unique = true)
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserType")
    private UserType usertype;
    @Column(columnDefinition = "boolean default true")
    private boolean status;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fileId")
    private UserFile file;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="authorizedEndPoints",joinColumns = @JoinColumn(name="userId"),inverseJoinColumns = @JoinColumn(name="endPointId"))
    private List<EndPoint> AuthorizedEndpoints;
    private String password;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permssion")
    private UserPermission permission;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usertype.getUserTypeName()),new SimpleGrantedAuthority(permission.getPermission()));
    }
    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
          return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
