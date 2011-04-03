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
package org.openremote.beehive.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.AccountDTO;
import org.openremote.beehive.api.dto.UserDTO;



/**
 * The Class User.
 * 
 * @author Dan 2009-7-7
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "user")
public class User extends BusinessEntity {

   /** The username. */
   private String username;
   
   /** The account. */
   private Account account;
   
   /** The token is for the user reset password. */
   private String token;
   
   /** Store the pending user's role, its a display name, like "Building Modeler","UI Designer". */
   private String pendingRoleName;
   
   /**
    * Instantiates a new user.
    */
   public User() {
      account  = new Account();
   }

   public User(Account account) {
      this.account = account;
   }

   /**
    * Gets the username.
    * 
    * @return the username
    */
   @Column(unique = true, nullable = false)
   public String getUsername() {
      return username;
   }
   
   /**
    * Sets the username.
    * 
    * @param username the new username
    */
   public void setUsername(String username) {
      this.username = username;
   }
   
   /**
    * Gets the account.
    * 
    * @return the account
    */
   @ManyToOne
   public Account getAccount() {
      return account;
   }
   
   /**
    * Sets the account.
    * 
    * @param account the new account
    */
   public void setAccount(Account account) {
      this.account = account;
   }
   
   @Column(name = "token")
   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }
   
   @Column(name = "pending_role_name")
   public String getPendingRoleName() {
      return pendingRoleName;
   }

   public void setPendingRoleName(String pendingRoleName) {
      this.pendingRoleName = pendingRoleName;
   }
   
   public UserDTO toDTO() {
	      UserDTO dto = new UserDTO();
	      dto.setId(getOid());
	      dto.setUsername(username);
	      dto.setPendingRoleName(pendingRoleName);
	      dto.setToken(token);
	      dto.setAccount(new AccountDTO(account.getOid()));
	      return dto;
   }
}
