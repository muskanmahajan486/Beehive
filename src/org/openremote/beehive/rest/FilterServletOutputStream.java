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
package org.openremote.beehive.rest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * Created by IntelliJ IDEA. User: finalist Date: Mar 5, 2009 Time: 2:11:00 PM To change this template use File |
 * Settings | File Templates.
 */

/**
 * @author allen.wei
 */
public class FilterServletOutputStream extends ServletOutputStream {
   private DataOutputStream stream;

   public FilterServletOutputStream(OutputStream output) {
      stream = new DataOutputStream(output);
   }

   public void write(int b) throws IOException {
      stream.write(b);
   }

   public void write(byte[] b) throws IOException {
      stream.write(b);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      stream.write(b, off, len);
   }
}
