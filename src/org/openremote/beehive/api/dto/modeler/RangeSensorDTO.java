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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.openremote.beehive.domain.modeler.RangeSensor;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorType;

/**
 * The Class is used for transmitting range sensor info.
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "RangeSensor")
@XmlRootElement(name="rangeSensor")
public class RangeSensorDTO extends SensorDTO {

   private int min;
   private int max;
   
   public RangeSensorDTO() {
      super(SensorType.RANGE);
   }
   
   public int getMin() {
      return min;
   }
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
   public Sensor toSensor() {
      RangeSensor sensor = new RangeSensor();
      sensor.setOid(getId());
      sensor.setName(getName());
      if (getSensorCommandRef() != null) {
         sensor.setSensorCommandRef(getSensorCommandRef().toSensorCommandRef(sensor));
      }
      if (getDevice() != null) {
         sensor.setDevice(getDevice().toDevice());
      }
      sensor.setMax(max);
      sensor.setMin(min);
      return sensor;
   }

   @Override
   public Sensor toSimpleSensor() {
      RangeSensor sensor = new RangeSensor();
      sensor.setOid(getId());
      sensor.setName(getName());
      sensor.setMax(max);
      sensor.setMin(min);
      return sensor;
   }
   
   
}
