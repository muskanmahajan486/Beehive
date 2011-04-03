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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.modeler.SwitchCommandOffRefDTO;
import org.openremote.beehive.api.dto.modeler.SwitchCommandOnRefDTO;
import org.openremote.beehive.api.dto.modeler.SwitchDTO;
import org.openremote.beehive.api.dto.modeler.SwitchSensorRefDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.BusinessEntity;

/**
 * It represents a switch entity, includes sensor, on command and off command.
 */
@Entity
@Table(name = "switch")
public class Switch extends BusinessEntity {

   private static final long serialVersionUID = -658135522907195149L;
   
   private String name;
   private SwitchCommandOnRef switchCommandOnRef;
   private SwitchCommandOffRef switchCommandOffRef;
   private SwitchSensorRef switchSensorRef;
   private Account account;
   private Device device;
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(mappedBy = "offSwitch", cascade = CascadeType.ALL)
   public SwitchCommandOffRef getSwitchCommandOffRef() {
      return switchCommandOffRef;
   }

   public void setSwitchCommandOffRef(SwitchCommandOffRef switchCommandOffRef) {
      this.switchCommandOffRef = switchCommandOffRef;
   }


   @OneToOne(mappedBy = "onSwitch", cascade = CascadeType.ALL)
   public SwitchCommandOnRef getSwitchCommandOnRef() {
      return switchCommandOnRef;
   }

   public void setSwitchCommandOnRef(SwitchCommandOnRef switchCommandOnRef) {
      this.switchCommandOnRef = switchCommandOnRef;
   }

   @OneToOne(mappedBy = "switchToggle", cascade = CascadeType.ALL)
   public SwitchSensorRef getSwitchSensorRef() {
      return switchSensorRef;
   }


   public void setSwitchSensorRef(SwitchSensorRef switchSensorRef) {
      this.switchSensorRef = switchSensorRef;
   }


   @ManyToOne
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   @ManyToOne
   public Device getDevice() {
      return device;
   }

   public void setDevice(Device device) {
      this.device = device;
   }

   public SwitchDTO toDTO() {
      SwitchDTO switchDTO = new SwitchDTO();
      switchDTO.setId(getOid());
      switchDTO.setName(name);
      String deviceName = null;
      if (device != null) {
         switchDTO.setDevice(device.toSimpleDTO());
         deviceName = device.getName();
      }
      
      if (switchCommandOnRef != null) {
         switchDTO.setSwitchCommandOnRef(new SwitchCommandOnRefDTO(switchCommandOnRef, deviceName));
      }
      if (switchCommandOffRef != null) {
         switchDTO.setSwitchCommandOffRef(new SwitchCommandOffRefDTO(switchCommandOffRef, deviceName));
      }
      if (switchSensorRef != null) {
         switchDTO.setSwitchSensorRef(new SwitchSensorRefDTO(switchSensorRef));
      }
      
      return switchDTO;
   }
   
   /**
    * Equals without compare oid.
    * Used for rebuilding from template.
    * 
    * @param swh the swh
    * 
    * @return true, if successful
    */
   public boolean equalsWithoutCompareOid(Switch swh) {
      if (name == null) {
         if (swh.name != null) return false;
      } else if (!name.equals(swh.name)) return false;
      if (switchCommandOffRef == null) {
         if (swh.switchCommandOffRef != null) return false;
      } else if (swh.switchCommandOffRef == null || !switchCommandOffRef.equalsWithoutCompareOid(swh.switchCommandOffRef)) return false;
      if (switchCommandOnRef == null) {
         if (swh.switchCommandOnRef != null) return false;
      } else if (swh.switchCommandOnRef == null || !switchCommandOnRef.equalsWithoutCompareOid(swh.switchCommandOnRef)) return false;
      if (switchSensorRef == null) {
         if (swh.switchSensorRef != null) return false;
      } else if (swh.switchSensorRef == null || !switchSensorRef.equalsWithoutCompareOid(swh.switchSensorRef)) return false;
      return true;
   }
}
