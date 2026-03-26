package com.ciao.clinica.backend.domain.usuario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ciao.clinica.backend.domain.usuario.entity.Rol;
import com.ciao.clinica.backend.domain.usuario.repository.RolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Rol crearRol(String nombre, String descripcion) {

        rolRepository.findByNombre(nombre)
                .ifPresent(r -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "El rol ya existe");
                });

        Rol rol = Rol.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        return rolRepository.save(rol);
    }

    public Rol actualizarRol(Long id, String descripcion) {

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado"));

        if (rol.getSystemRole()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede modificar un rol del sistema");
        }

        rol.setDescripcion(descripcion);

        return rolRepository.save(rol);
    }

    public void eliminarRol(Long id) {

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rol no encontrado"));

        if (rol.getSystemRole()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede eliminar un rol del sistema");
        }

        rolRepository.delete(rol);
    }
}