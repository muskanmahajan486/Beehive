/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.UserDTO;
import org.openremote.beehive.api.service.UserService;
import org.openremote.beehive.domain.User;

/**
 * Export restful service to manage user.
 * 
 * @author tomsky
 *
 */
@Path("/manageuser")
public class UserRESTService extends RESTBaseService {
   
   /**
    * Create new <code>User</code> by posted <code>UserDTO</code>.
    * Visits @ url "/manageuser/create"
    * 
    * @param userDTO the received <code>UserDTO</code>.
    * @return the <code>UserDTO</code> with id and <code>Account</code>.
    */
   @Path("create")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response createUser(UserDTO userDTO) {
      User user = new User();
      user.setUsername(userDTO.getUsername());
      getUserService().saveUser(user);
      return buildResponse(user.toDTO());
   }
   
   /**
    * Show user by {user_id}.
    * Visits @ url "/manageuser/get/{user_id}"
    * 
    * @param userId
    * @return the <code>UserDTO</code>.
    */
   @Path("get/{user_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response getUserById(@PathParam("user_id")  long userId) {
      User user = getUserService().getUserById(userId);
      if (user == null) return resourceNotFoundResponse();
      return buildResponse(user.toDTO());
   }
   
   /**
    * Show user by {username}.
    * Visits @ url "/manageuser/getbyname/{username}"
    * 
    * @param username
    * @return the <code>UserDTO</code>.
    */
   @Path("getbyname/{username}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response getUserByUsername(@PathParam("username")  String username) {
      User user = getUserService().getUserByUsername(username);
      if (user == null) return resourceNotFoundResponse();
      return buildResponse(user.toDTO());
   }
   
   /**
    * Update <code>User</code> token and pendingRoleName by posted <code>UserDTO</code>.
    * Visits @ url "/manageuser/update"
    * 
    * @param userDto the received <code>UserDTO</code>.
    * @return status code 200 or 500.
    */
   @Path("update")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response updateUser(UserDTO userDto) {
      User user = getUserService().getUserById(userDto.getId());
      user.setToken(userDto.getToken());
      user.setPendingRoleName(userDto.getPendingRoleName());
      getUserService().updateUser(user);
      return buildResponse(true);
   }
   
   /**
    * Delete <code>User</code> by {user_id}.
    * Visits @ url "/manageuser/delete/{user_id}"
    * 
    * @param userId
    * @return status code 200 or 500.
    */
   @Path("delete/{user_id}")
   @DELETE
   public Response deleteUser(@PathParam("user_id") long userId) {
      getUserService().deleteUserById(userId);
      return buildResponse(true);
   }
   
   /**
    * Create an invitee by posted <code>UserDTO</code> under the specified <code>Account</code>.
    * Visits @ url "/manageuser/createinvitee/{account_id}"
    * 
    * @param userDTO  the invitee, it is a <code>UserDTO</code>.
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the invitee with id and <code>Account</code> information.
    */
   @Path("createinvitee/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response createInvitee(UserDTO userDTO, @PathParam("account_id") long accountId,
                           @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      User invitee = userDTO.toUser();
      getUserService().saveInvitee(invitee, accountId);
      return buildResponse(invitee.toDTO());
   }
   
   /**
    * Gets all users under an Account.
    * Visits @ url "/manageuser/loadall/{account_id}"
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of users or 404 status code.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadUsersByAccount(@PathParam("account_id") long accountId,
                        @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<User> users = getUserService().loadUsersByAccount(accountId);
      List<UserDTO> userDTOs = new ArrayList<UserDTO>();
      for (User user : users) {
         userDTOs.add(user.toDTO());
      }
      
      if (userDTOs.size() == 0) return resourceNotFoundResponse();
      return buildResponse(new UserListing(userDTOs));
   }
   
   /**
    * Retrieves instance of UserService from spring IOC
    * 
    * @return UserService instance
    */
   protected UserService getUserService() {
      return (UserService) getSpringContextInstance().getBean("userService");
   }
}
