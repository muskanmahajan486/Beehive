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
package org.openremote.beehive.api.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.domain.User;

/**
 * @author tomsky
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "user")
public class UserDTO extends BusinessEntityDTO {

   private String username;
   private String token;
   private String pendingRoleName;
   private AccountDTO account;
   
   public String getUsername() {
      return username;
   }
   public String getToken() {
      return token;
   }
   public String getPendingRoleName() {
      return pendingRoleName;
   }
   public void setUsername(String username) {
      this.username = username;
   }
   public void setToken(String token) {
      this.token = token;
   }
   public void setPendingRoleName(String pendingRoleName) {
      this.pendingRoleName = pendingRoleName;
   }
   @XmlElement(name = "account")
   public AccountDTO getAccount() {
      return account;
   }
   public void setAccount(AccountDTO account) {
      this.account = account;
   }
   
   public User toUser() {
      User user = new User();
      user.setOid(getId());
      user.setUsername(username);
      user.setPendingRoleName(pendingRoleName);
      user.setToken(token);
      return user;
   }
}
