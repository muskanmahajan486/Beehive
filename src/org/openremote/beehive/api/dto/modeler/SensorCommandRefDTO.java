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

import org.apache.commons.lang.StringUtils;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorCommandRef;

@SuppressWarnings("serial")
public class SensorCommandRefDTO extends CommandRefItemDTO {

   private SensorDTO sensor;

   public SensorCommandRefDTO() {
   }
   
   public SensorCommandRefDTO(SensorCommandRef ref, String deviceName) {
      setId(ref.getOid());
      setDeviceCommand(ref.getDeviceCommand().toDTO());
      if (!StringUtils.isEmpty(deviceName)) {
         setDeviceName(deviceName);
      }
   }
   
   public SensorDTO getSensor() {
      return sensor;
   }

   public void setSensor(SensorDTO sensor) {
      this.sensor = sensor;
   }
   
   public SensorCommandRef toSensorCommandRef(Sensor sensor) {
      SensorCommandRef sensorCommandRef = new SensorCommandRef();
      sensorCommandRef.setDeviceCommand(getDeviceCommand().toDeviceCommand());
      sensorCommandRef.setSensor(sensor);
      return sensorCommandRef;
   }
}
