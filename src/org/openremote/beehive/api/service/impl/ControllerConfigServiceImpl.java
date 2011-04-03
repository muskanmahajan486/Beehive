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

import org.openremote.beehive.api.dto.modeler.ControllerConfigDTO;
import org.openremote.beehive.api.service.ControllerConfigService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.ControllerConfig;

public class ControllerConfigServiceImpl extends BaseAbstractService<ControllerConfig> implements ControllerConfigService {
   
   public void saveDefaultConfigurationsToAccount(List<ControllerConfigDTO> contollerConfigDTOs, long accountId) {
      Account account = genericDAO.getById(Account.class, accountId);
      List<ControllerConfig> controllerConfigs = new ArrayList<ControllerConfig>();
      for(ControllerConfigDTO cfgDTO : contollerConfigDTOs){
         ControllerConfig cfg = cfgDTO.toControllerConfig();
         cfg.setAccount(account);
         controllerConfigs.add(cfg);
      }
      genericDAO.saveOrUpdateAll(controllerConfigs);
   }

   public List<ControllerConfigDTO> loadAccountConfigsByCategoryName(long accountId, String categoryName) {
      String hql = "select cfg from ControllerConfig cfg where cfg.category like ? and cfg.account.oid=?";
      Object[] args = new Object[]{categoryName,accountId};
      List<ControllerConfig> controllerConfigs = genericDAO.find(hql, args);
      List<ControllerConfigDTO> cfgDTOs = new ArrayList<ControllerConfigDTO>();
      for (ControllerConfig controllerConfig : controllerConfigs) {
         cfgDTOs.add(controllerConfig.toDTO());
      }
      return cfgDTOs;
   }

   public List<ControllerConfigDTO> saveOrUpdateConfigurationsToAccount(List<ControllerConfigDTO> contollerConfigDTOs, long accountId) {
      Account account = genericDAO.getById(Account.class, accountId);
      List<ControllerConfig> controllerConfigs = new ArrayList<ControllerConfig>();
      for(ControllerConfigDTO cfgDTO : contollerConfigDTOs){
         ControllerConfig cfg = cfgDTO.toControllerConfig();
         cfg.setAccount(account);
         controllerConfigs.add(cfg);
      }
      genericDAO.saveOrUpdateAll(controllerConfigs);
      List<ControllerConfigDTO> cfgDTOs = new ArrayList<ControllerConfigDTO>();
      for (ControllerConfig controllerConfig : controllerConfigs) {
         cfgDTOs.add(controllerConfig.toDTO());
      }
      return cfgDTOs;
   }

   public List<ControllerConfigDTO> loadAccountConfigs(long accountId) {
      String hql = "select cfg from ControllerConfig cfg where cfg.account.oid=?";
      Object[] args = new Object[]{accountId};
      List<ControllerConfig> controllerConfigs = genericDAO.find(hql, args);
      List<ControllerConfigDTO> cfgDTOs = new ArrayList<ControllerConfigDTO>();
      for (ControllerConfig controllerConfig : controllerConfigs) {
         cfgDTOs.add(controllerConfig.toDTO());
      }
      return cfgDTOs;
   }

}
