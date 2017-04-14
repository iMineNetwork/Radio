package nl.imine.radio.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //MVC
                    .antMatchers("/").permitAll()

                //Radio API
//                    .antMatchers(HttpMethod.DELETE, "/radio/**").authenticated()
                    .antMatchers("/radio/**").permitAll();
    }

}
