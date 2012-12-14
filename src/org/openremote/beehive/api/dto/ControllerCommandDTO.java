/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.beehive.api.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.ControllerCommand.Type;

/**
 * Base class for the controller commands.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@SuppressWarnings("serial")
public class ControllerCommandDTO extends BusinessEntityDTO {
	
   /**
    * The type of command
    */
	private ControllerCommand.Type type;
	
	/**
	 * The URL to close this command
	 */
	private String closeURL;
	
	/**
	 * The URL to get this command's resource (can be logs directory or 
	 * controller update zip for example)
	 */
   private String resourceURL;

	public ControllerCommandDTO() {
		super();
	}

	public ControllerCommandDTO(ControllerCommand.Type type) {
		this.type = type;
	}

	@XmlAttribute(name = "type")
	public String getLabel() {
		return type.getLabel();
	}

	public void setLabel(String type) {
		this.type = Type.fromLabel(type);
	}

	@XmlAttribute
   public String getClose() {
      return closeURL;
   }

   public void setClose(String closeURL) {
      this.closeURL = closeURL;
   }

   @XmlAttribute
   public String getResource() {
      return resourceURL;
   }

   public void setResource(String resource) {
      this.resourceURL = resource;
   }

   @XmlTransient
   public ControllerCommand.Type getType() {
      return type;
   }

   public void setType(ControllerCommand.Type type) {
      this.type = type;
   }
}
