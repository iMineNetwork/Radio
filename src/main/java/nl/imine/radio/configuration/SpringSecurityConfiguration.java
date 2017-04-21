package nl.imine.radio.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${ldap.url}")
    private String ldapUrl;
    @Value("${ldap.baseDn}")
    private String ldapBaseDn;
    @Value("${ldap.groupSearchBase}")
    private String ldapGroupSearchBase;
    @Value("${ldap.userDnPattern}")
    private String ldapUserDnPattern;
    @Value("${ldap.passwordAttribute}")
    private String ldapPasswordAttribute;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()

                    //MVC
                    .antMatchers("/").permitAll()

                    .antMatchers("/edit/**").fullyAuthenticated()
                    .antMatchers("/delete/**").fullyAuthenticated()

                    //Radio API
                    .antMatchers(HttpMethod.DELETE, "/radio/**").fullyAuthenticated()
                    .antMatchers(HttpMethod.PUT, "/radio/**").fullyAuthenticated()
                    .antMatchers("/radio/**").permitAll()
                    .and()
                .formLogin();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns(ldapUserDnPattern)
                .groupSearchBase(ldapGroupSearchBase)
                .contextSource(contextSource())
                .passwordCompare()
                .passwordEncoder(new LdapShaPasswordEncoder())
                .passwordAttribute(ldapPasswordAttribute);
    }

    @Bean
    protected DefaultSpringSecurityContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(Arrays.asList(ldapUrl), ldapBaseDn);
    }

}
