package paengbeom.syono.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import paengbeom.syono.dto.SecurityUserDto;

import java.util.Collection;
import java.util.Collections;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "email", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(target = "authorities", expression = "java(mapRoleToAuthorities(user.getRole()))")
    SecurityUserDto userToSecurityUserDto(User user);

    default Collection<? extends GrantedAuthority> mapRoleToAuthorities(Role role) {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }
}
