/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2016, OpenRemote Inc.
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
package org.openremote.beehive;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Icon;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.domain.User;

import com.sun.syndication.io.impl.Base64;

/**
 * Test base for template-related test
 * 
 * @author Dan Cong
 *
 */
public class TemplateTestBase extends TestBase {
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();

      User user = new User();
      user.setUsername("dan");
      user.setPassword("cong");
      genericDAO.save(user);
      
      Account a = new Account();
      user.setAccount(a);
      Template t1 = new Template();
      t1.setAccount(a);
      t1.setName("t1");
      t1.setContent("content");
      a.addTemplate(t1);
      Template t2 = new Template();
      t2.setAccount(a);
      t2.setName("t2");
      t2.setContent("content");
      a.addTemplate(t2);
      Template t3 = new Template();
      t3.setAccount(a);
      t3.setName("t3");
      t3.setContent("content");
      t3.setShared(true);
      a.addTemplate(t3);
      accountService.save(a);
      
      Icon i = new Icon();
      i.setFileName("menu.png");
      i.setName("menu");
      genericDAO.save(i);
   }

   

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      User u = genericDAO.getByNonIdField(User.class, "username", "dan");
      genericDAO.delete(u);
   }
   
   protected void addCredential(MockHttpRequest mockHttpRequest) {
      mockHttpRequest.header(Constant.HTTP_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("dan:cong"));
   }

}
