/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.ControllerCommandDTO;
import org.openremote.beehive.api.dto.InitiateProxyControllerCommandDTO;
import org.openremote.beehive.api.dto.UpdateControllerCommandDTO;
import org.openremote.beehive.api.dto.UploadLogsControllerCommandDTO;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ControllerCommandService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.api.service.SensorValueService;
import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.ControllerCommand.State;
import org.openremote.beehive.domain.ControllerCommand.Type;
import org.openremote.beehive.domain.InitiateProxyControllerCommand;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.utils.ZipUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * ControllerCommand service implementation.
 * 
 * @author Stef Epardaud
 */
public class ControllerCommandServiceImpl extends BaseAbstractService<ControllerCommand> implements ControllerCommandService {

   private AccountService accountService;
   private ResourceService resourceService;
   private SensorValueService sensorValueService;

   @Override
   @Transactional
   public void save(ControllerCommand c) {
      genericDAO.save(c);
   }

   @Override
   public List<ControllerCommandDTO> queryByUsername(String username){
	   User u = genericDAO.getByNonIdField(User.class, "username", username);
	   if(u == null)
		   return Collections.emptyList();
	   
	   // we want all open controller commands for this account by creation date 
	   DetachedCriteria criteria = DetachedCriteria.forClass(ControllerCommand.class)
	   .add(Restrictions.eq("account", u.getAccount()))
	   .add(Restrictions.eq("state", State.OPEN))
	   .addOrder(Order.asc("creationDate"));
	   
	   List<ControllerCommand> list = genericDAO.findByDetachedCriteria(criteria);
	   // now translate the DB model to JAXB model (grrr)
	   List<ControllerCommandDTO> listDTO = new ArrayList<ControllerCommandDTO>(list.size());
	   for(ControllerCommand command : list){
		   ControllerCommandDTO commandDTO;
		   // do some specific things for specific commands
		   switch(command.getType()){
		   case UPDATE_CONTROLLER:
			   commandDTO = new UpdateControllerCommandDTO(String.valueOf(command.getOid()));
			   break;
		   case UPLOAD_LOGS:
		      commandDTO = new UploadLogsControllerCommandDTO();
		      break;
         case INITIATE_PROXY:
            commandDTO = new InitiateProxyControllerCommandDTO();
            break;
		   default:
		      commandDTO = new ControllerCommandDTO(command.getType());
		      break;
		   }
		   BeanUtils.copyProperties(command, commandDTO);
		   listDTO.add(commandDTO);
	   }
	   return listDTO;
   }

   @Override
   @Transactional
   public ControllerCommand saveUploadLogsCommand(String username) {
      User user = accountService.loadByUsername(username);

      ControllerCommand command = new ControllerCommand(user.getAccount(), Type.UPLOAD_LOGS);
      save(command);
      return command;
   }

   @Override
   @Transactional
   public InitiateProxyControllerCommand saveProxyControllerCommand(User user, String url) {
      InitiateProxyControllerCommand command = new InitiateProxyControllerCommand(user.getAccount(), Type.INITIATE_PROXY, url);
      save(command);
      return command;
   }

   @Override
   @Transactional
   public void saveUpdateControllerCommand(String username, File resource) throws IOException{
      User user = accountService.loadByUsername(username);
      File userDir = resourceService.getUserFolder(user.getAccount().getOid(), Constant.CONTROLLER_UPDATES_PATH);
      // record this update command
      ControllerCommand command = new ControllerCommand(user.getAccount(), Type.UPDATE_CONTROLLER);
      save(command);
      // now use its ID as file name to save the update
      File destFile = new File(userDir, String.valueOf(command.getOid()));
      // save it
      FileUtils.copyFile(resource, destFile);
   }
   
   @Override
   @Transactional
   public void saveLogs(ControllerCommand controllerCommand, User user, File resource) throws IOException {
      // FIXME: make sure we can't overwrite the logs dir from a panel resource
      File userDir = resourceService.getUserFolder(user.getAccount().getOid(), Constant.CONTROLLER_LOGS_PATH);
      // save the logs in a special folder
      File logsFolder = new File(userDir, String.valueOf(controllerCommand.getOid()));
      if(logsFolder.exists())
         FileUtils.deleteDirectory(logsFolder);
      logsFolder.mkdirs();
      // now unzip in there
      ZipUtil.unzip(resource, logsFolder.getAbsolutePath());
      
      // and now log any new sensor values
      sensorValueService.updateSensorValues(user.getAccount(), logsFolder);
   }

   @Override
   public void closeControllerCommand(ControllerCommand controllerCommand) {
      controllerCommand.setState(State.DONE);
   }

   @Override
   @Transactional
   public ControllerCommand findControllerCommandById(Long id) {
      return genericDAO.getById(ControllerCommand.class, id);
   }

   @Override
   @Transactional
   public void update(ControllerCommand controllerCommand) {
      genericDAO.saveOrUpdate(controllerCommand);
   }

   //
   // Internal plumbing
   
   public void setAccountService(AccountService accountService) {
      this.accountService = accountService;
   }

   public void setResourceService(ResourceService resourceService) {
      this.resourceService = resourceService;
   }

   public void setSensorValueService(SensorValueService sensorValueService) {
      this.sensorValueService = sensorValueService;
   }

}
