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
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.domain.modeler.DeviceMacroItem;

/**
 * The Class is used for transmitting device macro item info.
 *
 * @author tomsky
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="classType")
@JsonTypeName(value = "DeviceMacroItem")
@JsonSubTypes(value={@Type(value=DeviceCommandRefDTO.class), @Type(value=DeviceMacroRefDTO.class), @Type(CommandDelayDTO.class)})
@XmlRootElement(name="deviceMacroItem")
@XmlSeeAlso(value = { DeviceCommandRefDTO.class, DeviceMacroRefDTO.class, CommandDelayDTO.class})
public class DeviceMacroItemDTO extends UICommandDTO {

   private DeviceMacroDTO parentDeviceMacro;

   public DeviceMacroDTO getParentDeviceMacro() {
      return parentDeviceMacro;
   }

   public void setParentDeviceMacro(DeviceMacroDTO parentDeviceMacro) {
      this.parentDeviceMacro = parentDeviceMacro;
   }
   
   public DeviceMacroItem toDeviceMacroItem(DeviceMacro deviceMacro) {
      DeviceMacroItem deviceMacroItem = new DeviceMacroItem();
      deviceMacroItem.setParentDeviceMacro(deviceMacro);
      return deviceMacroItem;
   }
}
