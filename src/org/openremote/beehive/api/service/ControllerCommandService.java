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
package org.openremote.beehive.api.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openremote.beehive.api.dto.ControllerCommandDTO;
import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.InitiateProxyControllerCommand;
import org.openremote.beehive.domain.User;

/**
 * Account service.
 * 
 * @author Stef Epardaud
 *
 */
public interface ControllerCommandService {
   
   void save(ControllerCommand controllerCommand);

   void update(ControllerCommand controllerCommand);

   List<ControllerCommandDTO> queryByUsername(String username);

   void closeControllerCommand(ControllerCommand controllerCommand);

   ControllerCommand findControllerCommandById(Long id);

   ControllerCommand saveUploadLogsCommand(String username);

   void saveUpdateControllerCommand(String username, File resource) throws IOException;

   void saveLogs(ControllerCommand controllerCommand, User user, File resource) throws IOException;

   InitiateProxyControllerCommand saveProxyControllerCommand(User user, String url);

}
