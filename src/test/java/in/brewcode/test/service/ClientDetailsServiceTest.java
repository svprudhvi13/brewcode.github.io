package in.brewcode.test.service;

import in.brewcode.api.config.PersistenceConfig;
import in.brewcode.api.config.SecurityConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		PersistenceConfig.class, SecurityConfig.class })
@Transactional
public class ClientDetailsServiceTest {
	@Autowired
	@Qualifier(value="clientRegistrationService")
	ClientRegistrationService clientRegistrationService;

	@Before
	public void init(){
		System.out.println("Init method:"+ this.toString());
	}
	@Test
	//@Ignore
	public void whenAllClientDetailsRetreived_thenNoError() {
		BaseClientDetails bcd = new BaseClientDetails();
		bcd.setAccessTokenValiditySeconds(1000);

		bcd.setAdditionalInformation(new HashMap<String, String>() {
			{
				put("purpose", "Training");
			}
		});
		bcd.setClientId("Ninja");
		bcd.setAuthorizedGrantTypes(new ArrayList<String>() {
			{
				add("password");
				add("refresh_token");
			}
		});
		bcd.setAuthorities(new ArrayList<GrantedAuthority>() {
			{
				add(new SimpleGrantedAuthority("author"));
				add(new SimpleGrantedAuthority("admin"));
			}
		});
		bcd.setClientSecret("secret");
		bcd.setRegisteredRedirectUri(new HashSet<String>() {
			{
				add("brewcode.in");
			}
		});
		bcd.setRefreshTokenValiditySeconds(2000);
		bcd.setScope(new ArrayList<String>(){{add("read"); add("edit");}});
		
		
		clientRegistrationService.addClientDetails(bcd);
		
	}
}