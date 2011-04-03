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
import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.rest.service.DeviceRESTTestService;
import org.openremote.beehive.rest.service.SliderRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class SliderRESTServiceTest.
 */
public class SliderRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   public void testSaveUpdateAndDeleteSlider() throws Exception {
      DeviceDTO dbDevice = saveDeviceWithSwitchSensor();
      List<DeviceCommandDTO> deviceCommands = dbDevice.getDeviceCommands();
      long deviceId = dbDevice.getId();
      long commandSetId = deviceCommands.get(0).getId();
      long commandStatusId = deviceCommands.get(1).getId();
      List<SensorDTO> sensors = dbDevice.getSensors();
      long sensorId = sensors.get(0).getId();
      long sensorCommandRefId = sensors.get(0).getSensorCommandRef().getId();
      
      String sliderJson = FixtureUtil.getFileContent("dtos/slider/slider.json");
      String postData = sliderJson.replace("\"id\":24", "\"id\":" + deviceId).replace("\"id\":112",
            "\"id\":" + commandSetId).replace("\"id\":113", "\"id\":" + commandStatusId).replace("\"id\":41",
            "\"id\":" + sensorId).replace("\"id\":116", "\"id\":" + sensorCommandRefId);
      
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SliderRESTTestService.class);
      
      // save slider
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/slider/save/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      String dbSliderJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      SliderDTO sliderDTO = mapper.readValue(dbSliderJson, SliderDTO.class);
      assertEquals("lightcolor", sliderDTO.getName());

      super.tearDown();
      super.setUp();
      
      // update slider
      sliderDTO.setName("update lightcolor");
      MockHttpRequest mockUpdateHttpRequest = MockHttpRequest.post("/slider/update");
      mockUpdateHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockUpdateHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockUpdateHttpRequest);
      String updateSensorJson = mapper.writeValueAsString(sliderDTO);
      mockUpdateHttpRequest.content(updateSensorJson.getBytes());
      MockHttpResponse mockUpdateHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockUpdateHttpRequest, mockUpdateHttpResponse);
      
      String dbSliderJson2 = mockUpdateHttpResponse.getContentAsString();
      SliderDTO sliderDTO2 = mapper.readValue(dbSliderJson2, SliderDTO.class);
      assertEquals("update lightcolor", sliderDTO2.getName());
      
      // load same sliders
      MockHttpRequest mockLoadSameHttpRequest = MockHttpRequest.post("/slider/loadsamesliders");
      mockLoadSameHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockLoadSameHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockLoadSameHttpRequest);
      mockLoadSameHttpRequest.content(dbSliderJson2.getBytes());
      MockHttpResponse mockLoadSameHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadSameHttpRequest, mockLoadSameHttpResponse);

      String slidersJson = mockLoadSameHttpResponse.getContentAsString();
      SliderListing sliderList = mapper.readValue(slidersJson, SliderListing.class);
      assertEquals(1, sliderList.getSliders().size());
      
      // delete slider
      MockHttpRequest mockDeleteHttpRequest = MockHttpRequest.delete("/slider/delete/" + sliderDTO2.getId());
      addCredential(mockDeleteHttpRequest);
      MockHttpResponse mockDeleteHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockDeleteHttpRequest, mockDeleteHttpResponse);
      assertEquals(200, mockDeleteHttpResponse.getStatus());
   }
   
   public void testLoadAllSliders() throws Exception {
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
      
      // load all sliders
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(SliderRESTTestService.class);
      MockHttpRequest mockLoadAllHttpRequest = MockHttpRequest.get("/slider/loadall/1");
      mockLoadAllHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadAllHttpRequest);
      MockHttpResponse mockLoadAllHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadAllHttpRequest, mockLoadAllHttpResponse);
      
      String slidersJson = mockLoadAllHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      SliderListing sliderList = mapper.readValue(slidersJson, SliderListing.class);
      assertEquals(1, sliderList.getSliders().size());
   }
   
   private DeviceDTO saveDeviceWithSwitchSensor() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(DeviceRESTTestService.class);
      // save device with content
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/device/savewithcontent/1");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      String postData = FixtureUtil.getFileContent("dtos/device/device_with_colorsensor.json");
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
