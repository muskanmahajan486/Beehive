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
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.api.dto.modeler.DeviceAttrDTO;
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.api.service.DeviceMacroItemService;
import org.openremote.beehive.api.service.DeviceService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.domain.modeler.DeviceAttr;
import org.openremote.beehive.domain.modeler.DeviceCommand;

public class DeviceServiceImpl extends BaseAbstractService<Device> implements DeviceService {

   private DeviceMacroItemService deviceMacroItemService;
   
   public Device saveDevice(Device device) {
      genericDAO.save(device);
      return device;
   }

   public Device saveDeviceWithContent(Device device) {
      genericDAO.save(device);
      Hibernate.initialize(device.getSensors());
      Hibernate.initialize(device.getSwitchs());
      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
      for(DeviceCommand cmd : deviceCommands ) {
         Hibernate.initialize(cmd.getProtocol().getAttributes());
      }
      Hibernate.initialize(device.getSliders());
      Hibernate.initialize(device.getDeviceAttrs());
      return device;
   }
   
   public List<DeviceDTO> loadAllAccountDevices(long accountId) {
      Account account = genericDAO.getById(Account.class, accountId);
      List<Device> devices = account.getDevices();
      List<DeviceDTO> deviceDTOs = new ArrayList<DeviceDTO>();
      for (Device device : devices) {
         deviceDTOs.add(device.toSimpleDTO());
      }
      
      return deviceDTOs;
   }

   public DeviceDTO loadDeviceById(long id) {
      Device device = super.loadById(id);
      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
      for(DeviceCommand cmd : deviceCommands ) {
         Hibernate.initialize(cmd.getProtocol().getAttributes());
      }
      return device.toDTO();
   }

   public void update(DeviceDTO deviceDTO) {
      Device device = genericDAO.getById(Device.class, deviceDTO.getId());
      if (device != null) {
         device.setName(deviceDTO.getName());
         device.setVendor(deviceDTO.getVendor());
         device.setModel(deviceDTO.getModel());
         List<DeviceAttr> oldDeviceAttrs = device.getDeviceAttrs();
         if (oldDeviceAttrs != null && oldDeviceAttrs.size() > 0) {
        	 genericDAO.delete(oldDeviceAttrs);
         }
         
         List<DeviceAttrDTO> deviceAttrDTOs = deviceDTO.getDeviceAttrs();
         if (deviceAttrDTOs != null && deviceAttrDTOs.size() > 0) {
        	 List<DeviceAttr> deviceAttrs = new ArrayList<DeviceAttr>();
        	 for (DeviceAttrDTO deviceAttrDTO : deviceAttrDTOs) {
        		 deviceAttrs.add(deviceAttrDTO.toDeviceAttr());
			 }
        	 device.setDeviceAttrs(deviceAttrs);
         }
         genericDAO.update(device);
      }
      
   }

   public void delete(long deviceId) {
      Device device = loadById(deviceId);
      for (DeviceCommand deviceCommand : device.getDeviceCommands()) {
         deviceMacroItemService.deleteByDeviceCommand(deviceCommand);
      }
      genericDAO.delete(device);
   }

   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }

   public List<DeviceDTO> loadSameDevices(DeviceDTO deviceDTO, long accountId) {
      DetachedCriteria critera = DetachedCriteria.forClass(Device.class);
      critera.add(Restrictions.eq("name", deviceDTO.getName()));
      critera.add(Restrictions.eq("model", deviceDTO.getModel()));
      critera.add(Restrictions.eq("vendor", deviceDTO.getVendor()));
      critera.add(Restrictions.eq("account.oid", accountId));
      List<Device> devices = genericDAO.findPagedDateByDetachedCriteria(critera, 1, 0);
      
      List<DeviceDTO> deviceDTOs = new ArrayList<DeviceDTO>();
      for (Device device : devices) {
         deviceDTOs.add(device.toSimpleDTO());
      }
      
      return deviceDTOs;
   }

}
