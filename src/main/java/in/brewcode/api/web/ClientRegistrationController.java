package in.brewcode.api.web;

import java.util.List;

import in.brewcode.api.web.common.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Preconditions;


@Controller
@RequestMapping("/client")
public class ClientRegistrationController extends BaseController{

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Autowired
	private ClientRegistrationService clientRegistrationService;
	
//	@PreAuthorize("#oauth2.hasScope('register_client')")
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.CREATED)
	public void registerClient(@RequestBody BaseClientDetails clientDetails){
		
		Preconditions.checkNotNull(clientDetails);
		
		clientRegistrationService.addClientDetails(clientDetails);
	}

	@PreAuthorize("#oauth2.hasScope('client')")
	@RequestMapping(value="/update", method=RequestMethod.PUT)
	@ResponseStatus(value=HttpStatus.OK)
	public void updateClient(@RequestBody BaseClientDetails clientDetails){
		
		Preconditions.checkNotNull(clientDetails);
		clientRegistrationService.addClientDetails(clientDetails);
	}
	
	@PreAuthorize("#oauth2.hasScope('client, admin') or hasRole('ADMIN')")
	@RequestMapping(value="/delete/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(value=HttpStatus.OK)
	public void removeClient(@PathVariable(value="id") String clientId){
		
		Preconditions.checkNotNull(clientId);
		clientRegistrationService.removeClientDetails(clientId);
	}

	@PreAuthorize("#oauth2.hasScope('admin') and hasRole('ADMIN')")
	@RequestMapping(value="/getAllClients", method=RequestMethod.GET)
	@ResponseBody
	public List<ClientDetails> getAllClients(){
		
		return clientRegistrationService.listClientDetails();
	}
}