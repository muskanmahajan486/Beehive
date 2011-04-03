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

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.modeler.DeviceMacroItemDTO;

/**
 * The Class DeviceMacroItemList is for storing deviceMacroItems in a deviceMacro.
 *
 * @author tomsky
 */
@XmlRootElement(name = "deviceMacroItems")
public class DeviceMacroItemListing {

   private List<DeviceMacroItemDTO> deviceMacroItems = new ArrayList<DeviceMacroItemDTO>();

   public DeviceMacroItemListing() {
   }
   
   public DeviceMacroItemListing(List<DeviceMacroItemDTO> deviceMacroItems) {
      this.deviceMacroItems = deviceMacroItems;
   }
   
   @XmlElementRef(type=DeviceMacroItemDTO.class)
   public List<DeviceMacroItemDTO> getDeviceMacroItems() {
      return deviceMacroItems;
   }

   public void setDeviceMacroItems(List<DeviceMacroItemDTO> deviceMacroItems) {
      this.deviceMacroItems = deviceMacroItems;
   }
   
   
}
