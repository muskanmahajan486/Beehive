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

import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.Switch;

@XmlRootElement(name = "switch")
public class SwitchDTO extends BusinessEntityDTO {

   private static final long serialVersionUID = -513113405416151501L;

   private String name;
   private SwitchCommandOnRefDTO switchCommandOnRef;
   private SwitchCommandOffRefDTO switchCommandOffRef;
   private SwitchSensorRefDTO switchSensorRef;
   private DeviceDTO device;
   
   public SwitchDTO() {
   }
   
   public String getName() {
      return name;
   }
   public SwitchCommandOnRefDTO getSwitchCommandOnRef() {
      return switchCommandOnRef;
   }
   public SwitchCommandOffRefDTO getSwitchCommandOffRef() {
      return switchCommandOffRef;
   }
   public SwitchSensorRefDTO getSwitchSensorRef() {
      return switchSensorRef;
   }
   public DeviceDTO getDevice() {
      return device;
   }
   public void setName(String name) {
      this.name = name;
   }
   public void setSwitchCommandOnRef(SwitchCommandOnRefDTO switchCommandOnRef) {
      this.switchCommandOnRef = switchCommandOnRef;
   }
   public void setSwitchCommandOffRef(SwitchCommandOffRefDTO switchCommandOffRef) {
      this.switchCommandOffRef = switchCommandOffRef;
   }
   public void setSwitchSensorRef(SwitchSensorRefDTO switchSensorRef) {
      this.switchSensorRef = switchSensorRef;
   }
   public void setDevice(DeviceDTO device) {
      this.device = device;
   }
   
   public Switch toSwitch() {
      Switch switchToggle = new Switch();
      switchToggle.setOid(getId());
      switchToggle.setName(name);
      if (device != null) {
         switchToggle.setDevice(device.toDevice());
      }
      if (switchCommandOnRef != null) {
         switchToggle.setSwitchCommandOnRef(switchCommandOnRef.toSwitchCommandOnRef(switchToggle));
      }
      if (switchCommandOffRef != null) {
         switchToggle.setSwitchCommandOffRef(switchCommandOffRef.toSwitchCommandOffRef(switchToggle));
      }
      if (switchSensorRef != null) {
         switchToggle.setSwitchSensorRef(switchSensorRef.toSwitchSensorRef(switchToggle));
      }
      return switchToggle;
   }
   
   public Switch toSimpleSwitch() {
      Switch switchToggle = new Switch();
      switchToggle.setOid(getId());
      switchToggle.setName(name);
      return switchToggle;
   }
}
