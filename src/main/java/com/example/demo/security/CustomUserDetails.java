package com.example.demo.security;

import com.example.demo.model.Employe;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Employe employe;

    public CustomUserDetails(Employe employe) {
        this.employe = employe;
    }

    public String getNom() {
        return employe.getNom();
    }

    public String getPrenom() {
        return employe.getPrenom();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + employe.getRoleApp());
    }

    @Override
    public String getPassword() {
        return employe.getPassword();
    }

    @Override
    public String getUsername() {
        return employe.getMatricule();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Employe getEmploye() {
        return employe;
    }
}
