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
package org.openremote.beehive.api.dto.modeler;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.AccountDTO;
import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.Device;
import org.openremote.beehive.domain.modeler.DeviceAttr;
import org.openremote.beehive.domain.modeler.DeviceCommand;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorCommandRef;
import org.openremote.beehive.domain.modeler.Slider;
import org.openremote.beehive.domain.modeler.SliderCommandRef;
import org.openremote.beehive.domain.modeler.SliderSensorRef;
import org.openremote.beehive.domain.modeler.Switch;
import org.openremote.beehive.domain.modeler.SwitchCommandOffRef;
import org.openremote.beehive.domain.modeler.SwitchCommandOnRef;
import org.openremote.beehive.domain.modeler.SwitchSensorRef;

/**
 * The Class is used for transmitting device info.
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "device")
public class DeviceDTO extends BusinessEntityDTO {
   private String name;
   private String vendor;
   private String model;
   
   private AccountDTO account;
   private List<DeviceAttrDTO> deviceAttrs;
   private List<DeviceCommandDTO> deviceCommands;
   private List<SensorDTO> sensors;
   private List<SwitchDTO> switchs;
   private List<SliderDTO> sliders;
   
   public String getName() {
      return name;
   }
   public String getVendor() {
      return vendor;
   }
   public String getModel() {
      return model;
   }
   @XmlElement(name="account")
   public AccountDTO getAccount() {
      return account;
   }
   @XmlElementWrapper(name = "deviceCommands")
   @XmlElement(name="deviceCommand")
   public List<DeviceCommandDTO> getDeviceCommands() {
      return deviceCommands;
   }
   @XmlElementWrapper(name = "sensors")
   @XmlElementRef(type=SensorDTO.class)
   public List<SensorDTO> getSensors() {
      return sensors;
   }
   @XmlElementWrapper(name = "switchs")
   @XmlElement(name="switch")
   public List<SwitchDTO> getSwitchs() {
      return switchs;
   }
   @XmlElementWrapper(name = "sliders")
   @XmlElement(name="slider")
   public List<SliderDTO> getSliders() {
      return sliders;
   }
   @XmlElementWrapper(name = "deviceAttrs")
   @XmlElement(name="deviceAttr")
   public List<DeviceAttrDTO> getDeviceAttrs() {
	   return deviceAttrs;
   }

   public void setName(String name) {
      this.name = name;
   }
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }
   public void setModel(String model) {
      this.model = model;
   }
   public void setAccount(AccountDTO account) {
      this.account = account;
   }
   public void setDeviceAttrs(List<DeviceAttrDTO> deviceAttrs) {
	  this.deviceAttrs = deviceAttrs;
   }
   public void setDeviceCommands(List<DeviceCommandDTO> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }
   public void setSensors(List<SensorDTO> sensors) {
      this.sensors = sensors;
   }
   public void setSwitchs(List<SwitchDTO> switchs) {
      this.switchs = switchs;
   }
   public void setSliders(List<SliderDTO> sliders) {
      this.sliders = sliders;
   }
   
   public Device toDevice() {
      Device device = new Device();
      device.setOid(getId());
      device.setName(name);
      device.setVendor(vendor);
      device.setModel(model);
      
      if (deviceAttrs != null && deviceAttrs.size() > 0) {
    	  for (DeviceAttrDTO deviceAttrDTO : deviceAttrs) {
    		  DeviceAttr deviceAttr = deviceAttrDTO.toDeviceAttr();
    		  deviceAttr.setDevice(device);
    		  device.getDeviceAttrs().add(deviceAttr);
		}
      }
      return device;
   }
   
   public Device toDeviceWithContents(Account dbAccount) {
      Device device = toDevice();
      device.setAccount(dbAccount);
      if (deviceCommands != null && deviceCommands.size() > 0) {
         for (DeviceCommandDTO deviceCommandDTO : deviceCommands) {
            DeviceCommand deviceCommand = deviceCommandDTO.toDeviceCommand();
            deviceCommand.setDevice(device);
            device.getDeviceCommands().add(deviceCommand);
         }
      }
      List<DeviceCommand> deviceCommands = device.getDeviceCommands();
      
      if (sensors != null && sensors.size() > 0) {
         for (SensorDTO sensorDTO : sensors) {
            Sensor sensor = sensorDTO.toSimpleSensor();
            sensor.setAccount(dbAccount);
            sensor.setDevice(device);
            
            SensorCommandRefDTO sensorCommandRefDTO = sensorDTO.getSensorCommandRef();
            if (sensorCommandRefDTO != null) {
               String commandName = sensorCommandRefDTO.getDeviceCommand().getName();
               for (DeviceCommand deviceCommand : deviceCommands) {
                  if (deviceCommand.getName().equals(commandName)) {
                     SensorCommandRef sensorCommandRef = new SensorCommandRef();
                     sensorCommandRef.setSensor(sensor);
                     sensorCommandRef.setDeviceCommand(deviceCommand);
                     sensor.setSensorCommandRef(sensorCommandRef);
                  }
               }
            }
            device.getSensors().add(sensor);
         }
      }
      List<Sensor> newSensors = device.getSensors();
      
      if (switchs != null && switchs.size() > 0) {
         for (SwitchDTO switchDTO : switchs) {
            Switch switchToggle = switchDTO.toSimpleSwitch();
            switchToggle.setAccount(dbAccount);
            switchToggle.setDevice(device);
            
            SwitchCommandOnRefDTO switchCommandOnRefDTO = switchDTO.getSwitchCommandOnRef();
            if (switchCommandOnRefDTO != null) {
               String commandName = switchCommandOnRefDTO.getDeviceCommand().getName();
               for (DeviceCommand deviceCommand : deviceCommands) {
                  if (deviceCommand.getName().equals(commandName)) {
                     SwitchCommandOnRef switchCommandOnRef = new SwitchCommandOnRef();
                     switchCommandOnRef.setOnSwitch(switchToggle);
                     switchCommandOnRef.setDeviceCommand(deviceCommand);
                     switchToggle.setSwitchCommandOnRef(switchCommandOnRef);
                  }
               }
            }
            
            SwitchCommandOffRefDTO switchCommandOffRefDTO = switchDTO.getSwitchCommandOffRef();
            if (switchCommandOffRefDTO != null) {
               String commandName = switchCommandOffRefDTO.getDeviceCommand().getName();
               for (DeviceCommand deviceCommand : deviceCommands) {
                  if (deviceCommand.getName().equals(commandName)) {
                     SwitchCommandOffRef switchCommandOffRef = new SwitchCommandOffRef();
                     switchCommandOffRef.setOffSwitch(switchToggle);
                     switchCommandOffRef.setDeviceCommand(deviceCommand);
                     switchToggle.setSwitchCommandOffRef(switchCommandOffRef);
                  }
               }
            }
            
            SwitchSensorRefDTO switchSensorRefDTO = switchDTO.getSwitchSensorRef();
            if (switchSensorRefDTO != null) {
               String sensorName = switchSensorRefDTO.getSensor().getName();
               for (Sensor newSensor : newSensors) {
                  if (newSensor.getName().equals(sensorName)) {
                     SwitchSensorRef switchSensorRef = new SwitchSensorRef();
                     switchSensorRef.setSensor(newSensor);
                     switchSensorRef.setSwitchToggle(switchToggle);
                     switchToggle.setSwitchSensorRef(switchSensorRef);
                  }
               }
            }
            
            device.getSwitchs().add(switchToggle);
         }
      }
      
      if (sliders != null && sliders.size() > 0) {
         for (SliderDTO sliderDTO : sliders) {
            Slider slider = sliderDTO.toSimpleSlider();
            slider.setAccount(dbAccount);
            slider.setDevice(device);
            
            SliderCommandRefDTO sliderCommandRefDTO = sliderDTO.getSetValueCmd();
            if (sliderCommandRefDTO != null) {
               String commandName = sliderCommandRefDTO.getDeviceCommand().getName();
               for (DeviceCommand deviceCommand : deviceCommands) {
                  if (deviceCommand.getName().equals(commandName)) {
                     SliderCommandRef sliderCommandRef = new SliderCommandRef();
                     sliderCommandRef.setDeviceCommand(deviceCommand);
                     sliderCommandRef.setSlider(slider);
                     slider.setSetValueCmd(sliderCommandRef);
                  }
               }
            }
            
            SliderSensorRefDTO sliderSensorRefDTO = sliderDTO.getSliderSensorRef();
            if (sliderSensorRefDTO != null) {
               String sensorName = sliderSensorRefDTO.getSensor().getName();
               for (Sensor newSensor : newSensors) {
                  if (newSensor.getName().equals(sensorName)) {
                     SliderSensorRef sliderSensorRef = new SliderSensorRef();
                     sliderSensorRef.setSensor(newSensor);
                     sliderSensorRef.setSlider(slider);
                     slider.setSliderSensorRef(sliderSensorRef);
                  }
               }
            }
            
            device.getSliders().add(slider);
         }
      }
      
      return device;
   }
}
