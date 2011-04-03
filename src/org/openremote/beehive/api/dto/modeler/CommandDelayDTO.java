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
import org.openremote.beehive.domain.modeler.CommandDelay;
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.domain.modeler.DeviceMacroItem;

/**
 * The Class is used for transmitting command delay info.
 *
 * @author tomsky
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "CommandDelay")
@XmlRootElement(name="commandDelay")
public class CommandDelayDTO extends DeviceMacroItemDTO {

   private String delaySecond;

   public String getDelaySecond() {
      return delaySecond;
   }

   public void setDelaySecond(String delaySecond) {
      this.delaySecond = delaySecond;
   }
   
   @Override
   public DeviceMacroItem toDeviceMacroItem(DeviceMacro deviceMacro) {
      CommandDelay deviceMacroItem = new CommandDelay();
      deviceMacroItem.setParentDeviceMacro(deviceMacro);
      deviceMacroItem.setDelaySecond(delaySecond);
      return deviceMacroItem;
   }
}
