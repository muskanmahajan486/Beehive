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

import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.domain.modeler.Device;

/**
 * The service for managing devices.
 */
public interface DeviceService {

   /**
    * Save a simple device into database.
    *
    * @param device the device not includes command/sensor/switch/slider.
    * @return the device with id.
    */
   Device saveDevice(Device device);
   
   /**
    * Save device with content, the content includes command/sensor/switch/slider.
    * 
    * @param device
    * @return the device and its content with id.
    */
   Device saveDeviceWithContent(Device device);
   
   /**
    * Load all simple devices under an <code>Account</code>.
    * 
    * @param accountId
    * @return a list of DeviceDTOs, the DeviceDTO includes id,name,vendor and model.
    */
   List<DeviceDTO> loadAllAccountDevices(long accountId);
   
   /**
    * Load <code>DeviceDTO</code> by deviceId.
    * 
    * @param deviceId
    * @return DeviceDTO includes commands,sensors,switchs and sliders.
    */
   DeviceDTO loadDeviceById(long deviceId);
   
   /**
    * Update <code>Device</code> name,vendor and model.
    * 
    * @param deviceDTO received from client.
    */
   void update(DeviceDTO deviceDTO);
   
   /**
    * Delete device by deviceId.
    * 
    * @param deviceId
    */
   void delete(long deviceId);
   
   /**
    * Load same Devices from database, each of them has the same properties with <code>DeviceDTO</code>. 
    * 
    * @param deviceDTO received from client, it has not specified id.
    * @param accountId
    * @return a list of DeviceDTOs.
    */
   List<DeviceDTO> loadSameDevices(DeviceDTO deviceDTO, long accountId);
   
}
