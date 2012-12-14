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
package org.openremote.beehive.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "sensor_value")
public class SensorValue extends BusinessEntity {

   private Account account;
   private String sensor;
   private String value;
   private Date time;

   public SensorValue(Account account, String sensor, String value, Date time) {
      this.account = account;
      this.sensor = sensor;
      this.value = value;
      this.time = time;
   }

   public SensorValue(){}

   @JoinColumn(nullable = false)
   @ManyToOne
   public Account getAccount() {
      return account;
   }
   public void setAccount(Account account) {
      this.account = account;
   }

   @Column(nullable = false)
   public String getSensor() {
      return sensor;
   }
   public void setSensor(String sensor) {
      this.sensor = sensor;
   }

   @Column(nullable = false)
   public String getValue() {
      return value;
   }
   public void setValue(String value) {
      this.value = value;
   }

   @Column(nullable = false)
   public Date getTime() {
      return time;
   }
   public void setTime(Date time) {
      this.time = time;
   }
   
}
