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

import org.openremote.beehive.api.dto.modeler.DeviceMacroDTO;
import org.openremote.beehive.api.dto.modeler.DeviceMacroItemDTO;

/**
 * The Interface DeviceMacroService.
 */
/**
 * @author tomsky
 *
 */
public interface DeviceMacroService {

   /**
    * Save a deviceMacroDTO into database.
    * 
    * @param deviceMacroDTO
    * @param accountId
    * @return the saved deviceMacroDTO with specified id.
    */
   public DeviceMacroDTO save(DeviceMacroDTO deviceMacroDTO, long accountId);
   
   /**
    * Load all deviceMacroItems under a deviceMacro.
    * The deviceMacroItem include <code>DeviceCommandRef</code>,<code>DeviceMacroRef</code> and <code>CommandDelay</code>.
    *  
    * @param macroId
    * @return a list of deviceMacroItems.
    */
   public List<DeviceMacroItemDTO> loadDeviceMacroItems(long macroId);
   
   /**
    * Load all deviceMacros under an account.
    * 
    * @param accountId
    * @return a list of deviceMacros.
    */
   public List<DeviceMacroDTO> loadAccountDeviceMacros(long accountId);
   
   /**
    * Delete a deviceMacro by its id.
    * 
    * @param macroId
    */
   public void deleteDeviceMacro(long macroId);
   
   /**
    * Update the deviceMacro properties to database, 
    * it will delete all old deviceMacroItems from database and add news.
    * 
    * @param deviceMacroDTO
    * @return the updated deviceMacroDTO.
    */
   public DeviceMacroDTO updateDeviceMacro(DeviceMacroDTO deviceMacroDTO);
   
   /**
    * Load same DeviceMacros from database, each of them has the same properties with <code>DeviceMacroDTO</code> except id. 
    * 
    * @param deviceMacroDTO
    * @param accountId
    * @return a list of deviceMacroDTOs.
    */
   public List<DeviceMacroDTO> loadSameDeviceMacros(DeviceMacroDTO deviceMacroDTO, long accountId);
   
}
