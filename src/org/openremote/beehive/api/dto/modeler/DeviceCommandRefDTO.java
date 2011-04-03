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
package org.openremote.beehive.api.dto.modeler;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.openremote.beehive.domain.modeler.DeviceCommandRef;
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.domain.modeler.DeviceMacroItem;

/**
 * The Class is used for transmitting device command ref info.
 *
 * @author tomsky
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "DeviceCommandRef")
@XmlRootElement(name="deviceCommandRef")
public class DeviceCommandRefDTO extends DeviceMacroItemDTO {

   private DeviceCommandDTO deviceCommand;
   
   private String deviceName;

   public DeviceCommandDTO getDeviceCommand() {
      return deviceCommand;
   }

   public void setDeviceCommand(DeviceCommandDTO deviceCommand) {
      this.deviceCommand = deviceCommand;
   }

   public String getDeviceName() {
      return deviceName;
   }

   public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
   }
   
   @Override
   public DeviceMacroItem toDeviceMacroItem(DeviceMacro deviceMacro) {
      DeviceCommandRef deviceMacroItem = new DeviceCommandRef();
      deviceMacroItem.setParentDeviceMacro(deviceMacro);
      if (deviceCommand != null) {
         deviceMacroItem.setDeviceCommand(deviceCommand.toDeviceCommand());
      }
      return deviceMacroItem;
   }
}
