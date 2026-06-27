package br.com.saga.mapper;

import br.com.saga.domain.entity.RoleEntity;
import br.com.saga.domain.entity.UserEntity;
import br.com.saga.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(UserEntity user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleEntity> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(RoleEntity::getName).collect(Collectors.toSet());
    }
}
