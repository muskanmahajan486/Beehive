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
import org.openremote.beehive.api.dto.modeler.DeviceMacroDTO;
import org.openremote.beehive.api.dto.modeler.DeviceMacroItemDTO;
import org.openremote.beehive.api.service.DeviceMacroService;

/**
 * Exports restful service of <code>DeviceMacro</code>.
 * 
 * @author tomsky
 *
 */
@Path("/devicemacro")
public class DeviceMacroRESTService extends RESTBaseService {

   /**
    * Create a new deviceMacro under an account by {account_id}.
    * Visits @ url "/devicemacro/save/{account_id}"
    * 
    * @param accountId
    * @param deviceMacroDTO
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the saved deviceMacroDTO with specified id.
    */
   @Path("save/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response save(@PathParam("account_id") long accountId, DeviceMacroDTO deviceMacroDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceMacroDTO newDTO = getDeviceMacroService().save(deviceMacroDTO, accountId);
      return buildResponse(newDTO);
   }
   
   /**
    * Show deviceMacroItems under a deviceMacro by {macro_id}.
    * Visits @ url "/devicemacro/loaditemsbyid/{macro_id}"
    * 
    * @param macroId the deviceMacro id.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of deviceMacroItemDTOs.
    */
   @Path("loaditemsbyid/{macro_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadDeviceMacroItems(@PathParam("macro_id") long macroId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceMacroItemDTO> deviceMacroItemDTOs = getDeviceMacroService().loadDeviceMacroItems(macroId);
      if (deviceMacroItemDTOs.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new DeviceMacroItemListing(deviceMacroItemDTOs));
   }
   
   /**
    * Show all deviceMacros under an account.
    * Visits @ url "/devicemacro/loadall/{account_id}"
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of JSON formated deviceMacroDTOs.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountMacros(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceMacroDTO> deviceMacros = getDeviceMacroService().loadAccountDeviceMacros(accountId);
      if (deviceMacros.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new DeviceMacroListing(deviceMacros));
   }
   
   /**
    * Delete a deviceMacro by {macro_id}.
    * Visits @ url "/devicemacro/delete/{macro_id}"
    * 
    * @param macroId the deviceMacro id.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return true or false.
    */
   @Path("delete/{macro_id}")
   @DELETE
   public Response delete(@PathParam("macro_id") long macroId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getDeviceMacroService().deleteDeviceMacro(macroId);
      return buildResponse(true);
   }
   
   /**
    * Update deviceMacro properties to database by posted deviceMacroDTO.
    * Visits @ url "/devicemacro/update"
    * 
    * @param deviceMacroDTO the posted JSON formated deviceMacro data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the updated JSON formated deviceMacroDTO.
    */
   @Path("update")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response update(DeviceMacroDTO deviceMacroDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceMacroDTO newDTO = getDeviceMacroService().updateDeviceMacro(deviceMacroDTO);
      return buildResponse(newDTO);
   }
   
   /**
    * Show a list of deviceMacros with same contents except id under an <code>Account</code>.
    * Visits @ url "/devicemacro/loadsamemacros/{account_id}"
    * 
    * @param accountId
    * @param deviceMacroDTO the deviceMacro to be compared, its not specified id.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of deviceMacros.
    */
   @Path("loadsamemacros/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadSameDeviceMacros(@PathParam("account_id") long accountId,
         DeviceMacroDTO deviceMacroDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceMacroDTO> deviceMacroDTOs = getDeviceMacroService().loadSameDeviceMacros(deviceMacroDTO, accountId);
      if (deviceMacroDTOs.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new DeviceMacroListing(deviceMacroDTOs));
   }
   /**
    * Retrieves instance of DeviceMacroService from spring IOC
    * 
    * @return DeviceMacroService instance
    */
   protected DeviceMacroService getDeviceMacroService() {
      return (DeviceMacroService) getSpringContextInstance().getBean("deviceMacroService");
   }
}
