package com.example.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Size(max = 80)
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;

    /**
     * fetch = FetchType.EAGER: Indica que al consultar algun usuario, nos obtiene de igual forma sus roles
     * targetEntity = RoleEntity.class: Indica con cual clase entity vamos a realizar al relacion
     * cascade (Opcional) = CascadeType.PERSIST:  las operaciones de guardado en la base de datos de las entidades padre se propagarán a las entidades relacionadas.
     *           Si, se crea un usuario, en automatico se crea el rol en la tabla roles pero este rol no se elimina si el usuario es eliminado
     *           (Es opcional hacerlo asi, por que podemos nosotros crear
     *           los roles en la tabla manual y validar el rol que el usuario elija.
     */
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    // Crea tabla intermedia user_roles, con los campos user_id, role_id
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
}
