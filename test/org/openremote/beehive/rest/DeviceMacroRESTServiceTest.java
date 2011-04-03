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
import org.openremote.beehive.api.dto.modeler.DeviceMacroDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.rest.service.DeviceMacroRESTTestService;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class DeviceMacroRESTServiceTest.
 */
public class DeviceMacroRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testSaveDeviceMacro() throws JsonParseException, JsonMappingException, URISyntaxException, IOException {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      List<DeviceCommandDTO> commandDTOs = deviceDTO.getDeviceCommands();
      long cmdId1 = commandDTOs.get(0).getId();
      long cmdId2 = commandDTOs.get(1).getId();
      long cmdId3 = commandDTOs.get(2).getId();
      
      String deviceMacroJson = FixtureUtil.getFileContent("dtos/device_macro/device_macro.json");
      String postData = deviceMacroJson.replace("\"id\":114", "\"id\":" + cmdId1).replace("\"id\":115",
            "\"id\":" + cmdId2).replace("\"id\":116", "\"id\":" + cmdId3);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceMacroRESTTestService.class);
      // save deviceMacro
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/devicemacro/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbMacroJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceMacroDTO deviceMacroDTO = mapper.readValue(dbMacroJson, DeviceMacroDTO.class);
      assertEquals("macro1", deviceMacroDTO.getName());
      
      // update macro1
      deviceMacroDTO.setName("update macro1");
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/devicemacro/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateMacroJson = mapper.writeValueAsString(deviceMacroDTO);
      mockUpdateHttpRequest.content(updateMacroJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      assertEquals(200, mockUpdateHttpResponse.getStatus());
      
      // load macro items
      MockHttpRequest mockLoadItemsHttpRequest = MockHttpRequest.get("/devicemacro/loaditemsbyid/" + deviceMacroDTO.getId());
      mockLoadItemsHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadItemsHttpRequest);
      MockHttpResponse mockLoadItemsHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadItemsHttpRequest, mockLoadItemsHttpResponse);
      
      String itemsJson = mockLoadItemsHttpResponse.getContentAsString();
      DeviceMacroItemListing itemsList = mapper.readValue(itemsJson, DeviceMacroItemListing.class);
      assertEquals(5, itemsList.getDeviceMacroItems().size());
   }
   
   public void testLoadAllAndDeleteMacro() throws Exception {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      List<DeviceCommandDTO> commandDTOs = deviceDTO.getDeviceCommands();
      long cmdId1 = commandDTOs.get(0).getId();
      long cmdId2 = commandDTOs.get(1).getId();
      long cmdId3 = commandDTOs.get(2).getId();
      
      String deviceMacroJson = FixtureUtil.getFileContent("dtos/device_macro/device_macro.json");
      String postData = deviceMacroJson.replace("\"id\":114", "\"id\":" + cmdId1).replace("\"id\":115",
            "\"id\":" + cmdId2).replace("\"id\":116", "\"id\":" + cmdId3);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceMacroRESTTestService.class);
      // save deviceMacro
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/devicemacro/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      super.tearDown();
      super.setUp();
      
      // load all deviceMacros
      MockHttpRequest mockLoadAllHttpRequest = MockHttpRequest.get("/devicemacro/loadall/1");
      mockLoadAllHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadAllHttpRequest);
      MockHttpResponse mockLoadAllHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadAllHttpRequest, mockLoadAllHttpResponse);
      
      String macrosJson = mockLoadAllHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceMacroListing macrosList = mapper.readValue(macrosJson, DeviceMacroListing.class);
      assertEquals(1, macrosList.getDeviceMacros().size());
      
      // delete macro1
      DeviceMacroDTO macro1 = macrosList.getDeviceMacros().get(0);
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/devicemacro/delete/" + macro1.getId());
      addCredential(mockDeleteHttpRequest);
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   public void testLoadSameDeviceMacros() throws Exception {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      List<DeviceCommandDTO> commandDTOs = deviceDTO.getDeviceCommands();
      long cmdId1 = commandDTOs.get(0).getId();
      long cmdId2 = commandDTOs.get(1).getId();
      long cmdId3 = commandDTOs.get(2).getId();
      
      String deviceMacroJson = FixtureUtil.getFileContent("dtos/device_macro/device_macro.json");
      String postData = deviceMacroJson.replace("\"id\":114", "\"id\":" + cmdId1).replace("\"id\":115",
            "\"id\":" + cmdId2).replace("\"id\":116", "\"id\":" + cmdId3);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceMacroRESTTestService.class);
      // save deviceMacro
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/devicemacro/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String macroJson = mockHttpResponse.getContentAsString();
      
      super.tearDown();
      super.setUp();
      
      MockHttpRequest mockLoadSameHttpRequest = MockHttpRequest.post("/devicemacro/loadsamemacros/1");
      mockLoadSameHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockLoadSameHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockLoadSameHttpRequest);
      mockLoadSameHttpRequest.content(macroJson.getBytes());
      MockHttpResponse mockLoadSameHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadSameHttpRequest, mockLoadSameHttpResponse);
      
      String macrosJson = mockLoadSameHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceMacroListing macrosList = mapper.readValue(macrosJson, DeviceMacroListing.class);
      assertEquals(1, macrosList.getDeviceMacros().size());
   }
   
   private DeviceDTO saveDeviceWithCommands() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_macrocommands.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbDeviceJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceDTO dbDevice = mapper.readValue(dbDeviceJson, DeviceDTO.class);
      
      return dbDevice;
   }
   
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      List<DeviceMacro> deviceMacros = genericDAO.loadAll(DeviceMacro.class);
      genericDAO.deleteAll(deviceMacros);
      List<Device> devices = genericDAO.loadAll(Device.class);
      genericDAO.deleteAll(devices);
   }
}
