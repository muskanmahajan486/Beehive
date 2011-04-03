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

import org.openremote.beehive.api.dto.modeler.SliderCommandRefDTO;
import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.api.dto.modeler.SliderSensorRefDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.BusinessEntity;

/**
 * The domain class represent a slider entity.
 * It contains a command and a sensor.
 */
@Entity
@Table(name = "slider")
public class Slider extends BusinessEntity {

   private static final long serialVersionUID = -2548147663772936911L;
   private String name;
   private SliderCommandRef setValueCmd;
   private SliderSensorRef sliderSensorRef;
   private Account account;
   private Device device;
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @OneToOne(mappedBy = "slider", cascade = CascadeType.ALL)
   public SliderCommandRef getSetValueCmd() {
      return setValueCmd;
   }

   public void setSetValueCmd(SliderCommandRef setValueCmd) {
      this.setValueCmd = setValueCmd;
   }

   @ManyToOne
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   @OneToOne(mappedBy = "slider", cascade = CascadeType.ALL)
   public SliderSensorRef getSliderSensorRef() {
      return sliderSensorRef;
   }

   public void setSliderSensorRef(SliderSensorRef sliderSensorRef) {
      this.sliderSensorRef = sliderSensorRef;
   }

   @ManyToOne
   public Device getDevice() {
      return device;
   }

   public void setDevice(Device device) {
      this.device = device;
   }
   
   public SliderDTO toDTO() {
      SliderDTO sliderDTO = new SliderDTO();
      sliderDTO.setId(getOid());
      sliderDTO.setName(name);
      String deviceName = null;
      if (device != null) {
         sliderDTO.setDevice(device.toSimpleDTO());
         deviceName = device.getName();
      }
      
      if (setValueCmd != null) {
         sliderDTO.setSetValueCmd(new SliderCommandRefDTO(setValueCmd, deviceName));
      }
      if (sliderSensorRef != null) {
         sliderDTO.setSliderSensorRef(new SliderSensorRefDTO(sliderSensorRef));
      }
      return sliderDTO;
   }
   
   /**
    * Equals without compare oid.
    * Used for rebuilding from template.
    * 
    * @param other the other
    * 
    * @return true, if successful
    */
   public boolean equalsWithoutCompareOid(Slider other) {
      if (other == null) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (this.device != null) {
         if (other.device == null || this.device.getOid() != other.device.getOid()) return false;
      } else if (other.device != null) return false;
      if (this.setValueCmd != null) {
         if (other.setValueCmd == null || !this.setValueCmd.equalsWithoutCompareOid(other.getSetValueCmd())) return false;
      } else if (other.setValueCmd != null)  return false;
      if (this.sliderSensorRef != null) {
         if (other.sliderSensorRef == null || !sliderSensorRef.equalsWithoutCompareOid(other.sliderSensorRef)) return false;
      } else if(other.sliderSensorRef != null) return false;
      return true;
   }
}
