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
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.domain.modeler.DeviceMacroItem;
import org.openremote.beehive.domain.modeler.DeviceMacroRef;

/**
 * The Class is used for transmitting device macro ref info.
 *
 * @author tomsky
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "DeviceMacroRef")
@XmlRootElement(name="deviceMacroRef")
public class DeviceMacroRefDTO extends DeviceMacroItemDTO {

   private DeviceMacroDTO targetDeviceMacro;

   public DeviceMacroDTO getTargetDeviceMacro() {
      return targetDeviceMacro;
   }

   public void setTargetDeviceMacro(DeviceMacroDTO targetDeviceMacro) {
      this.targetDeviceMacro = targetDeviceMacro;
   }
   
   @Override
   public DeviceMacroItem toDeviceMacroItem(DeviceMacro deviceMacro) {
      DeviceMacroRef deviceMacroItem = new DeviceMacroRef();
      deviceMacroItem.setParentDeviceMacro(deviceMacro);
      if (targetDeviceMacro != null) {
         deviceMacroItem.setTargetDeviceMacro(targetDeviceMacro.toDeviceMacro());
      }
      return deviceMacroItem;
   }
}
