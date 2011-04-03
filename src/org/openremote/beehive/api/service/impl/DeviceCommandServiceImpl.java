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
import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.api.service.DeviceCommandService;
import org.openremote.beehive.api.service.DeviceMacroItemService;
import org.openremote.beehive.domain.modeler.CommandRefItem;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.domain.modeler.DeviceCommand;
import org.openremote.beehive.domain.modeler.Protocol;

public class DeviceCommandServiceImpl extends BaseAbstractService<DeviceCommand> implements DeviceCommandService {

   private DeviceMacroItemService deviceMacroItemService;
   
   public DeviceCommandDTO loadDeviceCommandById(long id) {
      DeviceCommand deviceCommand = super.loadById(id);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand.toDTO();
   }

   public DeviceCommandDTO save(DeviceCommandDTO deviceCommandDTO) {
      
      DeviceCommand deviceCommand = deviceCommandDTO.toDeviceCommand();
      genericDAO.save(deviceCommand);
      Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      return deviceCommand.toDTO();
   }

   public Boolean deleteCommandById(long id) {
      DeviceCommand deviceCommand = loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(CommandRefItem.class);
      List<CommandRefItem> commandRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("deviceCommand", deviceCommand)));
      if (commandRefItems.size() > 0) {
         return false;
      } else {
         deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
         genericDAO.delete(deviceCommand);
      }
      return true;
   }

   public DeviceCommand update(DeviceCommandDTO deviceCommandDTO) {
      DeviceCommand old = loadById(deviceCommandDTO.getId());
      genericDAO.delete(old.getProtocol());
      old.setName(deviceCommandDTO.getName());
      Protocol newProtocol = deviceCommandDTO.getProtocol().toProtocol();
      newProtocol.setDeviceCommand(old);
      old.setProtocol(newProtocol);
      
      return old;
   }

   public List<DeviceCommand> saveAll(List<DeviceCommandDTO> deviceCommandDTOs) {
      List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
      for (DeviceCommandDTO deviceCommandDTO : deviceCommandDTOs) {
         DeviceCommand deviceCommand = deviceCommandDTO.toDeviceCommand();
         genericDAO.save(deviceCommand);
         Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
         deviceCommands.add(deviceCommand);
      }
      return deviceCommands;
   }

   public List<DeviceCommand> loadCommandsByDeviceId(long id) {
      Device device = genericDAO.loadById(Device.class, id);
      List<DeviceCommand> deviceCommandList = device.getDeviceCommands();
      for (DeviceCommand deviceCommand : deviceCommandList) {
         Hibernate.initialize(deviceCommand.getProtocol().getAttributes());
      }
      return deviceCommandList;
   }

   public List<DeviceCommand> loadSameDeviceCommands(DeviceCommandDTO deviceCommandDTO) {
      DeviceCommand deviceCommand = deviceCommandDTO.toDeviceCommand();
      List<DeviceCommand> tmpResult = new ArrayList<DeviceCommand>();
      DetachedCriteria critera = DetachedCriteria.forClass(DeviceCommand.class);
      critera.add(Restrictions.eq("device.oid", deviceCommand.getDevice().getOid()));
      critera.add(Restrictions.eq("name", deviceCommand.getName()));
      if (deviceCommand.getSectionId() != null) {
         critera.add(Restrictions.eq("sectionId", deviceCommand.getSectionId()));
      }
      tmpResult = genericDAO.findByDetachedCriteria(critera);
      if (tmpResult != null) {
         for(Iterator<DeviceCommand> iterator= tmpResult.iterator();iterator.hasNext();) {
            DeviceCommand cmd = iterator.next();
            if (! cmd.equalsWithoutCompareOid(deviceCommand)) {
               iterator.remove();
            }
         }
      }
      return tmpResult;
   }
   
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }


}
