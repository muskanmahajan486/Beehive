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
import org.openremote.beehive.api.dto.modeler.DeviceMacroDTO;
import org.openremote.beehive.api.dto.modeler.DeviceMacroItemDTO;
import org.openremote.beehive.api.service.DeviceMacroItemService;
import org.openremote.beehive.api.service.DeviceMacroService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.DeviceCommand;
import org.openremote.beehive.domain.modeler.DeviceCommandRef;
import org.openremote.beehive.domain.modeler.DeviceMacro;
import org.openremote.beehive.domain.modeler.DeviceMacroItem;

/**
 *
 * @author tomsky
 */
public class DeviceMacroServiceImpl extends BaseAbstractService<DeviceMacro> implements DeviceMacroService {

   private DeviceMacroItemService deviceMacroItemService;
   
   public DeviceMacroDTO save(DeviceMacroDTO deviceMacroDTO, long accountId) {
      Account account = genericDAO.loadById(Account.class, accountId);
      DeviceMacro deviceMacro = deviceMacroDTO.toDeviceMacroWithContent(account);
      genericDAO.save(deviceMacro);
      List<DeviceMacroItem> deviceMacroItems = deviceMacro.getDeviceMacroItems();
      for (DeviceMacroItem deviceMacroItem : deviceMacroItems) {
         if (deviceMacroItem instanceof DeviceCommandRef) {
            DeviceCommand deviceCommand = ((DeviceCommandRef)deviceMacroItem).getDeviceCommand();
            if (deviceCommand != null) {
               deviceCommand = genericDAO.loadById(DeviceCommand.class, deviceCommand.getOid());
               ((DeviceCommandRef)deviceMacroItem).setDeviceCommand(deviceCommand);
            }
         }
      }
      return deviceMacro.toDTO();
   }

   public List<DeviceMacroItemDTO> loadDeviceMacroItems(long macroId) {
      DeviceMacro deviceMacro = genericDAO.loadById(DeviceMacro.class, macroId);
      Hibernate.initialize(deviceMacro.getDeviceMacroItems());
      DeviceMacroDTO deviceMacroDTO = deviceMacro.toDTO();
      return deviceMacroDTO.getDeviceMacroItems();
   }

   public List<DeviceMacroDTO> loadAccountDeviceMacros(long accountId) {
      Account account = genericDAO.loadById(Account.class, accountId);
      List<DeviceMacro> deviceMacros = account.getDeviceMacros();
      List<DeviceMacroDTO> deviceMacroDTOs = new ArrayList<DeviceMacroDTO>();
      if (deviceMacros == null || deviceMacros.size() == 0) {
         return deviceMacroDTOs;
      }
      
      for (DeviceMacro deviceMacro : deviceMacros) {
         Hibernate.initialize(deviceMacro.getDeviceMacroItems());
         deviceMacroDTOs.add(deviceMacro.toDTO());
      }
      
      return deviceMacroDTOs;
   }

   public void deleteDeviceMacro(long macroId) {
      DeviceMacro deviceMacro = loadById(macroId);
      deviceMacroItemService.deleteByDeviceMacro(deviceMacro);
      genericDAO.delete(deviceMacro);
   }

   public DeviceMacroDTO updateDeviceMacro(DeviceMacroDTO deviceMacroDTO) {
      DeviceMacro old = genericDAO.loadById(DeviceMacro.class, deviceMacroDTO.getId());
      genericDAO.deleteAll(old.getDeviceMacroItems());
      old.getDeviceMacroItems().clear();

      old.setName(deviceMacroDTO.getName());
      List<DeviceMacroItemDTO> deviceMacroItemDTOs = deviceMacroDTO.getDeviceMacroItems();
      for (DeviceMacroItemDTO deviceMacroItemDTO : deviceMacroItemDTOs) {
         old.getDeviceMacroItems().add(deviceMacroItemDTO.toDeviceMacroItem(old));
      }
      
      List<DeviceMacroItem> deviceMacroItems = old.getDeviceMacroItems();
      for (DeviceMacroItem deviceMacroItem : deviceMacroItems) {
         if (deviceMacroItem instanceof DeviceCommandRef) {
            DeviceCommand deviceCommand = ((DeviceCommandRef)deviceMacroItem).getDeviceCommand();
            if (deviceCommand != null) {
               deviceCommand = genericDAO.loadById(DeviceCommand.class, deviceCommand.getOid());
               ((DeviceCommandRef)deviceMacroItem).setDeviceCommand(deviceCommand);
            }
         }
      }
      return old.toDTO();
   }
   
   public void setDeviceMacroItemService(DeviceMacroItemService deviceMacroItemService) {
      this.deviceMacroItemService = deviceMacroItemService;
   }

   public List<DeviceMacroDTO> loadSameDeviceMacros(DeviceMacroDTO deviceMacroDTO, long accountId) {
      DetachedCriteria critera = DetachedCriteria.forClass(DeviceMacro.class);
      critera.add(Restrictions.eq("account.oid", accountId));
      critera.add(Restrictions.eq("name", deviceMacroDTO.getName()));
      DeviceMacro macro = deviceMacroDTO.toDeviceMacroWithContent(null);
      List<DeviceMacro> results = genericDAO.findByDetachedCriteria(critera);
      if (results != null && results.size() >0) {
         for (Iterator<DeviceMacro> iterator = results.iterator();iterator.hasNext(); ) {
            DeviceMacro m = iterator.next();
            if (! m.equalsWitoutCompareOid(macro)) {
               iterator.remove();
            }
         }
      }
      List<DeviceMacroDTO> deviceMacroDTOs = new ArrayList<DeviceMacroDTO>();
      for (DeviceMacro deviceMacro : results) {
         deviceMacroDTOs.add(deviceMacro.toSimpleDTO());
      }
      
      return deviceMacroDTOs;
   }

}
