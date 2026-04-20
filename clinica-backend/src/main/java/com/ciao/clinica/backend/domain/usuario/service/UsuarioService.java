package com.ciao.clinica.backend.domain.usuario.service;

import com.ciao.clinica.backend.domain.usuario.entity.Rol;
import com.ciao.clinica.backend.domain.usuario.entity.Usuario;
import com.ciao.clinica.backend.domain.usuario.repository.RolRepository;
import com.ciao.clinica.backend.domain.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ciao.clinica.backend.domain.common.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // ==============================
    // Métodos básicos
    // ==============================

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // ==============================
    // Seguridad / Login
    // ==============================

    @Transactional
    public void resetearIntentos(Usuario usuario) {
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarIntentoFallido(String username, int maxIntentos) {

        usuarioRepository.findByUsername(username).ifPresent(usuario -> {

            // Si ya está bloqueado, no hacemos nada
            if (!usuario.getCuentaNoBloqueada()) {
                return;
            }

            int nuevosIntentos = usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(nuevosIntentos);

            if (nuevosIntentos >= maxIntentos) {
                usuario.setCuentaNoBloqueada(false);
            }

            usuarioRepository.save(usuario);
        });
    }

    // ==============================
    // Admin
    // ==============================

    @Transactional
    public Usuario desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setCuentaNoBloqueada(true);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> listarUsuarios(
            String search,
            Boolean activo,
            Boolean bloqueado,
            String rol,
            Pageable pageable) {

        return usuarioRepository.findAll((root, query, cb) -> {
            query.distinct(true);
            var predicates = cb.conjunction();

            // 🔎 search (username o email)
            if (search != null && !search.trim().isEmpty()) {
                var like = "%" + search.toLowerCase() + "%";

                var usernamePredicate = cb.like(cb.lower(root.get("username")), like);
                var emailPredicate = cb.like(cb.lower(root.get("email")), like);

                predicates = cb.and(predicates, cb.or(usernamePredicate, emailPredicate));
            }

            // ✅ activo
            if (activo != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), activo));
            }

            // 🔒 bloqueado (inverso de cuentaNoBloqueada)
            if (bloqueado != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("cuentaNoBloqueada"), !bloqueado));
            }

            // 🎭 rol
            if (rol != null && !rol.isEmpty()) {
                var joinRoles = root.join("roles");
                predicates = cb.and(predicates,
                        cb.equal(joinRoles.get("nombre"), rol));
            }

            return predicates;

        }, pageable);
    }

    public Page<Usuario> buscarUsuarios(String search, Pageable pageable) {

        if (search == null || search.trim().isEmpty()) {
            return usuarioRepository.findAll(pageable);
        }

        return usuarioRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search,
                        search,
                        pageable);
    }

    public List<Usuario> listarBloqueados() {
        return usuarioRepository.findByCuentaNoBloqueadaFalse();
    }

    @Transactional
    public void asignarRoles(Long usuarioId, Set<String> nombresRoles) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<Rol> roles = rolRepository.findAll()
                .stream()
                .filter(r -> nombresRoles.contains(r.getNombre()))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario crearUsuario(
            String username,
            String email,
            String password,
            Set<String> roles) {

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));

        Set<Rol> rolesEncontrados = rolRepository.findAll()
                .stream()
                .filter(r -> roles.contains(r.getNombre()))
                .collect(Collectors.toSet());

        usuario.setRoles(rolesEncontrados);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(
            Long id,
            String email,
            Boolean activo) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow();

        usuario.setEmail(email);
        usuario.setActivo(activo);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {

        usuarioRepository.deleteById(id);

    }
}