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
package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.modeler.ControllerConfigDTO;

public interface ControllerConfigService {

   void saveDefaultConfigurationsToAccount(List<ControllerConfigDTO> contollerConfigs, long accountId);
   
   List<ControllerConfigDTO> loadAccountConfigsByCategoryName(long accountId, String categoryName);
   
   List<ControllerConfigDTO> saveOrUpdateConfigurationsToAccount(List<ControllerConfigDTO> contollerConfigs, long accountId);
   
   List<ControllerConfigDTO> loadAccountConfigs(long accountId);
}
