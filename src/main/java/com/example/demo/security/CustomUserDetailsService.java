package com.example.demo.security;

import com.example.demo.model.Employe;
import com.example.demo.repository.EmployeRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeRepository employeRepository;

    public CustomUserDetailsService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employe emp = employeRepository.findByMatricule(username);

        if (emp == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        return new CustomUserDetails(emp);
    }
}