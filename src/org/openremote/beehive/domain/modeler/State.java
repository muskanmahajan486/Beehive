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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.modeler.StateDTO;
import org.openremote.beehive.domain.BusinessEntity;

/**
 * The Class State.
 */
@Entity
@Table(name = "state")
public class State extends BusinessEntity {
   private static final long serialVersionUID = -4878125106767971531L;
   
   private String name = "state1";
   private String value = "";
   
   private CustomSensor sensor;
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(nullable = false)
   public CustomSensor getSensor() {
      return sensor;
   }

   public void setSensor(CustomSensor sensor) {
      this.sensor = sensor;
   }
   
   public StateDTO toDTO() {
      StateDTO dto = new StateDTO();
      dto.setId(getOid());
      dto.setName(this.name);
      dto.setValue(this.value);
      return dto;
   }
}
