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
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.modeler.ControllerConfigDTO;
import org.openremote.beehive.api.service.ControllerConfigService;


/**
 * The Class is for manage controller configurations.
 */
@Path("/controllerconfig")
public class ControllerConfigRESTService extends RESTBaseService {

   /**
    * Save default controllerConfigs under an account, it will be call when create the new Account.
    * Visits @ url "/controllerconfig/savedefault/{account_id}"
    * 
    * @param controllerListing include a list of controllerConfigs.
    * @param aid the account id.
    * @return 200 or 500
    */
   @Path("savedefault/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response saveConfigsForAccount(ControllerConfigListing controllerListing, @PathParam("account_id") long aid) {
      getControllerConfigService().saveDefaultConfigurationsToAccount(controllerListing.getControllerConfigs(), aid);
      return buildResponse(true);
   }
   
   /**
    * 
    * Show a list of controllerConfigs under an account by categoryName.
    * Visits @ url "/controllerconfig/load/{account_id}/{catagory_name}"
    * 
    * @param accountId
    * @param categoryName
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of controllerConfigs.
    */
   @Path("load/{account_id}/{catagory_name}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountConfigsByCategoryName(@PathParam("account_id") long accountId,
                                                @PathParam("catagory_name") String categoryName,
                                                @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<ControllerConfigDTO> configs = getControllerConfigService().loadAccountConfigsByCategoryName(accountId, categoryName);
      return buildResponse(new ControllerConfigListing(configs));
   }
   
   /**
    * Save or update a list of controllerConfigs into an account.
    * Visits @ url "/controllerconfig/saveall/{account_id}"
    * 
    * @param controllerListing
    * @param aid
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of the saved controllerConfigs, each of them with the specified id.
    */
   @Path("saveall/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response saveOrUpdateConfigsForAccount(ControllerConfigListing controllerListing,
                                                @PathParam("account_id") long aid,
                                                @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<ControllerConfigDTO> configs = getControllerConfigService().saveOrUpdateConfigurationsToAccount(controllerListing.getControllerConfigs(), aid);
      return buildResponse(new ControllerConfigListing(configs));
   }
   
   /**
    * Show all controllerConfigs under an account.
    * Visits @ url "/controllerconfig/loadall/{account_id}"
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of controllerConfigs.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountConfigs(@PathParam("account_id") long accountId,
                                                @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<ControllerConfigDTO> configs = getControllerConfigService().loadAccountConfigs(accountId);
      return buildResponse(new ControllerConfigListing(configs));
   }
   
   /**
    * Retrieves instance of ControllerConfigService from spring IOC
    * 
    * @return ControllerConfigService instance
    */
   protected ControllerConfigService getControllerConfigService() {
      return (ControllerConfigService) getSpringContextInstance().getBean("controllerConfigService");
   }
}
