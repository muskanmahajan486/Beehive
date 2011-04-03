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
import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.api.dto.modeler.SwitchDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.rest.service.SwitchRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class SwitchRESTserviceTest.
 */
public class SwitchRESTserviceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testSaveUpdateAndDeleteSwitch() throws Exception {
      DeviceDTO dbDevice = saveDeviceWithSwitchSensor();
      List<DeviceCommandDTO> deviceCommands = dbDevice.getDeviceCommands();
      long deviceId = dbDevice.getId();
      long commandOnId = deviceCommands.get(0).getId();
      long commandOffId = deviceCommands.get(1).getId();
      long commandStatusId = deviceCommands.get(2).getId();
      List<SensorDTO> sensors = dbDevice.getSensors();
      long sensorId = sensors.get(0).getId();
      long sensorCommandRefId = sensors.get(0).getSensorCommandRef().getId();
      
      String switchJson = FixtureUtil.getFileContent("dtos/switchs/switch.json");
      String postData = switchJson.replace("\"id\":23", "\"id\":" + deviceId).replace("\"id\":109",
            "\"id\":" + commandOnId).replace("\"id\":110", "\"id\":" + commandOffId).replace("\"id\":111",
            "\"id\":" + commandStatusId).replace("\"id\":40", "\"id\":" + sensorId).replace("\"id\":90",
            "\"id\":" + sensorCommandRefId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SwitchRESTTestService.class);
      
      // save switch
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/switch/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbSwitchJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      SwitchDTO switchDTO = mapper.readValue(dbSwitchJson, SwitchDTO.class);
      assertEquals("lightswitch", switchDTO.getName());
      
      super.tearDown();
      super.setUp();
      
      // update switch
      switchDTO.setName("update lightswitch");
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/switch/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateSensorJson = mapper.writeValueAsString(switchDTO);
      mockUpdateHttpRequest.content(updateSensorJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      
      String dbSwitchJson2 = mockUpdateHttpResponse.getContentAsString();
      SwitchDTO switchDTO2 = mapper.readValue(dbSwitchJson2, SwitchDTO.class);
      assertEquals("update lightswitch", switchDTO2.getName());
      
      // load same switchs
      MockHttpRequest mockLoadSameHttpRequest = MockHttpRequest.post("/switch/loadsameswitchs");
      mockLoadSameHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockLoadSameHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockLoadSameHttpRequest);
      mockLoadSameHttpRequest.content(dbSwitchJson2.getBytes());
      MockHttpResponse mockLoadSameHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadSameHttpRequest, mockLoadSameHttpResponse);

      String switchsJson = mockLoadSameHttpResponse.getContentAsString();
      SwitchListing switchList = mapper.readValue(switchsJson, SwitchListing.class);
      assertEquals(1, switchList.getSwitchs().size());
      
      // delete switch
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/switch/delete/" + switchDTO2.getId());
      addCredential(mockDeleteHttpRequest);
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   public void testLoadAllSwitchs() throws Exception {
      Dispatcher deviceDispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_content.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      deviceDispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      super.tearDown();
      super.setUp();
      
      // load all switchs
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SwitchRESTTestService.class);
      MockHttpRequest mockLoadAllHttpRequest = MockHttpRequest.get("/switch/loadall/1");
      mockLoadAllHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadAllHttpRequest);
      MockHttpResponse mockLoadAllHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadAllHttpRequest, mockLoadAllHttpResponse);
      
      String switchsJson = mockLoadAllHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      SwitchListing switchList = mapper.readValue(switchsJson, SwitchListing.class);
      assertEquals(1, switchList.getSwitchs().size());
   }
   
   private DeviceDTO saveDeviceWithSwitchSensor() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_switchsensor.json");
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
      List<Device> devices = genericDAO.loadAll(Device.class);
      genericDAO.deleteAll(devices);
   }
}
