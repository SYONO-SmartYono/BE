package paengbeom.syono.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import paengbeom.syono.dto.SecurityUserDto;
import paengbeom.syono.dto.SignUpRequestDto;

import java.util.Collection;
import java.util.Collections;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "email", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "authorities", qualifiedByName = "mapRoleToAuthorities")
    SecurityUserDto userToSecurityUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password", qualifiedByName = "encryptPassword")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "connectedId", target = "connectedId")
    @Mapping(target = "nickname", expression = "java(\"defaultNickname\")")
    @Mapping(target = "role", expression = "java(Role.ROLE_USER)")
    @Mapping(target = "profileImg", expression = "java(\"defaultProfileImg\")")
    @Mapping(target = "lastLogin", ignore = true)
    User userFormDtoToUser(SignUpRequestDto signUpRequestDto);

    @Named("mapRoleToAuthorities")
    default Collection<? extends GrantedAuthority> mapRoleToAuthorities(Role role) {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Named("encryptPassword")
    default String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
