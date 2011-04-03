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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.DeviceCommand;

/**
 * The Class is used for transmitting device command info.
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "deviceCommand")
public class DeviceCommandDTO extends BusinessEntityDTO {

   private DeviceDTO device;
   private String name;
   private String sectionId;
   private ProtocolDTO protocol;
   
   @XmlElement(name="device")
   public DeviceDTO getDevice() {
      return device;
   }
   public String getName() {
      return name;
   }
   public String getSectionId() {
      return sectionId;
   }
   public void setDevice(DeviceDTO device) {
      this.device = device;
   }
   public void setName(String name) {
      this.name = name;
   }
   public void setSectionId(String sectionId) {
      this.sectionId = sectionId;
   }
   @XmlElement(name="protocol")
   public ProtocolDTO getProtocol() {
      return protocol;
   }
   public void setProtocol(ProtocolDTO protocol) {
      this.protocol = protocol;
   }
   
   public DeviceCommand toDeviceCommand() {
      DeviceCommand deviceCommand = new DeviceCommand();
      deviceCommand.setOid(getId());
      deviceCommand.setName(name);
      deviceCommand.setSectionId(sectionId);
      if (device != null) {
         deviceCommand.setDevice(device.toDevice());
      }
      if (protocol != null) {
         deviceCommand.setProtocol(protocol.toProtocol());
      }
      return deviceCommand;
   }
}
