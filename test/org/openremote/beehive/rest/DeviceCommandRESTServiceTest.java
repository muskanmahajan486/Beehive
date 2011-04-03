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
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.api.dto.modeler.ProtocolAttrDTO;
import org.openremote.beehive.api.dto.modeler.ProtocolDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.rest.service.DeviceCommandRESTTestService;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class DeviceCommandRESTServiceTest.
 */
public class DeviceCommandRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testSaveCommand() throws JsonParseException, JsonMappingException, URISyntaxException, IOException {
      Dispatcher deviceDispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      long deviceId = saveDevice(deviceDispatcher);
      
      String commandJson = FixtureUtil.getFileContent("dtos/device_command/command.json");
      String postData = commandJson.replaceAll("\"id\":17", "\"id\":" + deviceId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceCommandRESTTestService.class);
      
      // save device command
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/devicecommand/save");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbDeviceCommandJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceCommandDTO deviceCommand = mapper.readValue(dbDeviceCommandJson, DeviceCommandDTO.class);
      assertEquals("command1", deviceCommand.getName());
      
      // load device command
      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.get("/devicecommand/load/" + deviceCommand.getId());
      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadHttpRequest);
      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
      
      String dbDeviceCommandJson2 = mockLoadHttpResponse.getContentAsString();
      DeviceCommandDTO deviceCommand2 = mapper.readValue(dbDeviceCommandJson2, DeviceCommandDTO.class);
      assertEquals(deviceCommand.getName(), deviceCommand2.getName());
      
      deviceCommand2.setName("update command");
      ProtocolDTO protocol = new ProtocolDTO();
      ProtocolAttrDTO protocolAttr = new ProtocolAttrDTO();
      protocolAttr.setName("url");
      protocolAttr.setValue("http://update.command.com");
      List<ProtocolAttrDTO> attributes = new ArrayList<ProtocolAttrDTO>();
      attributes.add(protocolAttr);
      protocol.setAttributes(attributes);
      protocol.setType("HTTP");
      deviceCommand2.setProtocol(protocol);
      
      // update device command
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/devicecommand/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateCommandJson = mapper.writeValueAsString(deviceCommand2);
      mockUpdateHttpRequest.content(updateCommandJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      
      // delete device command
      deleteDeviceCommand(dispatcher, deviceCommand2.getId());
   }
   
   public void testSaveAllCommands() throws Exception {
      Dispatcher deviceDispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      long deviceId = saveDevice(deviceDispatcher);
      
      String commandsJson = FixtureUtil.getFileContent("dtos/device_command/commands.json");
      String postData = commandsJson.replaceAll("\"id\":20", "\"id\":" + deviceId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceCommandRESTTestService.class);
      
      // save device command
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/devicecommand/saveall");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(200, mockHttpResponse.getStatus());

//      super.tearDown();
//      super.setUp();
//      // load all device commands
//      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.get("/devicecommand/loadbydevice/" + deviceId);
//      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
//      addCredential(mockLoadHttpRequest);
//      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
//      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
//      
//      String allCommandsJson = mockLoadHttpResponse.getContentAsString();
//      ObjectMapper mapper = new ObjectMapper();
//      DeviceCommandListing deviceCommandList = mapper.readValue(allCommandsJson, DeviceCommandListing.class);
//      assertEquals(20, deviceCommandList.getDeviceCommands().size());
//      
//      // load same device commands
//      DeviceCommandDTO deviceCommandDTO = deviceCommandList.getDeviceCommands().get(0);
//      String dtoJson = mapper.writeValueAsString(deviceCommandDTO);
//      MockHttpRequest mockLoadSameHttpRequest = MockHttpRequest.post("/devicecommand/loadsamecommands");
//      mockLoadSameHttpRequest.accept(MediaType.APPLICATION_JSON);
//      mockLoadSameHttpRequest.contentType(MediaType.APPLICATION_JSON);
//      addCredential(mockLoadSameHttpRequest);
//      mockLoadSameHttpRequest.content(dtoJson.getBytes());
//      MockHttpResponse mockLoadSameHttpResponse = new MockHttpResponse();
//      dispatcher.invoke(mockLoadSameHttpRequest, mockLoadSameHttpResponse);
//      String sameCommandsJson = mockLoadSameHttpResponse.getContentAsString();
//      DeviceCommandListing sameDeviceCommandList = mapper.readValue(sameCommandsJson, DeviceCommandListing.class);
//      DeviceCommandDTO deviceCommandDTO2 = sameDeviceCommandList.getDeviceCommands().get(0);
//      assertTrue(deviceCommandDTO.getName().equals(deviceCommandDTO2.getName()));
      
   }
   
   private long saveDevice(Dispatcher deviceDispatcher) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_simple.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      deviceDispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbDeviceJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceDTO dbDevice = mapper.readValue(dbDeviceJson, DeviceDTO.class);
      
      return dbDevice.getId();
   }
   
   private void deleteDeviceCommand(Dispatcher dispatcher, long deviceCommandId) throws URISyntaxException {
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/devicecommand/delete/" + deviceCommandId);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(200, mockHttpResponse.getStatus());
   }
   
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      List<Device> devices = genericDAO.loadAll(Device.class);
      genericDAO.deleteAll(devices);
   }
}
