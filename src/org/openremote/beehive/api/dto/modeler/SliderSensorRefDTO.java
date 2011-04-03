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

import org.openremote.beehive.domain.modeler.Slider;
import org.openremote.beehive.domain.modeler.SliderSensorRef;

@SuppressWarnings("serial")
public class SliderSensorRefDTO extends SensorRefItemDTO {

   private SliderDTO slider;

   public SliderSensorRefDTO() {
   }
   public SliderSensorRefDTO(SliderSensorRef sliderSensorRef) {
      super(sliderSensorRef.getSensor());
   }
   
   public SliderDTO getSlider() {
      return slider;
   }

   public void setSlider(SliderDTO slider) {
      this.slider = slider;
   }
   
   public SliderSensorRef toSliderSensorRef(Slider slider) {
      SliderSensorRef sliderSensorRef = new SliderSensorRef(slider);
      sliderSensorRef.setSensor(getSensor().toSensor());
      return sliderSensorRef;
   }
}
