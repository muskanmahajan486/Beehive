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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.Protocol;

/**
 * The Class is used for transmitting protocol info.
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "protocol")
public class ProtocolDTO extends BusinessEntityDTO {

   private String type;
   private List<ProtocolAttrDTO> attributes;
   
   public String getType() {
      return type;
   }
   @XmlElementWrapper(name = "attributes")
   @XmlElement(name="attribute")
   public List<ProtocolAttrDTO> getAttributes() {
      return attributes;
   }
   public void setType(String type) {
      this.type = type;
   }
   public void setAttributes(List<ProtocolAttrDTO> attributes) {
      this.attributes = attributes;
   }
   
   public Protocol toProtocol() {
      Protocol protocol = new Protocol();
      protocol.setOid(getId());
      protocol.setType(type);
      if (attributes != null) {
         for (ProtocolAttrDTO attr : attributes) {
            protocol.addProtocolAttr(attr.toProtocolAttr(protocol));
         }
      }
      return protocol;
   }
}
