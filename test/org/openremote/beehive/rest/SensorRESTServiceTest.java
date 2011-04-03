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
import org.openremote.beehive.api.dto.modeler.CustomSensorDTO;
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.api.dto.modeler.RangeSensorDTO;
import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.rest.service.SensorRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class SensorRESTServiceTest.
 */
public class SensorRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testSaveAndUpdateSwitchSensor() throws Exception {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      long deviceId = deviceDTO.getId();
      long commandId = deviceDTO.getDeviceCommands().get(0).getId();
      
      String switchSensorJson = FixtureUtil.getFileContent("dtos/sensor/switch_sensor.json");
      String postData = switchSensorJson.replaceAll("\"id\":21", "\"id\":" + deviceId).replaceAll("\"id\":101", "\"id\":" + commandId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SensorRESTTestService.class);
      
      // save switchSensor
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/sensor/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbSensorJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      SensorDTO switchSensor = mapper.readValue(dbSensorJson, SensorDTO.class);
      assertEquals("switchsensor", switchSensor.getName());
      
      // update switchSensor
      switchSensor.setName("update switchsensor");
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/sensor/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateSensorJson = mapper.writeValueAsString(switchSensor);
      mockUpdateHttpRequest.content(updateSensorJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      
      super.tearDown();
      super.setUp();
      
      // load all sensors
      MockHttpRequest mockLoadAllHttpRequest = MockHttpRequest.get("/sensor/loadall/1");
      mockLoadAllHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadAllHttpRequest);
      MockHttpResponse mockLoadAllHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadAllHttpRequest, mockLoadAllHttpResponse);
      
      String sensorsJson = mockLoadAllHttpResponse.getContentAsString();
      SensorListing sensorList = mapper.readValue(sensorsJson, SensorListing.class);
      assertEquals(1, sensorList.getSensors().size());
   }
   
   public void testSaveAndLoadRangeSensor() throws Exception {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      long deviceId = deviceDTO.getId();
      long commandId = deviceDTO.getDeviceCommands().get(1).getId();
      
      String rangeSensorJson = FixtureUtil.getFileContent("dtos/sensor/range_sensor.json");
      String postData = rangeSensorJson.replaceAll("\"id\":21", "\"id\":" + deviceId).replaceAll("\"id\":102", "\"id\":" + commandId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SensorRESTTestService.class);
      
      // save rangeSensor
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/sensor/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbSensorJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      RangeSensorDTO rangeSensor = mapper.readValue(dbSensorJson, RangeSensorDTO.class);
      assertEquals("rangesensor", rangeSensor.getName());
      
      super.tearDown();
      super.setUp();
      
      // load rangeSensor
      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.get("/sensor/load/" + rangeSensor.getId());
      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadHttpRequest);
      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
      
      String dbSensorJson2 = mockLoadHttpResponse.getContentAsString();
      RangeSensorDTO rangeSensor2 = mapper.readValue(dbSensorJson2, RangeSensorDTO.class);
      assertEquals(rangeSensor.getName(), rangeSensor2.getName());
      
      // load same sensors
      String dtoJson = mapper.writeValueAsString(rangeSensor2);
      MockHttpRequest mockLoadSameHttpRequest = MockHttpRequest.post("/sensor/loadsamesensors");
      mockLoadSameHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockLoadSameHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockLoadSameHttpRequest);
      mockLoadSameHttpRequest.content(dtoJson.getBytes());
      MockHttpResponse mockLoadSameHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadSameHttpRequest, mockLoadSameHttpResponse);

      String sensorsJson = mockLoadSameHttpResponse.getContentAsString();
      SensorListing sensorList = mapper.readValue(sensorsJson, SensorListing.class);
      assertEquals(1, sensorList.getSensors().size());
   }
   
   public void testSaveAndDeleteCustomSensor() throws JsonParseException, JsonMappingException, URISyntaxException, IOException {
      DeviceDTO deviceDTO = saveDeviceWithCommands();
      long deviceId = deviceDTO.getId();
      long commandId = deviceDTO.getDeviceCommands().get(2).getId();
      
      String customSensorJson = FixtureUtil.getFileContent("dtos/sensor/custom_sensor.json");
      String postData = customSensorJson.replaceAll("\"id\":21", "\"id\":" + deviceId).replaceAll("\"id\":103", "\"id\":" + commandId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SensorRESTTestService.class);
      
      // save customSensor
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/sensor/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbSensorJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      CustomSensorDTO customSensor = mapper.readValue(dbSensorJson, CustomSensorDTO.class);
      assertEquals("customsensor", customSensor.getName());
      
      // update customSensor
      customSensor.setName("update customsensor");
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/sensor/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateSensorJson = mapper.writeValueAsString(customSensor);
      mockUpdateHttpRequest.content(updateSensorJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      assertEquals(200, mockUpdateHttpResponse.getStatus());
      
      // delete customSensor
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/sensor/delete/" + customSensor.getId());
      addCredential(mockDeleteHttpRequest);
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   private DeviceDTO saveDeviceWithCommands() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_commands.json");
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
