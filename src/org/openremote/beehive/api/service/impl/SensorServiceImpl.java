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
package org.openremote.beehive.api.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.api.dto.modeler.CustomSensorDTO;
import org.openremote.beehive.api.dto.modeler.RangeSensorDTO;
import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.api.dto.modeler.StateDTO;
import org.openremote.beehive.api.service.SensorService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.CustomSensor;
import org.openremote.beehive.domain.modeler.DeviceCommand;
import org.openremote.beehive.domain.modeler.RangeSensor;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorRefItem;
import org.openremote.beehive.domain.modeler.SensorType;

public class SensorServiceImpl extends BaseAbstractService<Sensor> implements SensorService {

   public SensorDTO save(SensorDTO sensorDTO, long accountId) {
      Sensor sensor = sensorDTO.toSensor();
      Account account = genericDAO.loadById(Account.class, accountId);
      sensor.setAccount(account);
      genericDAO.save(sensor);
      DeviceCommand deviceCommand = sensor.getSensorCommandRef().getDeviceCommand();
      if (deviceCommand != null) {
         Hibernate.initialize(deviceCommand.getDevice());
      }
      return sensor.toDTO();
   }

   public boolean deleteSensorById(long id) {
      Sensor sensor = super.loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(SensorRefItem.class);
      List<SensorRefItem> sensorRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("sensor",
            sensor)));
      if (sensorRefItems.size() > 0) {
         return false;
      } else {
         genericDAO.delete(sensor);
      }
      return true;
   }

   public Sensor updateSensor(SensorDTO sensorDTO) {
      Sensor old = null;
      
      if (SensorType.RANGE == sensorDTO.getType()) {
         old = genericDAO.loadById(RangeSensor.class, sensorDTO.getId());
         RangeSensorDTO rangeSensorDTO = (RangeSensorDTO)sensorDTO;
         ((RangeSensor) old).setMax(rangeSensorDTO.getMax());
         ((RangeSensor) old).setMin(rangeSensorDTO.getMin());
      } else if (SensorType.CUSTOM == sensorDTO.getType()) {
         DetachedCriteria criteria = DetachedCriteria.forClass(CustomSensor.class);
         criteria.add(Restrictions.eq("oid", sensorDTO.getId()));
         old = genericDAO.findOneByDetachedCriteria(criteria);
         old = genericDAO.loadById(CustomSensor.class, sensorDTO.getId());
         genericDAO.deleteAll(((CustomSensor)old).getStates());
         List<StateDTO> stateDTOs = ((CustomSensorDTO) sensorDTO).getStates();
         CustomSensor oldCustomSensor = (CustomSensor)old;
         oldCustomSensor.getStates().clear();
         for (StateDTO stateDTO : stateDTOs) {
            oldCustomSensor.addState(stateDTO.toState(oldCustomSensor));
         }
         
      } else {
         old = genericDAO.loadById(Sensor.class, sensorDTO.getId());
      }
      
      old.setName(sensorDTO.getName());
      if (sensorDTO.getSensorCommandRef() != null) {
         genericDAO.delete(old.getSensorCommandRef());
         old.setSensorCommandRef(sensorDTO.getSensorCommandRef().toSensorCommandRef(old));
      }
      if (old.getSensorCommandRef() != null && old.getSensorCommandRef().getDeviceCommand().getProtocol() != null) {
         Hibernate.initialize(old.getSensorCommandRef().getDeviceCommand().getProtocol().getAttributes());
      }
      return old;
   }

   public List<SensorDTO> loadAllAccountSensors(long accountId) {
      Account account = genericDAO.loadById(Account.class, accountId);
      List<Sensor> sensors = account.getSensors();
      for (Sensor sensor : sensors) {
         if (sensor.getType() == SensorType.CUSTOM) {
            Hibernate.initialize(((CustomSensor) sensor).getStates());
         }
      }
      List<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
      for (Sensor sensor : sensors) {
         sensorDTOs.add(sensor.toDTO());
      }
      return sensorDTOs;
   }

   public SensorDTO loadSensorById(long id) {
      Sensor sensor = genericDAO.getById(Sensor.class, id);
      if (sensor instanceof CustomSensor) {
         Hibernate.initialize(((CustomSensor) sensor).getStates());
      }
      return sensor.toDTO();
   }

   public List<SensorDTO> loadSameSensors(SensorDTO sensorDTO) {
      DetachedCriteria critera = DetachedCriteria.forClass(Sensor.class);
      critera.add(Restrictions.eq("name", sensorDTO.getName()));
      critera.add(Restrictions.eq("type", sensorDTO.getType()));
      critera.add(Restrictions.eq("device.oid", sensorDTO.getDevice().getId()));
      List<Sensor> result = genericDAO.findByDetachedCriteria(critera);
      Sensor sensor = sensorDTO.toSensor();
      
      List<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
      if (result != null) {
         for(Iterator<Sensor> iterator=result.iterator();iterator.hasNext();) {
            Sensor s = iterator.next();
            if(!s.equalsWithoutCompareOid(sensor)){
               iterator.remove();
            }
         }
         
         for (Sensor dbSensor : result) {
            sensorDTOs.add(dbSensor.toDTO());
         }
      }
      return sensorDTOs;
   }

}
