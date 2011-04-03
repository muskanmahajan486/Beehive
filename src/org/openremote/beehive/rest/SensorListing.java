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
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.modeler.SensorDTO;

/**
 * In order to let rest service to serialize list of sensors.
 */
@XmlRootElement(name = "sensors")
public class SensorListing {

   private List<SensorDTO> sensors = new ArrayList<SensorDTO>();

   public SensorListing() {
   }
   
   public SensorListing(List<SensorDTO> sensors) {
      this.sensors = sensors;
   }

   @XmlElementRef(type=SensorDTO.class)
   public List<SensorDTO> getSensors() {
      return sensors;
   }
   public void setSensors(List<SensorDTO> sensors) {
      this.sensors = sensors;
   }
   
}
