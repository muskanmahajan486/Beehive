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
package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.LIRCTestBase;
import org.openremote.beehive.rest.service.RemoteSectionRESTTestService;
import org.openremote.beehive.utils.RESTTestUtils;

public class RemoteSectionRESTServiceTest extends LIRCTestBase {
   
   public void testGetRemoteSectionsXml() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(RemoteSectionRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m/MP8640");
      mockHttpRequest.accept(MediaType.APPLICATION_XML);

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());

   }

   public void testGetRemoteSectionsJson() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(RemoteSectionRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m/MP8640");
      mockHttpRequest.accept(MediaType.APPLICATION_JSON);

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());

   }

}
