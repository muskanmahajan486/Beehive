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

import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.domain.modeler.Slider;

/**
 * Business service for <code>SliderDTO</code>.
 */
public interface SliderService {

   /**
    * Save a sliderDTO into the database, which is under an account.
    * 
    * @param sliderDTO
    * @param accountId
    * @return the saved switchDTO with specified id.
    */
   public SliderDTO save(SliderDTO sliderDTO, long accountId);
   
   /**
    * Update slider properties into database.
    * 
    * @param sliderDTO
    * @return the updated slider.
    */
   public Slider update(SliderDTO sliderDTO);
   
   /**
    * Delete a slider by sliderId.
    * 
    * @param sliderId
    */
   public void deleteById(long sliderId);
   
   /**
    * Load all sliderDTOs under an account.
    * 
    * @param accountId
    * @return a list of sliderDTOs.
    */
   public List<SliderDTO> loadAccountSliders(long accountId);
   
   /**
    * Load a list of sliderDTOs, each of them has same properties with the specified sliderDTO except id.
    * 
    * @param sliderDTO the specified sliderDTO.
    * @return a list of sliderDTO.
    */
   public List<SliderDTO> loadSameSliders(SliderDTO sliderDTO);
   
}
