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

import org.openremote.beehive.api.dto.modeler.SwitchDTO;
import org.openremote.beehive.domain.modeler.Switch;

/**
 * Business service for <code>SwitchDTO</code>.
 */
public interface SwitchService {

   /**
    * Save a switchDTO into database.
    * 
    * @param switchDTO
    * @param accountId
    * @return the saved switchDTO with specified id.
    */
   public SwitchDTO save(SwitchDTO switchDTO, long accountId);
   
   /**
    * Update switch properties into database.
    * 
    * @param switchDTO
    * @return the updated switch
    */
   public Switch updateSwitch(SwitchDTO switchDTO);
   
   /**
    * Delete a switch by switchId.
    * 
    * @param id the switchId.
    */
   public void deleteSwitchById(long id);
   
   /**
    * Load a list of switchDTOs under an account.
    * 
    * @param accountId
    * @return a list of switchDTOs.
    */
   public List<SwitchDTO> loadAccountSwitchs(long accountId);
   
   /**
    * Load a list of switchDTOs, each of them has same properties with the specified switchDTO except id.
    * 
    * @param switchDTO the specified switchDTO.
    * @return a list of switchDTO.
    */
   public List<SwitchDTO> loadSameSwitchs(SwitchDTO switchDTO);
   
}
