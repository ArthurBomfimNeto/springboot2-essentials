package academy.devdojo.springboot2.config;

import academy.devdojo.springboot2.service.DevDojoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@Log4j2
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * BasicAuthenticationFilter
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginPageGeneratingFilter
     * DefaultLogoutPageGeneratingFilter
     * FilterDecurityInterceptor
     * Authentication -> Authorization
     *
     * @return
     */

    private DevDojoUserService devDojoUserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                 .and()
                .authorizeHttpRequests()
                .requestMatchers("/animes/admin/**").hasRole("ADMIN")
                .requestMatchers("/animes/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();

        return http.build();
    }

    //    @Bean
//    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder)  {
////        UserDetails user = User.withDefaultPasswordEncoder()
////                .username("arthur")
////                .password("teste1")
////                .roles("USER", "ADMIN")
////                .build();
////
////        UserDetails user2 = User.withDefaultPasswordEncoder()
////                .username("rafaela")
////                .password("teste2")
////                .roles("USER")
////                .build();
////        log.info("Password encoded {}", user2.getPassword());  //-- Log para verificar que o password realmente esta criptografado
//
//        String encodedPassword1 = passwordEncoder.encode("teste1");
//        String encodedPassword2 = passwordEncoder.encode("teste2");
//
//        UserDetails user = User.withUsername("arthur")
//                .password(encodedPassword1)
//                .roles("USER", "ADMIN")
//                .build();
//
//        UserDetails user2 = User.withUsername("rafaela")
//                .password(encodedPassword2)
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, user2);
//    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       DevDojoUserService devDojoUserService) throws Exception {

        log.info("Password encoded {}", passwordEncoder.encode("academy"));

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(devDojoUserService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

}
