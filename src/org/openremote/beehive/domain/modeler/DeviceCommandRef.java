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
package org.openremote.beehive.domain.modeler;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.openremote.beehive.api.dto.modeler.DeviceCommandRefDTO;
import org.openremote.beehive.api.dto.modeler.DeviceMacroItemDTO;


/**
 * The Class Device Command Reference.
 * 
 * @author Dan 2009-7-6
 */
@Entity
@DiscriminatorValue("DEVICE_CMD_REF")
public class DeviceCommandRef extends DeviceMacroItem {
   
   private static final long serialVersionUID = 3969930759279661982L;

   /** The device command. */
   private DeviceCommand deviceCommand;
   
   /**
    * Instantiates a new device command ref.
    */
   public DeviceCommandRef() {
   }
   
   /**
    * Instantiates a new device command ref.
    * 
    * @param deviceCommand the device command
    */
   public DeviceCommandRef(DeviceCommand deviceCommand) {
      super();
      this.deviceCommand = deviceCommand;
   }

   /**
    * Gets the device command.
    * 
    * @return the device command
    */
   @OneToOne
   @JoinColumn(name = "target_device_cmd_oid")
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   /**
    * Sets the device command.
    * 
    * @param deviceCommand the new device command
    */
   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }
   
   @Override
   public DeviceMacroItemDTO toDTO() {
      DeviceCommandRefDTO deviceMacroItemDTO = new DeviceCommandRefDTO();
      deviceMacroItemDTO.setId(getOid());
      DeviceMacro parentDeviceMacro = getParentDeviceMacro();
      if (parentDeviceMacro != null) {
         deviceMacroItemDTO.setParentDeviceMacro(parentDeviceMacro.toSimpleDTO());
      }
      if (deviceCommand != null) {
         deviceMacroItemDTO.setDeviceCommand(deviceCommand.toDTO());
         deviceMacroItemDTO.setDeviceName(deviceCommand.getDevice().getName());
      }
         
      return deviceMacroItemDTO;
   }
}
