package in.brewcode.api.service.impl;

import static in.brewcode.api.service.common.ServiceUtils.convertToAuthorDto;
import static in.brewcode.api.service.common.ServiceUtils.convertToAuthorEntity;
import static in.brewcode.api.service.common.ServiceUtils.convertToPrivilegeEntity;
import static in.brewcode.api.service.common.ServiceUtils.convertToRoleDto;
import static in.brewcode.api.service.common.ServiceUtils.convertToRoleEntity;
import in.brewcode.api.dto.AuthorDto;
import in.brewcode.api.dto.AuthorWithRoleDto;
import in.brewcode.api.dto.PrivilegeDto;
import in.brewcode.api.dto.RoleDto;
import in.brewcode.api.exception.PrivilegeAlreadyExistsException;
import in.brewcode.api.exception.PrivilegeNotFoundException;
import in.brewcode.api.exception.RoleAlreadyExistsException;
import in.brewcode.api.exception.RoleNotFoundException;
import in.brewcode.api.exception.UserNotFoundException;
import in.brewcode.api.persistence.dao.IAdminAuthorDao;
import in.brewcode.api.persistence.dao.IPrivilegeDao;
import in.brewcode.api.persistence.dao.IRoleDao;
import in.brewcode.api.persistence.entity.Author;
import in.brewcode.api.persistence.entity.Privilege;
import in.brewcode.api.persistence.entity.Role;
import in.brewcode.api.service.IAdminService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
@Transactional
public class AdminService implements IAdminService {
	@Autowired
	private IAdminAuthorDao adminAuthorDao;

	@Autowired
	private IRoleDao roleDao;

	@Autowired
	private IPrivilegeDao privilegeDao;

	protected PagingAndSortingRepository<Author, Long> getAdminAuthorDao() {

		return adminAuthorDao;
	}

	public void addPrivilege(PrivilegeDto privilegeDto) throws PrivilegeAlreadyExistsException {
		Preconditions.checkArgument(null!=privilegeDto, "Privilege cannot be null");
		Privilege privilege = new Privilege();
		if(privilegeDao.findByPrivilegeNameIgnoreCase(privilegeDto.getPrivilegeName())==null){
		privilegeDao.save(convertToPrivilegeEntity(privilegeDto, privilege));}
		else{
			throw new PrivilegeAlreadyExistsException("Cannot create a duplicate privilege.");
		}
	}

	public void addRole(RoleDto roleDto) throws PrivilegeNotFoundException {
		Preconditions.checkArgument(null!=roleDto, " Role cannot be null");
		Role role = convertToRoleEntity(roleDto, new Role());
		Set<Privilege> rolePrivileges = null;
		if (roleDto.getPrivileges() != null) {
			rolePrivileges = new HashSet<Privilege>();
			for (PrivilegeDto pd : roleDto.getPrivileges()) {
				Privilege privilege = privilegeDao
						.findByPrivilegeNameIgnoreCase(pd.getPrivilegeName());
				if (privilege != null) {
					rolePrivileges.add(privilege);
				} else {
					throw new PrivilegeNotFoundException(
							"Incorrect Set of Privileges");
				}
			}
			role.setRolePrivileges(rolePrivileges);
		}
		roleDao.save(role);
	}
	
	public void updateRoleName(String oldRoleName, String newRoleName)
			throws RoleNotFoundException, RoleAlreadyExistsException {
		Preconditions.checkArgument(!(oldRoleName == null || oldRoleName == ""
				|| newRoleName == null || newRoleName == ""), " Old / New Role name cannot be empty");
		;
		Role role = roleDao.findByRoleNameIgnoreCase(oldRoleName);
		if (roleDao.findByRoleNameIgnoreCase(newRoleName) != null
				&& !(oldRoleName.equalsIgnoreCase(newRoleName))) {
			throw new RoleAlreadyExistsException(
					"Cannot update role name. Already a role with name "
							+ newRoleName + " exists");
		}
		if (role != null) {
			role.setRoleName(newRoleName);
			roleDao.save(role);
		} else {
			throw new RoleNotFoundException("Invalid role name, " + oldRoleName);
		}
	}
	public void updatePrivilegesOfRole(RoleDto roleDto)
			throws RoleNotFoundException, PrivilegeNotFoundException {
		Preconditions.checkArgument(null!=roleDto, "RoleDto input can't be null");
		Role role = roleDao.findByRoleNameIgnoreCase(roleDto.getRoleName());
		//If update is not possible, create new role.
		if(role==null){
			//If update is not possible, create new role.
			//throw new RoleNotFoundException("Role doesn't exists. So, can't update");
			//Now, instead of updating, a new role is created
			addRole(roleDto);
		}
		else{
			role = convertToRoleEntity(roleDto, role);
			Set<Privilege> rolePrivileges = null; 
			if (roleDto.getPrivileges() != null) {
				rolePrivileges = new HashSet<Privilege>();
				for (PrivilegeDto pd : roleDto.getPrivileges()) {
					Privilege privilege = privilegeDao
							.findByPrivilegeNameIgnoreCase(pd.getPrivilegeName());
					if (privilege != null) {
						rolePrivileges.add(privilege);
					} else {
						throw new PrivilegeNotFoundException(
								"Incorrect Set of Privileges");
					}
				}
				role.setRolePrivileges(rolePrivileges);
			}
			roleDao.save(role);

			
		}
		
	}

	public void deleteRole(String roleName) throws RoleNotFoundException {
		Preconditions.checkArgument(null!=roleName);
		Role role = roleDao.findByRoleNameIgnoreCase(roleName);
		if (role != null) {
			roleDao.delete(role);
		} else {
			throw new RoleNotFoundException("Cannot delete role. " + roleName
					+ " is invalid input");
		}
	}

	public void deletePrivilege(String privilegeName)
			throws PrivilegeNotFoundException {
		Preconditions.checkArgument(null!=privilegeName);
		Privilege privilege = privilegeDao
				.findByPrivilegeNameIgnoreCase(privilegeName);
		if (privilege != null) {
			privilegeDao.delete(privilege);
		} else {
			throw new PrivilegeNotFoundException(privilegeName
					+ " privilege doesn't exist");
		}

	}

	public void createAuthor(AuthorDto authorDto) {
		Preconditions.checkArgument(null!=authorDto);
		
		Author author = convertToAuthorEntity(authorDto, new Author());

		getAdminAuthorDao().save(author);

	}

	public void lockAuthor(String userName) throws UserNotFoundException {
		Preconditions.checkArgument(userName != null && !userName.equals(""));
		if (adminAuthorDao.findByAuthorUserName(userName) != null) {
			adminAuthorDao.lockAuthor(userName);
		} else {
			throw new UserNotFoundException(userName + " not found");
		}
	}

	public void unlockAuthor(String userName) throws UserNotFoundException {
		Preconditions.checkArgument(userName != null && !userName.equals(""));
		if (adminAuthorDao.findByAuthorUserName(userName) != null) {
			adminAuthorDao.unlockAuthor(userName);
		} else {
			throw new UserNotFoundException(userName + " not found");
		}
	}

	public AuthorDto findAuthorByUserName(String userName) {
		AuthorDto authorDto = null;
		Author author = adminAuthorDao.findByAuthorUserName(userName);

		Preconditions.checkArgument(null!=author);
		authorDto = convertToAuthorDto(author);

		return authorDto;
	}

	public void updateAuthor(AuthorDto authorDto) {
		Preconditions.checkArgument(authorDto!=null);
		Author author = adminAuthorDao.findByAuthorUserName(authorDto
				.getAuthorUserName());
		getAdminAuthorDao().save(convertToAuthorEntity(authorDto, author));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.brewcode.api.service.IAdminService#findAllAuthors()
	 */
	public List<AuthorWithRoleDto> findAllAuthors() {
		List<AuthorWithRoleDto> listAuthorWithRoleDto = null;
		List<Author> listAuthors = (List<Author>) getAdminAuthorDao().findAll();
		Hibernate.initialize(listAuthors);
		if (listAuthors != null) {
			listAuthorWithRoleDto = new ArrayList<AuthorWithRoleDto>();
			for (Author a : listAuthors) {
				AuthorDto ad = convertToAuthorDto(a);
				AuthorWithRoleDto ald = new AuthorWithRoleDto();
				ald.setAuthorDto(ad);
				//Authors can have no roles assigned to them. In convertToRole(..), we shall not pass null
				if(a.getRole()!=null){
				ald.setRoleDto(convertToRoleDto(a.getRole()));
				}listAuthorWithRoleDto.add(ald);
			}
		}
		return listAuthorWithRoleDto;
	}

	public AuthorDto findByUserName(String userName) {

		Author author = adminAuthorDao.findByAuthorUserName(userName);
		return convertToAuthorDto(author);
	}

	public void assignRoletoAuthor(String authorUserName, String roleName)
			throws UserNotFoundException, RoleNotFoundException {
		Preconditions.checkArgument(!(authorUserName == null
				|| roleName == null || authorUserName == "" || roleName == ""));
		Author author = adminAuthorDao.findByAuthorUserName(authorUserName);
		if (author == null) {
			throw new UserNotFoundException();
		}
		Role role = roleDao.findByRoleNameIgnoreCase(roleName);
		if (role == null) {
			throw new RoleNotFoundException(roleName + "role not found");
		}

		author.setRole(role);
		adminAuthorDao.save(author);

	}

	public void removeRoleOfAuthor(String authorUserName, String roleName)
			throws UserNotFoundException, RoleNotFoundException {
		Preconditions.checkArgument(null!=authorUserName, authorUserName+" can't be null");
		Preconditions.checkArgument(null!=roleName, roleName+" can't be null");
		Author author = adminAuthorDao.findByAuthorUserName(authorUserName);
		if (author == null) {
			throw new UserNotFoundException("No user exists with username: "
					+ authorUserName);
		}
		Role role = roleDao.findByRoleNameIgnoreCase(roleName);
		if (role == null) {
			throw new RoleNotFoundException(roleName + " not found");
		} else if (author.getRole() != null
				&& author.getRole().getRoleName()
						.equalsIgnoreCase(role.getRoleName())) {
			author.setRole(null);
			adminAuthorDao.save(author);
		} else {
			throw new RoleNotFoundException(" Wrong Input");
		}

	}

	public void deleteAuthor(String authorUserName)
			throws UserNotFoundException {
		Preconditions.checkArgument(null!=authorUserName);
		Author author = adminAuthorDao.findByAuthorUserName(authorUserName);
		if (author == null) {
			throw new UserNotFoundException("No user exists with username: "
					+ authorUserName);
		} else {
			adminAuthorDao.delete(author);
		}

	}


}
