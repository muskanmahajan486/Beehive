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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.openremote.beehive.domain.modeler.CustomSensor;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorType;

/**
 * The Class is used for transmitting custom sensor info.
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "CustomSensor")
@XmlRootElement(name="customSensor")
public class CustomSensorDTO extends SensorDTO {

   private List<StateDTO> states = new ArrayList<StateDTO>();

   public CustomSensorDTO() {
      super(SensorType.CUSTOM);
   }
   
   @XmlElementWrapper(name="states")
   @XmlElement(name = "state")
   public List<StateDTO> getStates() {
      return states;
   }

   public void setStates(List<StateDTO> states) {
      this.states = states;
   }
   
   public void addState(StateDTO state) {
      this.states.add(state);
   }
   
   @Override
   public Sensor toSensor() {
      CustomSensor sensor = new CustomSensor();
      sensor.setOid(getId());
      sensor.setName(getName());
      if (getSensorCommandRef() != null) {
         sensor.setSensorCommandRef(getSensorCommandRef().toSensorCommandRef(sensor));
      }
      if (getDevice() != null) {
         sensor.setDevice(getDevice().toDevice());
      }
      for (StateDTO stateDTO : states) {
         sensor.addState(stateDTO.toState(sensor));
      }
      return sensor;
   }

   @Override
   public Sensor toSimpleSensor() {
      CustomSensor sensor = new CustomSensor();
      sensor.setOid(getId());
      sensor.setName(getName());
      for (StateDTO stateDTO : states) {
         sensor.addState(stateDTO.toState(sensor));
      }
      return sensor;
   }
   
   
}
