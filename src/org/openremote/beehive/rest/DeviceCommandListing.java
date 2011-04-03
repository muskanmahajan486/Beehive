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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;

/**
 * In order to let rest service to serialize list of DeviceCommands.
 */
@XmlRootElement(name = "deviceCommands")
public class DeviceCommandListing {

   private List<DeviceCommandDTO> deviceCommands = new ArrayList<DeviceCommandDTO>();
   
   public DeviceCommandListing() {
   }
   
   public DeviceCommandListing(List<DeviceCommandDTO> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }
   
   @XmlElement(name = "deviceCommand")
   public List<DeviceCommandDTO> getDeviceCommands() {
      return deviceCommands;
   }
   
   public void setDeviceCommands(List<DeviceCommandDTO> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }
}
