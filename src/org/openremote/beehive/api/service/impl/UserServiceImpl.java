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
package org.openremote.beehive.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.beehive.api.service.UserService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.User;

/**
 * @author tomsky
 *
 */
public class UserServiceImpl extends BaseAbstractService<User> implements UserService {

   public User saveUser(User user) {
      genericDAO.save(user.getAccount());
      genericDAO.save(user);
      return user;
   }

   public User getUserById(long id) {
      return genericDAO.getById(User.class, id);
   }

   public User getUserByUsername(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username);
   }
   
   public void updateUser(User user) {
      genericDAO.update(user);
   }
   
   public void deleteUserById(long id) {
      genericDAO.delete(getUserById(id));
   }

   public User saveInvitee(User invitee, long accountId) {
      Account account = genericDAO.getById(Account.class, accountId);
      if (account != null) {
         account.getUsers().add(invitee);
         invitee.setAccount(account);
         genericDAO.save(invitee);
      }
      return invitee;
   }

   public List<User> loadUsersByAccount(long accountId) {
      Account account = genericDAO.getById(Account.class, accountId);
      if (account != null) {
         Hibernate.initialize(account.getUsers());
         return account.getUsers();
      }
      return new ArrayList<User>();
   }

}
