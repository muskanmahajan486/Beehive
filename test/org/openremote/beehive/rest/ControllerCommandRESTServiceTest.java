/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.dto.ControllerCommandDTO;
import org.openremote.beehive.api.dto.UpdateControllerCommandDTO;
import org.openremote.beehive.api.dto.UploadLogsControllerCommandDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.ControllerCommand.State;
import org.openremote.beehive.domain.ControllerCommand.Type;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.exception.FilePermissionException;
import org.openremote.beehive.rest.service.ControllerCommandRESTTestService;
import org.openremote.beehive.rest.service.ResourceRESTTestService;
import org.openremote.beehive.utils.RESTTestUtils;

import com.sun.syndication.io.impl.Base64;

public class ControllerCommandRESTServiceTest extends TemplateTestBase {

   private static final byte[] TEST_BYTES = new byte[]{1,2,3,4,5,6};

   private ResourceService resourceService = (ResourceService) SpringTestContext.getInstance().getBean("resourceService");
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   private Configuration configuration = (Configuration) SpringTestContext.getInstance().getBean("configuration");

   @Override
   protected void setUp() throws Exception {
      super.setUp();

      User user = new User();
      user.setUsername("stef");
      // this here is called hashed, because we hash in the db. This is MD5('stef{stef}')
      user.setPassword("447cf0ee0366f1225e2509ee3c82fee3");
      genericDAO.save(user);

      Account a = new Account();
      user.setAccount(a);
      genericDAO.save(a);
   }

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      User u = genericDAO.getByNonIdField(User.class, "username", "stef");
      genericDAO.delete(u);
      genericDAO.deleteAll(genericDAO.loadAll(ControllerCommand.class));
   }

   protected void addCredential(MockHttpRequest mockHttpRequest) {
      mockHttpRequest.header(Constant.HTTP_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("stef:stef"));
   }

   public void testUpload() throws URISyntaxException, IOException, FilePermissionException {

      DetachedCriteria criteria = DetachedCriteria.forClass(ControllerCommand.class);

      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));

      // add a command
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/user/stef/command-queue/update-controller");
      mockHttpRequest.contentType("application/octet-stream");
      mockHttpRequest.content(TEST_BYTES);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());

      // make sure it has been saved
      List<ControllerCommand> commandList = genericDAO.findByDetachedCriteria(criteria );
      assertEquals(1, commandList.size());
      ControllerCommand command = commandList.get(0);
      assertEquals(State.OPEN, command.getState());
      assertEquals(Type.UPDATE_CONTROLLER, command.getType());
      assertNotNull(command.getCreationDate());

      // make sure the file is correct as well
      File resource = resourceService.getResource("stef", Constant.CONTROLLER_UPDATES_PATH + File.separatorChar + command.getOid());
      assertNotNull(resource);
      assertEquals(TEST_BYTES.length, resource.length());
      FileInputStream is = new FileInputStream(resource);
      byte[] bytes = new byte[TEST_BYTES.length]; 
      assertEquals(bytes.length, is.read(bytes));
      assertEquals(TEST_BYTES, bytes);

      // all good, let's remove that file
      resource.delete();
   }

   public void testLogUpload() throws URISyntaxException, IOException, FilePermissionException {
      DetachedCriteria criteria = DetachedCriteria.forClass(ControllerCommand.class);

      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));

      // add a command
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/user/stef/command-queue/upload-logs");
      mockHttpRequest.contentType("application/octet-stream");
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      assertEquals(HttpURLConnection.HTTP_CREATED, mockHttpResponse.getStatus());

      // make sure we get a URL to post to
      URI location = (URI) mockHttpResponse.getOutputHeaders().getFirst("Location");
      assertNotNull(location);

      // make sure it has been saved
      List<ControllerCommand> commandList = genericDAO.findByDetachedCriteria(criteria );
      assertEquals(1, commandList.size());
      ControllerCommand command = commandList.get(0);
      assertEquals(State.OPEN, command.getState());
      assertEquals(Type.UPLOAD_LOGS, command.getType());
      assertNotNull(command.getCreationDate());

      // now post to it
      mockHttpRequest = MockHttpRequest.post(location.getPath());
      mockHttpRequest.contentType("application/octet-stream");
      // use the openremote.zip in the fixtures
      InputStream zip = getClass().getResourceAsStream("/fixture/resources/1/openremote.zip");
      mockHttpRequest.content(zip);
      addCredential(mockHttpRequest);
      mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      zip.close();

      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());

      // make sure the zip was uploaded
      File resourcesDir = resourceService.getResource("stef", Constant.CONTROLLER_LOGS_PATH + File.separatorChar + command.getOid());
      assertNotNull(resourcesDir);
      // check that all files were extracted
      assertEquals(8, resourcesDir.list().length);

      // and check a random file among them
      File resource = new File(resourcesDir, "controller.xml");
      FileInputStream is = new FileInputStream(resource);
      @SuppressWarnings("unchecked")
      List<String> lines = IOUtils.readLines(is);
      assertTrue(lines.size() > 0);
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", lines.get(0));

      // all good, let's clear up
      FileUtils.deleteDirectory(resourcesDir);
   }

   private void assertEquals(byte[] expected, byte[] tested){
      assertEquals(expected.length, tested.length);
      for(int i=0;i<expected.length;i++)
         assertEquals(expected[i], tested[i]);
   }

   public void testDownload() throws URISyntaxException, IOException {
      User user = accountService.loadByUsername("stef");
      Account account = user.getAccount();

      // save a new command
      ControllerCommand updateCommand = new ControllerCommand(account, Type.UPDATE_CONTROLLER);
      genericDAO.save(updateCommand);

      // save the resource file
      File folder = new File(configuration.getModelerResourcesDir() 
            + File.separator + account.getOid()
            + File.separator + Constant.CONTROLLER_UPDATES_PATH);
      folder.mkdirs();
      File resource = new File(folder, String.valueOf(updateCommand.getOid()));
      assertNotNull(resource);
      FileOutputStream os = new FileOutputStream(resource);
      os.write(TEST_BYTES);
      os.flush();
      os.close();

      // now try to download it
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ResourceRESTTestService.class);
      MockHttpRequest mockHttpRequest = 
         MockHttpRequest.get("/user/stef/resources/updates/"+updateCommand.getOid());
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());
      assertEquals(TEST_BYTES, mockHttpResponse.getOutput());

      // all good, let's remove that file
      resource.delete();
   }

   public void testListXML() throws URISyntaxException, JAXBException {
      ControllerCommand[] commands = setupListContent();

      // get the list
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/stef/command-queue");
      addCredential(mockHttpRequest);
      mockHttpRequest.accept(MediaType.APPLICATION_XML);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      // check the list order
      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());
      JAXBContext jaxbContext = JAXBContext.newInstance(ControllerCommand.class, ControllerCommandListingXML.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      ControllerCommandListingXML commandList = (ControllerCommandListingXML) unmarshaller.unmarshal(new StringReader(mockHttpResponse.getContentAsString()));
      assertNotNull(commandList);
      List<ControllerCommandDTO> xmlCommands = commandList.getCommands();
      assertEquals(3, xmlCommands.size());
      
      ControllerCommandDTO xmlCommand0 = xmlCommands.get(0);
      ControllerCommandDTO xmlCommand1 = xmlCommands.get(1);
      ControllerCommandDTO xmlCommand2 = xmlCommands.get(2);
      
      assertEquals(commands[2].getOid(), xmlCommand0.getOid());
      assertEquals(commands[1].getOid(), xmlCommand1.getOid());
      assertEquals(commands[0].getOid(), xmlCommand2.getOid());
      
      // now check their types
      assertTrue(xmlCommand0 instanceof UploadLogsControllerCommandDTO);
      assertEquals(Type.UPLOAD_LOGS, xmlCommand0.getType());
      assertTrue(xmlCommand1 instanceof UpdateControllerCommandDTO);
      assertEquals(Type.UPDATE_CONTROLLER, xmlCommand1.getType());
      assertTrue(xmlCommand2 instanceof UpdateControllerCommandDTO);
      assertEquals(Type.UPDATE_CONTROLLER, xmlCommand2.getType());

      // and make sure they all have the proper urls
      assertNotNull(((UploadLogsControllerCommandDTO)xmlCommand0).getResource());
      assertNotNull(xmlCommand0.getClose());
      assertNotNull(((UpdateControllerCommandDTO)xmlCommand1).getResource());
      assertNotNull(xmlCommand1.getClose());
      assertNotNull(((UpdateControllerCommandDTO)xmlCommand2).getResource());
      assertNotNull(xmlCommand2.getClose());
   }

   public void testListJSON() throws URISyntaxException, JSONException {
      ControllerCommand[] commands = setupListContent();

      // get the list
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/user/stef/command-queue");
      addCredential(mockHttpRequest);
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      // check the list order
      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());

      System.out.println(mockHttpResponse.getContentAsString());
      JSONObject json = new JSONObject(mockHttpResponse.getContentAsString());
      assertTrue(json.has("commands"));
      JSONObject jsonCommands = json.getJSONObject("commands");
      assertTrue(jsonCommands.has("command"));
      JSONArray jsonCommandsArray = jsonCommands.getJSONArray("command");
      assertEquals(3, jsonCommandsArray.length());
      JSONObject jsonObject0 = jsonCommandsArray.getJSONObject(0);
      JSONObject jsonObject1 = jsonCommandsArray.getJSONObject(1);
      JSONObject jsonObject2 = jsonCommandsArray.getJSONObject(2);
      assertEquals(commands[2].getOid(), jsonObject0.getInt("id"));
      assertEquals(commands[1].getOid(), jsonObject1.getInt("id"));
      assertEquals(commands[0].getOid(), jsonObject2.getInt("id"));
      
      // now check that we have the right types
      assertEquals("upload-logs", jsonObject0.get("@type"));
      assertEquals("update-controller", jsonObject1.get("@type"));
      assertEquals("update-controller", jsonObject2.get("@type"));
      
      // and make sure they all have the proper urls
      assertTrue(jsonObject0.has("@resource"));
      assertTrue(jsonObject0.has("@close"));
      assertTrue(jsonObject1.has("@resource"));
      assertTrue(jsonObject1.has("@close"));
      assertTrue(jsonObject2.has("@resource"));
      assertTrue(jsonObject2.has("@close"));
   }

   private ControllerCommand[] setupListContent() {
      User user = accountService.loadByUsername("stef");
      Account account = user.getAccount();

      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));

      // make up some dates to check for ordering
      Calendar cal = new GregorianCalendar();

      // save some commands
      ControllerCommand updateCommand1 = new ControllerCommand(account, Type.UPDATE_CONTROLLER);
      updateCommand1.setCreationDate(cal.getTime());
      genericDAO.save(updateCommand1);

      ControllerCommand updateCommand2 = new ControllerCommand(account, Type.UPDATE_CONTROLLER);
      cal.add(Calendar.YEAR, -1);
      updateCommand2.setCreationDate(cal.getTime());
      genericDAO.save(updateCommand2);

      ControllerCommand updateCommand3 = new ControllerCommand(account, Type.UPLOAD_LOGS);
      cal.add(Calendar.YEAR, -1);
      updateCommand3.setCreationDate(cal.getTime());
      genericDAO.save(updateCommand3);

      ControllerCommand updateCommand4 = new ControllerCommand(account, Type.UPDATE_CONTROLLER);
      cal.add(Calendar.YEAR, -1);
      updateCommand4.setCreationDate(cal.getTime());
      updateCommand4.setState(State.DONE);
      genericDAO.save(updateCommand4);

      return new ControllerCommand[]{updateCommand1, updateCommand2, updateCommand3, updateCommand4};
   }

   public void testDelete() throws URISyntaxException, JAXBException {
      User user = accountService.loadByUsername("stef");
      Account account = user.getAccount();

      // save a command
      ControllerCommand updateCommand1 = new ControllerCommand(account, Type.UPDATE_CONTROLLER);
      assertTrue(updateCommand1.getState() == State.OPEN);
      genericDAO.save(updateCommand1);

      // delete it
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/command-queue/"+updateCommand1.getOid());
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      // check the list order
      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());
      assertTrue(updateCommand1.getState() == State.DONE);
   }
}
