package nl.imine.radio.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.authentication.DefaultValuesAuthenticationSourceDecorator;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.SpringSecurityAuthenticationSource;

@Configuration
public class LdapConfiguration {

	@Value("${ldap.url}")
	private String ldapUrl;
	@Value("${ldap.baseDn}")
	private String ldapBaseDn;

	@Value("${ldap.managerUser}")
	private String ldapManagerUser;
	@Value("${ldap.managerPassword}")
	private String ldapManagerPassword;

	@Bean
	public SpringSecurityAuthenticationSource springSecurityAuthenticationSource(){
		SpringSecurityAuthenticationSource springSecurityAuthenticationSource = new SpringSecurityAuthenticationSource();
		return springSecurityAuthenticationSource;
	}

	@Bean
	public ContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(ldapUrl);
		contextSource.setBase(ldapBaseDn);
		contextSource.setAuthenticationSource(authenticationSource());
		return contextSource;
	}

	@Bean
	public AuthenticationSource authenticationSource(){
		DefaultValuesAuthenticationSourceDecorator authenticationSource = new DefaultValuesAuthenticationSourceDecorator();
		authenticationSource.setTarget(springSecurityAuthenticationSource());
		authenticationSource.setDefaultUser(ldapManagerUser);
		authenticationSource.setDefaultPassword(ldapManagerPassword);
		return authenticationSource;
	}
}
