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

import javax.xml.bind.annotation.XmlTransient;

import org.openremote.beehive.domain.ControllerCommand;

/**
 * DTO for the update-controller command 
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@SuppressWarnings("serial")
public class UpdateControllerCommandDTO extends ControllerCommandDTO {
	
   /**
    * We store the resource name in this transient field so that the REST
    * service can use it for building the resource URL later on.
    */
	private String resourceName;

	public UpdateControllerCommandDTO() {
		super(ControllerCommand.Type.UPDATE_CONTROLLER);
	}

	public UpdateControllerCommandDTO(String resourceName) {
		this();
		this.resourceName = resourceName;
	}

	@XmlTransient
   public String getResourceName() {
      return resourceName;
   }

   public void setResourceName(String resourceName) {
      this.resourceName = resourceName;
   }
}
