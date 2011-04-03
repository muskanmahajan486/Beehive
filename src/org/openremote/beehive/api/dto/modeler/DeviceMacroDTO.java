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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.AccountDTO;
import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.DeviceMacro;

/**
 * The Class is used for transmitting device macro info.
 *
 * @author tomsky
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "deviceMacro")
public class DeviceMacroDTO extends BusinessEntityDTO {

   private List<DeviceMacroItemDTO> deviceMacroItems = new ArrayList<DeviceMacroItemDTO>();
   private String name;
   private AccountDTO account;
   
   @XmlElementWrapper(name="deviceMacroItems")
   @XmlElementRef(type=DeviceMacroItemDTO.class)
   public List<DeviceMacroItemDTO> getDeviceMacroItems() {
      return deviceMacroItems;
   }
   public void setDeviceMacroItems(List<DeviceMacroItemDTO> deviceMacroItems) {
      this.deviceMacroItems = deviceMacroItems;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public AccountDTO getAccount() {
      return account;
   }
   public void setAccount(AccountDTO account) {
      this.account = account;
   }
   public void addDeviceMacroItem(DeviceMacroItemDTO deviceMacroItemDTO) {
      deviceMacroItems.add(deviceMacroItemDTO);
   }
   
   public DeviceMacro toDeviceMacro() {
      DeviceMacro deviceMacro = new DeviceMacro();
      deviceMacro.setName(name);
      deviceMacro.setOid(getId());
      return deviceMacro;
   }
   
   public DeviceMacro toDeviceMacroWithContent(Account dbAccount) {
      DeviceMacro deviceMacro = toDeviceMacro();
      deviceMacro.setAccount(dbAccount);
      if (deviceMacroItems != null) {
         for (DeviceMacroItemDTO deviceMacroItemDTO : deviceMacroItems) {
            deviceMacro.addDeviceMacroItem(deviceMacroItemDTO.toDeviceMacroItem(deviceMacro));
         }
      }
      return deviceMacro;
   }
   
}
