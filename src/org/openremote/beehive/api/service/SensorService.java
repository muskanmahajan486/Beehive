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
package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.domain.modeler.Sensor;

/**
 * Business service for <code>SensorDTO</code>.
 */
public interface SensorService {

   /**
    * Save a sensorDTO into database.
    * 
    * @param sensorDTO can be all kinds of sensorDTO, includes RangeSensorDTO and CustomSensorDTO now.
    * @param accountId
    * @return the saved sensorDTO with specified id.
    */
   public SensorDTO save(SensorDTO sensorDTO, long accountId);
   
   /**
    * Delete a sensor by sensorId.
    * 
    * @param id the sensorId.
    * @return true or false.
    */
   public boolean deleteSensorById(long id);
   
   /**
    * Update sensor properties with database.
    * As the different sensor has different properties, so we handle them distinctively.
    * 
    * @param sensorDTO
    * @return the updated sensor.
    */
   public Sensor updateSensor(SensorDTO sensorDTO);
   
   /**
    * Load a list of sensors under an account.
    * 
    * @param accountId
    * @return a list of sensors.
    */
   public List<SensorDTO> loadAllAccountSensors(long accountId);
   
   /**
    * Load a sensorDTO by sensorId.
    * 
    * @param id the sensorId.
    * @return the sensorDTO
    */
   SensorDTO loadSensorById(long id);
   
   /**
    * Load a list of sensorDTOs, each of them has same properties with the specified sensorDTO except id.
    * 
    * @param sensorDTO the specified sensorDTO.
    * @return a list of sensorDTOs.
    */
   public List<SensorDTO> loadSameSensors(SensorDTO sensorDTO);
   
}
