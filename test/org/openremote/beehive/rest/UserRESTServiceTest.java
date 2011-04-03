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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.dto.UserDTO;
import org.openremote.beehive.rest.service.UserRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class UserRESTServiceTest.
 */
public class UserRESTServiceTest extends TemplateTestBase {

   public void testCreateAndDeleteUser() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(UserRESTTestService.class);
      // create user
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/manageuser/create");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      String postData = FixtureUtil.getFileContent("dtos/user/user_create.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      String dbUserJson = mockHttpResponse.getContentAsString();
      
      ObjectMapper mapper = new ObjectMapper();
      UserDTO dbUser = mapper.readValue(dbUserJson, UserDTO.class);
      assertEquals("macuser5", dbUser.getUsername());
      
      // delete user
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/manageuser/delete/" + dbUser.getId());
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   public void testLoadById() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(UserRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/manageuser/get/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testLoadByName() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(UserRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/manageuser/getbyname/dan");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testCreateUpdateInvitee() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(UserRESTTestService.class);
      // create invitee
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/manageuser/createinvitee/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/user/invitee_create.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      String dbUserJson = mockHttpResponse.getContentAsString();
      
      ObjectMapper mapper = new ObjectMapper();
      UserDTO dbUser = mapper.readValue(dbUserJson, UserDTO.class);
      assertEquals("tomsky.wang@finalist.hk", dbUser.getUsername());
      
      // update invitee
      dbUser.setUsername("tomsky");
      String newUserJson = mapper.writeValueAsString(dbUser);
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/manageuser/update");
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.content(newUserJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      assertEquals(200, mockUpdateHttpResponse.getStatus());
      
      // delete invitee
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/manageuser/delete/" + dbUser.getId());
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   public void testLoadAllByAccountId() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(UserRESTTestService.class);
      
      // create invitee
      MockHttpRequest mockCreateHttpRequest = MockHttpRequest.post("/manageuser/createinvitee/1");
      mockCreateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockCreateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockCreateHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/user/invitee_create.json");
      mockCreateHttpRequest.content(postData.getBytes());
      MockHttpResponse mockCreateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockCreateHttpRequest, mockCreateHttpResponse);
      String dbUserJson = mockCreateHttpResponse.getContentAsString();
      
      // load all users by accountId
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/manageuser/loadall/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
      
      ObjectMapper mapper = new ObjectMapper();
      UserDTO dbUser = mapper.readValue(dbUserJson, UserDTO.class);
      
      // delete user
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/manageuser/delete/" + dbUser.getId());
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
}
