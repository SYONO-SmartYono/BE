package paengbeom.syono.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import paengbeom.syono.dto.SecurityUserDto;
import paengbeom.syono.entity.User;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class UserToSecurityUserDto implements Converter<User, SecurityUserDto> {

    @Override
    public SecurityUserDto convert(User user) {
        log.info("convert user={}", user);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        log.info(user.getRole().name());

        return new SecurityUserDto(user.getEmail(), user.getPassword(), authorities);
    }
}
