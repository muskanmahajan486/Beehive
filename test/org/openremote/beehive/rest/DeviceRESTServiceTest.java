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
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class DeviceRESTServiceTest.
 */
public class DeviceRESTServiceTest extends TemplateTestBase {

   public void testSaveUpdateAndDeleteSimpleDevice() throws Exception {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // create device
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_simple.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbDeviceJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceDTO dbDevice = mapper.readValue(dbDeviceJson, DeviceDTO.class);
      
      // update device
      dbDevice.setName("Update device");
      dbDevice.setVendor("update");
      dbDevice.setModel("update");
      String updateDeviceJson = mapper.writeValueAsString(dbDevice);
      updateDevice(dispatcher, updateDeviceJson);
      
      super.tearDown();
      super.setUp();
      
      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.get("/device/loadall/1");
      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadHttpRequest);
      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
      String devicesJson = mockLoadHttpResponse.getContentAsString();
      DeviceListing dbDevices = mapper.readValue(devicesJson, DeviceListing.class);
      assertEquals(1, dbDevices.getDevices().size());
      
      // delete device
      deleteDevice(dispatcher, dbDevice.getId());
   }
   
   public void testSaveDeviceWithContent() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_content.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbDeviceJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceDTO dbDevice = mapper.readValue(dbDeviceJson, DeviceDTO.class);
      assertEquals("DeviceWithContent", dbDevice.getName());
      
      // load device by device_id
      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.get("/device/load/" + dbDevice.getId());
      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadHttpRequest);
      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
      String dbDeviceJson2 = mockLoadHttpResponse.getContentAsString();
      assertEquals(dbDeviceJson, dbDeviceJson2);
      
      // delete device
      deleteDevice(dispatcher, dbDevice.getId());
   }
   
   public void testLoadEmptyDevices() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/device/loadall/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(404, mockHttpResponse.getStatus());
   }
   
   public void testLoadSameDevices() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // create device
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_simple.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      String deviceJson = mockHttpResponse.getContentAsString();
      
      // load device by device_id
      MockHttpRequest mockLoadHttpRequest = MockHttpRequest.post("/device/loadsamedevices/1");
      mockLoadHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockLoadHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockLoadHttpRequest);
      mockLoadHttpRequest.content(deviceJson.getBytes());
      MockHttpResponse mockLoadHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadHttpRequest, mockLoadHttpResponse);
      String devicesJson = mockLoadHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      DeviceListing dbDevices = mapper.readValue(devicesJson, DeviceListing.class);
      assertEquals(1, dbDevices.getDevices().size());
   }
   
   private void updateDevice(Dispatcher dispatcher, String deviceJson) throws URISyntaxException {
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/update");
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(deviceJson.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(200, mockHttpResponse.getStatus());
   }
   
   private void deleteDevice(Dispatcher dispatcher, long deviceId) throws URISyntaxException {
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/device/delete/" + deviceId);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(200, mockHttpResponse.getStatus());
   }
}
