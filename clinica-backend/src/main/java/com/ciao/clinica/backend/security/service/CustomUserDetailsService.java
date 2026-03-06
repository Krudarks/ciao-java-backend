package com.ciao.clinica.backend.security.service;

import com.ciao.clinica.backend.domain.usuario.entity.Usuario;
import com.ciao.clinica.backend.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!usuario.getActivo())
                .accountLocked(!usuario.getCuentaNoBloqueada())
                .accountExpired(!usuario.getCuentaNoExpirada())
                .credentialsExpired(!usuario.getCredencialesNoExpiradas())
                .authorities(
                        usuario.getRoles().stream()
                                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                                .toList())
                .build();
    }
}