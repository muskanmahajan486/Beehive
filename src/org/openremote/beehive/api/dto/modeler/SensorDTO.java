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
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorType;

/**
 * The Class is used for transmitting sensor info.
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="classType")
@JsonTypeName(value = "Sensor")
@JsonSubTypes(value={@Type(value=RangeSensorDTO.class), @Type(value=CustomSensorDTO.class)})
@XmlRootElement(name="sensor")
@XmlSeeAlso(value = { RangeSensorDTO.class, CustomSensorDTO.class})
public class SensorDTO extends BusinessEntityDTO {

   private String name;
   private SensorType type;
   private SensorCommandRefDTO sensorCommandRef;
   private DeviceDTO device;
   
   public SensorDTO() {
   }
   
   public SensorDTO(SensorType type) {
      this.type = type;
   }
   public String getName() {
      return name;
   }
   public SensorType getType() {
      return type;
   }
   public void setName(String name) {
      this.name = name;
   }
   public void setType(SensorType type) {
      this.type = type;
   }
   public SensorCommandRefDTO getSensorCommandRef() {
      return sensorCommandRef;
   }
   public void setSensorCommandRef(SensorCommandRefDTO sensorCommandRef) {
      this.sensorCommandRef = sensorCommandRef;
   }
   public DeviceDTO getDevice() {
      return device;
   }
   public void setDevice(DeviceDTO device) {
      this.device = device;
   }
   
   public Sensor toSensor() {
      Sensor sensor = new Sensor(type);
      sensor.setOid(getId());
      sensor.setName(name);
      if (sensorCommandRef != null) {
         sensor.setSensorCommandRef(sensorCommandRef.toSensorCommandRef(sensor));
      }
      if (device != null) {
         sensor.setDevice(device.toDevice());
      }
      return sensor;
   }
   
   public Sensor toSimpleSensor() {
      Sensor sensor = new Sensor(type);
      sensor.setOid(getId());
      sensor.setName(name);
      return sensor;
   }
}
