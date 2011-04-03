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
package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.domain.User;

/**
 * Business service for <code>User</code>.
 * 
 * @author tomsky
 *
 */
public interface UserService {

   /**
    * Save user with default <code>Account</code>.
    * Used for creating a new user.
    * 
    * @param user
    * @return the new user
    */
   User saveUser(User user);
   
   /**
    * Get <code>User</code> according to <code>User</code> id.
    * 
    * @param id
    * @return the user which have account information.
    */
   User getUserById(long id);
   
   /**
    * Get <code>User</code> according to <code>User</code> username.
    * 
    * @param username
    * @return the user which have account information.
    */
   User getUserByUsername(String username);
   
   /**
    * Update <code>User</code> token or pendingRoleName.
    * 
    * @param user
    */
   void updateUser(User user);
   
   /**
    * Delete <code>User</code> according to <code>User</code> id.
    * 
    * @param id   the user id.
    */
   void deleteUserById(long id);
   
   /**
    * Save an invitee into an exist <code>Account</code>.
    * 
    * @param invitee
    * @param accountId
    * @return the saved invitee.
    */
   User saveInvitee(User invitee, long accountId);
   
   /**
    * Load All users under the same account according to <code>Account</code> id.
    * 
    * @param accountId
    * @return a list of Users.
    */
   List<User> loadUsersByAccount(long accountId);
}
