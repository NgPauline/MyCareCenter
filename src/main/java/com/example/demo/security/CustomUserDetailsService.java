package com.example.demo.security;

import com.example.demo.model.Employe;
import com.example.demo.model.Soignant;
import com.example.demo.model.Administratif;
import com.example.demo.repository.AdministratifRepository;
import com.example.demo.repository.SoignantRepository;
import com.example.demo.repository.EmployeRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeRepository employeRepository;
    private final SoignantRepository soignantRepository;
    private final AdministratifRepository administratifRepository;

    public CustomUserDetailsService(
            EmployeRepository employeRepository,
            SoignantRepository soignantRepository,
            AdministratifRepository administratifRepository
    ) {
        this.employeRepository = employeRepository;
        this.soignantRepository = soignantRepository;
        this.administratifRepository = administratifRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Chercher dans Employe
        Employe emp = employeRepository.findByMatricule(username);
        if (emp != null) {
            return new User(
                    emp.getMatricule(),
                    emp.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + emp.getRoleApp()))
            );
        }

        // 2. Chercher dans Soignant
        Soignant soi = soignantRepository.findByMatricule(username);
        if (soi != null) {
            return new User(
                    soi.getMatricule(),
                    soi.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + soi.getRoleApp()))
            );
        }

        // 3. Chercher dans Administratif
        Administratif adm = administratifRepository.findByMatricule(username);
        if (adm != null) {
            return new User(
                    adm.getMatricule(),
                    adm.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + adm.getRoleApp()))
            );
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé");
    }
}