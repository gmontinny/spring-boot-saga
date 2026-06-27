package br.com.saga.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {

    @Size(min = 2, message = "Nome deve ter no mínimo 2 caracteres")
    private String name;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    private Boolean enabled;

    private Set<String> roles;
}
