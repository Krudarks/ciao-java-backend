package com.ciao.clinica.backend.domain.usuario.service;

import com.ciao.clinica.backend.domain.usuario.entity.Rol;
import com.ciao.clinica.backend.domain.usuario.entity.Usuario;
import com.ciao.clinica.backend.domain.usuario.repository.RolRepository;
import com.ciao.clinica.backend.domain.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    // ==============================
    // Métodos básicos
    // ==============================

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // ==============================
    // Seguridad / Login
    // ==============================

    public void resetearIntentos(Usuario usuario) {
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
    }

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

    public Usuario desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        usuario.setCuentaNoBloqueada(true);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarBloqueados() {
        return usuarioRepository.findByCuentaNoBloqueadaFalse();
    }

    public void asignarRoles(Long usuarioId, Set<String> nombresRoles) {

    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado"));

    Set<Rol> roles = rolRepository.findAll()
            .stream()
            .filter(r -> nombresRoles.contains(r.getNombre()))
            .collect(Collectors.toSet());

    usuario.setRoles(roles);

    usuarioRepository.save(usuario);
}
}