package in.brewcode.api.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthorLoginDto {
	/**
	 * This field encapsulates username and email
	 */
	@NotNull
	private AuthorDto authorDto;
	/*@NotNull
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	*/private String adminPassword;
	/*
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	*/private String authorMobileNumber;
	
	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public AuthorDto getAuthorDto() {
		return authorDto;
	}

	public void setAuthorDto(AuthorDto authorDto) {
		this.authorDto = authorDto;
	}

	public String getAuthorMobileNumber() {
		return authorMobileNumber;
	}

	public void setAuthorMobileNumber(String authorMobileNumber) {
		this.authorMobileNumber = authorMobileNumber;
	}

	// Shalln't retreive Password
	/*
	 * public String getAdminPassword() { return adminPassword; }
	 */

}
