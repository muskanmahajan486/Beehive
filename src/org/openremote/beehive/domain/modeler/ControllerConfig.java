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
package org.openremote.beehive.domain.modeler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.modeler.ControllerConfigDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.BusinessEntity;

/**
 * A domain class which represent a configuration. 
 * The name and value property is necessary.
 * The property validation is used to validate the value.
 * The property options represent the options for the value.
 *
 */
@Entity
@Table(name = "controller_config")
public class ControllerConfig extends BusinessEntity{
   private static final long serialVersionUID = -6443368320902438959L;
   
   private String category = "";
   private String name = "";
   private String value = "";
   
   private Account account = null;
   
   public ControllerConfig(){}
   
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
   @Column(nullable = false)
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @ManyToOne
   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }
   
   public String getCategory() {
      return category;
   }
   
   public void setCategory(String category) {
      this.category = category;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((category == null) ? 0 : category.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ControllerConfig other = (ControllerConfig) obj;
      if (category == null) {
         if (other.category != null) return false;
      } else if (!category.equals(other.category)) return false;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      return true;
   }
   
   public ControllerConfigDTO toDTO() {
      ControllerConfigDTO controllerConfigDTO = new ControllerConfigDTO();
      controllerConfigDTO.setId(getOid());
      controllerConfigDTO.setCategory(category);
      controllerConfigDTO.setName(name);
      controllerConfigDTO.setValue(value);
      return controllerConfigDTO;
   }
}
