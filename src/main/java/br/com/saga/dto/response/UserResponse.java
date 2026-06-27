package br.com.saga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class UserResponse extends RepresentationModel<UserResponse> {
    private Long id;
    private String name;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private Set<String> roles;
}
