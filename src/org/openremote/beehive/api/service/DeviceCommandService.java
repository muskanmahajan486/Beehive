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

import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.domain.modeler.DeviceCommand;

/**
 * Business service for <code>DeviceCommandDTO</code>.
 */
public interface DeviceCommandService {

   /**
    * Load <code>DeviceCommandDTo</code> by deviceCommand id.
    * 
    * @param id the deviceCommand id.
    * @return the deviceCommandDTO, its includes protocol and protocolAttrs.
    */
   DeviceCommandDTO loadDeviceCommandById(long id);
   
   /**
    * Save <code>DeviceCommandDTO</code> into database.
    * 
    * @param deviceCommandDTO
    * @return the deviceCommandDTO with specified id.
    */
   DeviceCommandDTO save(DeviceCommandDTO deviceCommandDTO);
   
   /**
    * Save a list of deviceCommandDTOs into database.
    * 
    * @param deviceCommandDTOs
    * @return the saved deviceCommandDTOs with specified ids.
    */
   List<DeviceCommand> saveAll(List<DeviceCommandDTO> deviceCommandDTOs);
   
   /**
    * Delete a deviceCommand by its id.
    * 
    * @param id the deviceCommand id
    * @return true or false.
    */
   Boolean deleteCommandById(long id);
   
   /**
    * Update <code>DeviceCommand</code> name and protocol.
    * 
    * @param deviceCommandDTO
    * @return the updated DeviceCommand
    */
   DeviceCommand update(DeviceCommandDTO deviceCommandDTO);
   
   /**
    * Load a list of deviceCommands under a device by deviceId.
    * 
    * @param id the deviceId.
    * @return a list of DeviceCommands.
    */
   List<DeviceCommand> loadCommandsByDeviceId(long id);
   
   /**
    * Load a list of deviceCommands, each of them has same properties with the specified deviceCommandDTO except id.
    * 
    * @param deviceCommandDTO
    * @return a list of DeviceCommands.
    */
   List<DeviceCommand> loadSameDeviceCommands(DeviceCommandDTO deviceCommandDTO);
   
}
