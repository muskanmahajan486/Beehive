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
package org.openremote.beehive.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.dto.RemoteSectionDTO;
import org.openremote.beehive.api.service.CodeService;
import org.openremote.beehive.api.service.ModelService;
import org.openremote.beehive.api.service.RemoteOptionService;
import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.api.service.VendorService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Controller for Beehive database UI Interface User: tomsky Date: 2010-11-26 Time: 17:27:22
 * The interface is more human-readable.
 * 
 * User should call this by url like "http://org.openremote.beehive/database.html?vendor=sony&model=RM-862&sectionId=2050".
 * The params "vendor","model" and "sectionId" are optional.
 */
public class BeehiveLIRCDatabaseController extends MultiActionController {

    private static final Logger logger = Logger.getLogger(BeehiveLIRCDatabaseController.class);
    
   private VendorService vendorService;
   private ModelService modelService;
   private RemoteSectionService remoteSectionService;
   private RemoteOptionService remoteOptionService;
   private CodeService codeService;

   public void setVendorService(VendorService vendorService) {
      this.vendorService = vendorService;
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }

   public void setRemoteSectionService(RemoteSectionService remoteSectionService) {
      this.remoteSectionService = remoteSectionService;
   }

   public void setRemoteOptionService(RemoteOptionService remoteOptionService) {
      this.remoteOptionService = remoteOptionService;
   }

   public void setCodeService(CodeService codeService) {
      this.codeService = codeService;
   }

   private String index;

   /**
    * Default method in controller
    * 
    * @param request
    *           HttpServletRequest
    * @param response
    *           HttpServletResponse
    * @return ModelAndView
    */
   public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
      ModelAndView mav = new ModelAndView(index);
      String vendorName = request.getParameter("vendor");
      String modelName = request.getParameter("model");
      String sectionId = request.getParameter("sectionId");
      
      mav.addObject("vendors", vendorService.loadAllVendors());
      
      if (vendorName != null) {
          if (vendorService.loadByName(vendorName) != null) {
              mav.addObject("vendorName", vendorName);
              mav.addObject("models", modelService.findModelsByVendorName(vendorName));
          }
          
      }
      
      if (vendorName != null && modelName != null) {
          ModelDTO modelDTO = modelService.loadByVendorNameAndModelName(vendorName, modelName);
          if(modelDTO != null) {
              mav.addObject("modelName", modelName);
              long modelId = modelDTO.getId();
              List<RemoteSectionDTO> sectionDTOs = remoteSectionService.findByModelId(modelId);
              if (sectionDTOs.size() > 1) {
                  mav.addObject("showSection", true);
                  mav.addObject("sections", sectionDTOs);
                  showDetailBySectionId(mav, sectionId);
              } else if (sectionDTOs.size() == 1){
                  showLIRCDetailByModelId(mav, modelId);
              }
          }
      }
      return mav;
   }

    /**
     * The model has more than one sections and the user had selected a section.
     */
    private void showDetailBySectionId(ModelAndView mav, String sectionId) {
        if (sectionId != null) {
              try {
                  long sectionOId = Long.parseLong(sectionId);
                  if (remoteSectionService.loadSectionById(sectionOId) != null) {
                      mav.addObject("showDetail", true);
                      mav.addObject("sectionId", sectionOId);
                      mav.addObject("model", remoteSectionService.loadModelById(sectionOId));
                      mav.addObject("section", remoteSectionService.loadSectionById(sectionOId));
                      mav.addObject("options", remoteOptionService.findByRemoteSectionId(sectionOId));
                      mav.addObject("codes", codeService.findByRemoteSectionId(sectionOId));
                  }
              } catch (NumberFormatException e) {
                  logger.error("The sectionId " + sectionId + " should be a integer.");
              }
          }
    }

    /**
     * The model has only one section.
     */
    private void showLIRCDetailByModelId(ModelAndView mav, long modelId) {
        RemoteSectionDTO remoteSectionDTO = remoteSectionService.loadFisrtRemoteSectionByModelId(modelId);
          mav.addObject("showDetail", true);
          mav.addObject("model", modelService.loadModelById(modelId));
          mav.addObject("section", remoteSectionDTO);
          mav.addObject("options", remoteOptionService.findByRemoteSectionId(remoteSectionDTO.getId()));
          mav.addObject("codes", codeService.findByRemoteSectionId(remoteSectionDTO.getId()));
    }

   public void setIndex(String index) {
      this.index = index;
   }

}
