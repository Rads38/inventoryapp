package com.retail.inventory.security;
import java.util.ArrayList;import java.util.Collection;import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;import org.springframework.context.annotation.Bean;import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;import org.springframework.security.core.GrantedAuthority;import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.oauth2.jwt.Jwt;import org.springframework.security.web.SecurityFilterChain;
@AutoConfiguration @EnableMethodSecurity
public class ServiceSecurityAutoConfiguration {
 @Bean SecurityFilterChain serviceSecurityFilterChain(HttpSecurity http)throws Exception{
  http.csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"))
   .headers(headers->headers.frameOptions(frame->frame.sameOrigin()))
   .authorizeHttpRequests(auth->auth.requestMatchers("/actuator/health").permitAll()
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers("/inventory/**").hasAnyRole("INVENTORY_SERVICE","ORDER_SERVICE","ADMIN")
    .anyRequest().authenticated())
   .oauth2ResourceServer(oauth2->oauth2.jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
  return http.build();
 }
 @Bean JwtAuthenticationConverter jwtAuthenticationConverter(){
  JwtGrantedAuthoritiesConverter scopes=new JwtGrantedAuthoritiesConverter();
  JwtAuthenticationConverter converter=new JwtAuthenticationConverter();
  converter.setJwtGrantedAuthoritiesConverter(jwt->{Collection<GrantedAuthority> authorities=new ArrayList<>(scopes.convert(jwt));List<String> roles=jwt.getClaimAsStringList("roles");if(roles!=null)roles.forEach(role->authorities.add(new SimpleGrantedAuthority("ROLE_"+role)));return authorities;});
  return converter;
 }
}