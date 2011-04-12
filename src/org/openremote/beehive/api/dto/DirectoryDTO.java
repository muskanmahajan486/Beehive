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
package org.openremote.beehive.api.dto;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * File system directory DTO. Used for browsing resources.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@XmlRootElement(name = "directory")
public class DirectoryDTO {
   String name;
   List<FileDTO> files = new LinkedList<FileDTO>();

   public DirectoryDTO() {
   }

   public DirectoryDTO(File directory){
      this.name = directory.getName();
      for(File f : directory.listFiles()){
         files.add(new FileDTO(f));
      }
   }
   
   @XmlAttribute
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlElementWrapper(name = "files")
   @XmlElement(name = "file")
   public List<FileDTO> getFiles() {
      return files;
   }

   public void setFiles(List<FileDTO> files) {
      this.files = files;
   }

   /**
    * DTO for directory children
    */
   public static class FileDTO {
      private String name;
      private boolean directory;
      private Long size;
      
      public FileDTO() {
      }
      public FileDTO(File file){
         this.name = file.getName();
         this.directory = file.isDirectory();
         if(!this.directory)
            this.size = file.length();
      }
      
      @XmlAttribute
      public String getName() {
         return name;
      }
      public void setName(String name) {
         this.name = name;
      }
      @XmlAttribute
      public boolean isDirectory() {
         return directory;
      }
      public void setDirectory(boolean directory) {
         this.directory = directory;
      }
      @XmlAttribute
      public Long getSize() {
         return size;
      }
      public void setSize(Long size) {
         this.size = size;
      }
   }
}
