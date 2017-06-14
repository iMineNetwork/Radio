package nl.imine.radio.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(WebSecurityConfigurerAdapter.class);

    @Autowired
    private ContextSource contextSource;

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

    @Value("${ldap.managerUser}")
    private String ldapManagerUser;
    @Value("${ldap.managerPassword}")
    private String ldapManagerPassword;

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
                .ldapAuthentication().contextSource((BaseLdapPathContextSource) contextSource)
                    .userDnPatterns(ldapUserDnPattern)
                    .groupSearchBase(ldapGroupSearchBase)
                    .passwordCompare()
                        .passwordEncoder(new LdapShaPasswordEncoder())
                        .passwordAttribute(ldapPasswordAttribute);
    }
}
