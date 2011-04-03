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
package org.openremote.beehive.domain.modeler;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.openremote.beehive.api.dto.modeler.RangeSensorDTO;
import org.openremote.beehive.api.dto.modeler.SensorCommandRefDTO;
import org.openremote.beehive.api.dto.modeler.SensorDTO;

/**
 * The RangeSensor define a integer range that the sensor should to be.
 */
@Entity
@DiscriminatorValue("RANGE_SENSOR")
@SecondaryTable(name = "range_sensor", pkJoinColumns = @PrimaryKeyJoinColumn(name = "oid"))
public class RangeSensor extends Sensor {

   private static final long serialVersionUID = 8187543869104066750L;
   
   private int min;
   private int max;

   public RangeSensor() {
      super(SensorType.RANGE);
   }
   
   @Column(table = "range_sensor", name = "min_value")
   public int getMin() {
      return min;
   }
   
   @Column(table = "range_sensor", name = "max_value")
   public int getMax() {
      return max;
   }

   public void setMin(int min) {
      this.min = min;
   }

   public void setMax(int max) {
      this.max = max;
   }

   @Override
   public SensorDTO toDTO() {
      RangeSensorDTO rangeSensorDTO = new RangeSensorDTO();
      rangeSensorDTO.setId(getOid());
      rangeSensorDTO.setName(getName());
      rangeSensorDTO.setType(getType());
      SensorCommandRef sensorCommandRef = getSensorCommandRef();
      Device device = getDevice();
      String deviceName = null;
      if (device != null) {
         rangeSensorDTO.setDevice(device.toSimpleDTO());
         deviceName = device.getName();
      }
      if (sensorCommandRef != null) {
         rangeSensorDTO.setSensorCommandRef(new SensorCommandRefDTO(sensorCommandRef, deviceName));
      }
      rangeSensorDTO.setMax(max);
      rangeSensorDTO.setMin(min);
      return rangeSensorDTO;
   }
   
   
}
