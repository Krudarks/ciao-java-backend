package com.ciao.clinica.backend.api.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ciao.clinica.backend.api.admin.dto.RolRequest;
import com.ciao.clinica.backend.api.admin.dto.UsuarioResponse;
import com.ciao.clinica.backend.api.admin.dto.UsuarioRolesRequest;
import com.ciao.clinica.backend.api.admin.mapper.UsuarioMapper;
import com.ciao.clinica.backend.domain.usuario.entity.Rol;
import com.ciao.clinica.backend.domain.usuario.service.RolService;
import com.ciao.clinica.backend.domain.usuario.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final RolService rolService;

    // 🔓 Desbloquear usuario
    @PutMapping("/usuarios/{id}/unlock")
    public ResponseEntity<String> desbloquearUsuario(@PathVariable Long id) {

        usuarioService.desbloquearUsuario(id);

        return ResponseEntity.ok("Usuario desbloqueado correctamente");
    }

    // 📋 Listar todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {

        List<UsuarioResponse> usuarios = usuarioService.listarTodos()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    // 🚫 Listar usuarios bloqueados
    @GetMapping("/usuarios/bloqueados")
    public ResponseEntity<List<UsuarioResponse>> listarUsuariosBloqueados() {

        List<UsuarioResponse> bloqueados = usuarioService.listarBloqueados()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();

        return ResponseEntity.ok(bloqueados);
    }

    @PutMapping("/usuarios/{id}/roles")
    public ResponseEntity<String> asignarRoles(
            @PathVariable Long id,
            @RequestBody UsuarioRolesRequest request) {

        usuarioService.asignarRoles(id, request.getRoles());

        return ResponseEntity.ok("Roles actualizados correctamente");
    }
    
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<Rol> crearRol(@RequestBody RolRequest request) {

        Rol rol = rolService.crearRol(
                request.getNombre(),
                request.getDescripcion());

        return ResponseEntity.ok(rol);
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<Rol> actualizarRol(
            @PathVariable Long id,
            @RequestBody RolRequest request) {

        Rol rol = rolService.actualizarRol(
                id,
                request.getDescripcion());

        return ResponseEntity.ok(rol);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {

        rolService.eliminarRol(id);

        return ResponseEntity.ok("Rol eliminado");
    }
    
}