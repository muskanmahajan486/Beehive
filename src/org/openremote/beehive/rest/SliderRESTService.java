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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.api.service.SliderService;

/**
 * Exports restful service of <code>Slider</code>
 */
@Path("/slider")
public class SliderRESTService extends RESTBaseService {

   /**
    * Create a new slider under an account by {account_id}.
    * Visits @ url "/slider/save/{account_id}"
    * 
    * @param accountId
    * @param sliderDTO the posted slider data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the saved slider with specified id, and with device information.
    */
   @Path("save/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response save(@PathParam("account_id") long accountId, SliderDTO sliderDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SliderDTO newDTO = getSliderService().save(sliderDTO, accountId);
      newDTO.getSetValueCmd().getDeviceCommand().setDevice(sliderDTO.getDevice());
      newDTO.getSliderSensorRef().getSensor().setDevice(sliderDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   /**
    * Delete a slider by {slider_id}.
    * Visits @ url "/slider/delete/{slider_id}"
    * 
    * @param sliderId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return 200 or 500.
    */
   @Path("delete/{slider_id}")
   @DELETE
   public Response delete(@PathParam("slider_id") long sliderId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getSliderService().deleteById(sliderId);
      return buildResponse(true);
   }
   
   /**
    * Update slider properties to database by posted sliderDTO.
    * Visits @ url "/slider/update"
    * 
    * @param sliderDTO the posted slider data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the updated sliderDTO with device information.
    */
   @Path("update")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response update(SliderDTO sliderDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SliderDTO newDTO = getSliderService().update(sliderDTO).toDTO();
      newDTO.getSetValueCmd().getDeviceCommand().setDevice(sliderDTO.getDevice());
      newDTO.getSliderSensorRef().getSensor().setDevice(sliderDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   /**
    * Show all sliders under an account by {account_id}.
    * Visits @ url "/slider/loadall/{account_id}"
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of sliderDTOs.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountSwitchs(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SliderDTO> sliders = getSliderService().loadAccountSliders(accountId);
      if (sliders.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new SliderListing(sliders));
   }
   
   /**
    * Show a list of sliderDTOs under an account, each of them has the same properties with the specified sliderDTO except id.
    * Visits @ url "/slider/loadsamesliders"
    * 
    * @param sliderDTO the specified slider data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of sldierDTOs.
    */
   @Path("loadsamesliders")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadSameSliders(SliderDTO sliderDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SliderDTO> sliders = getSliderService().loadSameSliders(sliderDTO);
      if (sliders.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new SliderListing(sliders));
   }
   
   /**
    * Retrieves instance of SliderService from spring IOC
    * 
    * @return SliderService instance
    */
   protected SliderService getSliderService() {
      return (SliderService) getSpringContextInstance().getBean("sliderService");
   }
}
