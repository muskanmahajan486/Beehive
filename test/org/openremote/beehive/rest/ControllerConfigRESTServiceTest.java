/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2010, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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
import org.openremote.beehive.api.dto.modeler.ControllerConfigDTO;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.modeler.ControllerConfig;
import org.openremote.beehive.rest.service.ControllerConfigRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;
import org.openremote.beehive.utils.RESTTestUtils;

/**
 * The Class ControllerConfigRESTServiceTest.
 */
public class ControllerConfigRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");

   /**
    * Load all controllerConfigs under an account
    * 
    * @throws URISyntaxException
    * @throws JsonParseException
    * @throws JsonMappingException
    * @throws IOException
    */
   public void testLoadAllConfigs() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {

      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerConfigRESTTestService.class);
      saveDefaultConfigs(dispatcher);

      MockHttpRequest mockLoadAllHttpRequest = MockHttpRequest.get("/controllerconfig/loadall/1");
      mockLoadAllHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockLoadAllHttpRequest);
      MockHttpResponse mockLoadAllHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockLoadAllHttpRequest, mockLoadAllHttpResponse);

      String dbControllerConfigsJson = mockLoadAllHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      ControllerConfigListing controllerConfigs = mapper.readValue(dbControllerConfigsJson,
            ControllerConfigListing.class);
      assertEquals(14, controllerConfigs.getControllerConfigs().size());
   }

   /**
    * Load controllerConfigs under an account by categoryName
    * 
    * @throws URISyntaxException
    * @throws IOException
    * @throws JsonMappingException
    * @throws JsonParseException
    */
   public void testLoadAccountConfigsByCategoryName() throws URISyntaxException, JsonParseException, JsonMappingException,
         IOException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerConfigRESTTestService.class);
      saveDefaultConfigs(dispatcher);

      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/controllerconfig/load/1/macro");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      String dbControllerConfigsJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      ControllerConfigListing controllerConfigs = mapper.readValue(dbControllerConfigsJson,
            ControllerConfigListing.class);
      assertEquals(1, controllerConfigs.getControllerConfigs().size());
   }

   /**
    * Save all controllerConfigs under account 1 and category roundrobin
    * 
    * @throws Exception
    */
   public void testSaveAllRoundrobinControllerConfigs() throws Exception {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerConfigRESTTestService.class);
      saveDefaultConfigs(dispatcher);

      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/controllerconfig/load/1/roundrobin");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      super.tearDown();
      super.setUp();
      
      String dbControllerConfigsJson = mockHttpResponse.getContentAsString();
      ObjectMapper mapper = new ObjectMapper();
      ControllerConfigListing controllerConfigs = mapper.readValue(dbControllerConfigsJson,
            ControllerConfigListing.class);
      List<ControllerConfigDTO> controllerConfigDTOs = controllerConfigs.getControllerConfigs();
      assertEquals(5, controllerConfigDTOs.size());
      long id1 = controllerConfigDTOs.get(0).getId();
      long id2 = controllerConfigDTOs.get(1).getId();
      long id3 = controllerConfigDTOs.get(2).getId();
      long id4 = controllerConfigDTOs.get(3).getId();
      long id5 = controllerConfigDTOs.get(4).getId();
      
      String controllerConfigJson = FixtureUtil.getFileContent("dtos/controller_config/controllerconfig_roundrobin.json");
      String postData = controllerConfigJson.replace("\"id\":85", "\"id\":" + id1)
            .replace("\"id\":87", "\"id\":" + id2).replace("\"id\":89", "\"id\":" + id3).replace("\"id\":91",
                  "\"id\":" + id4).replace("\"id\":93", "\"id\":" + id5);
      MockHttpRequest mockSaveHttpRequest = MockHttpRequest.post("/controllerconfig/saveall/1");
      mockSaveHttpRequest.accept(MediaType.APPLICATION_JSON);
      mockSaveHttpRequest.contentType(MediaType.APPLICATION_JSON);
      addCredential(mockSaveHttpRequest);
      mockSaveHttpRequest.content(postData.getBytes());
      MockHttpResponse mockSaveHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockSaveHttpRequest, mockSaveHttpResponse);
      assertEquals(200, mockSaveHttpResponse.getStatus());
   }

   /**
    * Save default controllerConfigs to the new account
    * 
    * @param dispatcher
    * @throws URISyntaxException
    */
   private void saveDefaultConfigs(Dispatcher dispatcher) throws URISyntaxException {
      String controllerConfigJson = FixtureUtil.getFileContent("dtos/controller_config/controllerconfig_default.json");
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/controllerconfig/savedefault/1");
      mockHttpRequest.contentType(MediaType.APPLICATION_JSON);
      mockHttpRequest.content(controllerConfigJson.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      assertEquals(200, mockHttpResponse.getStatus());
   }

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      List<ControllerConfig> controllerConfigs = genericDAO.loadAll(ControllerConfig.class);
      genericDAO.deleteAll(controllerConfigs);
   }
}
