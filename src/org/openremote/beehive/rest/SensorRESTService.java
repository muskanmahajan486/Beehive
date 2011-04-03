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
import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.api.service.SensorService;

/**
 * Exports restful service of <code>Sensor</code>
 */
@Path("/sensor")
public class SensorRESTService extends RESTBaseService {

   /**
    * Create a new sensor under an account by {account_id}.
    * 
    * @param accountId
    * @param sensorDTO the posted sensor data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the saved sensor with specified id.
    */
   @Path("save/{account_id}")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response save(@PathParam("account_id") long accountId, SensorDTO sensorDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SensorDTO newDTO = getSensorService().save(sensorDTO, accountId);
      newDTO.getSensorCommandRef().getDeviceCommand().setDevice(sensorDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   /**
    * Delete a sensor by {sensor_id}.
    * 
    * @param sensorId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return true or false.
    */
   @Path("delete/{sensor_id}")
   @DELETE
   public Response delete(@PathParam("sensor_id") long sensorId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      return buildResponse(getSensorService().deleteSensorById(sensorId));
   }
   
   /**
    * Update sensor properties with database by posted sensorDTO.
    * 
    * @param sensorDTO the posted sensor data.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the updated sensorDTO.
    */
   @Path("update")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response update(SensorDTO sensorDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SensorDTO newDTO = getSensorService().updateSensor(sensorDTO).toDTO();
      newDTO.getSensorCommandRef().getDeviceCommand().setDevice(sensorDTO.getDevice());
      return buildResponse(newDTO);
   }
   
   /**
    * Show a list of sensorDTOs under an account.
    * 
    * @param accountId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of sensorDTOs.
    */
   @Path("loadall/{account_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadAccountSensors(@PathParam("account_id") long accountId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SensorDTO> sensors = getSensorService().loadAllAccountSensors(accountId);
      if (sensors.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new SensorListing(sensors));
   }
   
   /**
    * Show sensorDTO by {sensor_id}.
    * 
    * @param sensorId
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return the sensorDTO.
    */
   @Path("load/{sensor_id}")
   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadById(@PathParam("sensor_id") long sensorId,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      SensorDTO sensorDTO = getSensorService().loadSensorById(sensorId);
      return buildResponse(sensorDTO);
   }
   
   /**
    * Show a list of sensorDTOs, each of them has same properties with the specified sensorDTO.
    * 
    * @param sensorDTO the specified sensorDTO.
    * @param credentials the Base64 encoded username and password, format is "username:password".
    * @return a list of sensorDTOs.
    */
   @Path("loadsamesensors")
   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public Response loadSameSensors(SensorDTO sensorDTO,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
      if (!authorize(credentials)) return unAuthorizedResponse();
      List<SensorDTO> sensors = getSensorService().loadSameSensors(sensorDTO);
      if (sensors.size() == 0) {
         return resourceNotFoundResponse();
      }
      return buildResponse(new SensorListing(sensors));
   }
   
   /**
    * Retrieves instance of SensorService from spring IOC
    * 
    * @return SensorService instance
    */
   protected SensorService getSensorService() {
      return (SensorService) getSpringContextInstance().getBean("sensorService");
   }
}
