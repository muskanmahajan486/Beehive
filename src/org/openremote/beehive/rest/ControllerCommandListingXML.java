/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.openremote.beehive.api.dto.ControllerCommandDTO;
import org.openremote.beehive.api.dto.UpdateControllerCommandDTO;
import org.openremote.beehive.api.dto.UploadLogsControllerCommandDTO;

/**
 * This is the XML version of the list of commands. It differs from the JSON version
 * because the JSON mapping doesn't support ordered lists of mixed elements.
 * See http://www.xml.com/lpt/a/1658 for examples.
 * 
 * This serialiser produces this sort of output:
 * 
 * <code>
 *  &lt;commands>
 *   &lt;upload-logs/>
 *   &lt;update-controller/>
 *   &lt;upload-logs/>
 *  &lt;/commands>
 * </code>
 * 
 * @author Stef Epardaud
 */
@XmlRootElement(name = "commands")
@XmlSeeAlso({UpdateControllerCommandDTO.class, UploadLogsControllerCommandDTO.class})
@Mapped(namespaceMap = {
      @XmlNsMap(namespace = "http://www.w3.org/2001/XMLSchema-instance", jsonName = "xsd")
})
public class ControllerCommandListingXML {

	private List<ControllerCommandDTO> commands = new ArrayList<ControllerCommandDTO>();

	public ControllerCommandListingXML() {
	}

	public ControllerCommandListingXML(List<ControllerCommandDTO> commands) {
		this.commands = commands;
	}
	
	@XmlElements({
		@XmlElement(type = UpdateControllerCommandDTO.class, name = "update-controller"),
		@XmlElement(type = UploadLogsControllerCommandDTO.class, name = "upload-logs"),
		@XmlElement(type = ControllerCommandDTO.class, name = "command")
	})
	public List<ControllerCommandDTO> getCommands() {
		return commands;
	}

	public void setCommands(List<ControllerCommandDTO> commands) {
		this.commands = commands;
	}

}
