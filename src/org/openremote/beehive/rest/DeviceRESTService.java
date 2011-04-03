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
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.DeviceService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.Device;

/**
 * Export restful service to manage device.
 */
@Path("/device")
public class DeviceRESTService extends RESTBaseService {

   /**
    * Create an new simple device under an account, it just includes name,vendor and model.
    * Visits @ url "/device/save/{account_id}"
    * 
    * @param aid the account id.
    * @param deviceDTO the device need to be save into database.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the created device with specified id.
    */
   @Path("save/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response saveDevice(@PathParam("account_id") long aid,
                              DeviceDTO deviceDTO,
                              @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      Account account = getAccountService().getById(aid);
      Device device = deviceDTO.toDevice();
      device.setAccount(account);
      Device dbDevice = getDeviceService().saveDevice(device);
      deviceDTO.setId(dbDevice.getOid());
      return buildResponse(deviceDTO);
   }
   
   /**
    * Create an new device with contents, the contents includes commands, sensors, switchs and sliders.
    * Visits @ url "/device/savewithcontent/{account_id}"
    * 
    * @param aid the account id.
    * @param deviceDTO
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the created device with specified id ant contents.
    */
   @Path("savewithcontent/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response saveDeviceWithContent(@PathParam("account_id") long aid,
         DeviceDTO deviceDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      Account account = getAccountService().getById(aid);
      Device device = deviceDTO.toDeviceWithContents(account);
      Device dbDevice = getDeviceService().saveDeviceWithContent(device);
      return buildResponse(dbDevice.toDTO());
   }
   
   /**
    * Show a list of devices with simple properties under an account, each device includes id, name, vendor and model.
    * Visits @ url "/device/loadall/{account_id}"
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of devices.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountDevices(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceDTO> devices = getDeviceService().loadAllAccountDevices(accountId);
      if (devices.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new DeviceListing(devices));
   }
   
   /**
    * Show a device by {device_id}.
    * Visits @ url "/device/load/{device_id}"
    * 
    * @param deviceId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return DeviceDTO includes commands,sensors,switchs and sliders.
    */
   @Path("load/{device_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadById(@PathParam("device_id") long deviceId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      DeviceDTO device = getDeviceService().loadDeviceById(deviceId);
      return buildResponse(device);
   }
   
   /**
    * Update <code>Device</code> name,vendor and model.
    * Visits @ url "/device/update"
    * 
    * @param deviceDTO JSON formated, include id,name,vendor and model.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return status code 200 or 500.
    */
   @Path("update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public Response update(DeviceDTO deviceDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getDeviceService().update(deviceDTO);
      return buildResponse(true);
   }
   
   /**
    * Delete a device by {device_id}.
    * Visits @ url "/device/delete/{device_id}"
    * 
    * @param deviceId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return status code 200 or 500.
    */
   @Path("delete/{device_id}")
   @DELETE
   public Response delete(@PathParam("device_id") long deviceId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      getDeviceService().delete(deviceId);
      return buildResponse(true);
   }
   
   /**
    * Show a list of devices with same contents except id under an <code>Account</code>.
    * Visits @ url "/device/loadsamedevices/{account_id}"
    * 
    * @param accountId
    * @param deviceDTO the device to be compared, its not specified id.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of devices.
    */
   @Path("loadsamedevices/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadSameDevices(@PathParam("account_id") long accountId,
         DeviceDTO deviceDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<DeviceDTO> devices = getDeviceService().loadSameDevices(deviceDTO, accountId);
      if (devices.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new DeviceListing(devices));
   }
   
   /**
    * Retrieves instance of DeviceService from spring IOC
    * 
    * @return DeviceService instance
    */
   protected DeviceService getDeviceService() {
      return (DeviceService) getSpringContextInstance().getBean("deviceService");
   }
   
   /**
    * Retrieves instance of AccountService from spring IOC
    * 
    * @return AccountService instance
    */
   protected AccountService getAccountService() {
      return (AccountService) getSpringContextInstance().getBean("accountService");
   }
}
