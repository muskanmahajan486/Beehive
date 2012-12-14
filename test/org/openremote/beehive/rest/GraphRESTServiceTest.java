/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.HttpResponse;
import org.openremote.beehive.Constant;
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.ControllerCommand.Type;
import org.openremote.beehive.domain.SensorValue;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.exception.FilePermissionException;
import org.openremote.beehive.rest.service.ControllerCommandRESTTestService;
import org.openremote.beehive.rest.service.GraphRESTTestService;
import org.openremote.beehive.utils.RESTTestUtils;
import org.openremote.beehive.utils.ZipUtil;

import com.sun.syndication.io.impl.Base64;

public class GraphRESTServiceTest extends TemplateTestBase {

   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   private User user;

   @Override
   protected void setUp() throws Exception {
      super.setUp();

      user = new User();
      user.setUsername("stef");
      // this here is called hashed, because we hash in the db. This is MD5('stef{stef}')
      user.setPassword("447cf0ee0366f1225e2509ee3c82fee3");
      genericDAO.save(user);

      Account a = new Account();
      user.setAccount(a);
      genericDAO.save(a);
   }

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      User u = genericDAO.getByNonIdField(User.class, "username", "stef");
      genericDAO.delete(u);
      genericDAO.deleteAll(genericDAO.loadAll(SensorValue.class));
      genericDAO.deleteAll(genericDAO.loadAll(ControllerCommand.class));
   }

   protected void addCredential(MockHttpRequest mockHttpRequest) {
      mockHttpRequest.header(Constant.HTTP_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("stef:stef"));
   }

   public void testSingleFile() throws URISyntaxException, IOException, InterruptedException {
      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(SensorValue.class).setProjection(Projections.rowCount())));

      List<SensorValue> sensorValues = testSensorValues(
            new String[]{
                  "INFO 2011-01-02 1:22:33,000 (sensor): sensor name1\u001E30",
                  "INFO 2011-01-03 1:22:33,000 (sensor): sensor name2\u001E40",
            }
      );
      assertEquals(2, sensorValues.size());

      assertEquals(sensorValues.get(0), user.getAccount(), new Date(111, 0, 2, 1, 22, 33), "sensor name1", "30");
      assertEquals(sensorValues.get(1), user.getAccount(), new Date(111, 0, 3, 1, 22, 33), "sensor name2", "40");
   }

   public void testMultipleFiles() throws URISyntaxException, IOException, InterruptedException {
      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(SensorValue.class).setProjection(Projections.rowCount())));
      
      List<SensorValue> sensorValues = testSensorValues(
            new String[]{
                  "INFO 2011-01-02 1:22:33,000 (sensor): sensor name\u001E30",
                  "INFO 2011-01-03 1:22:33,000 (sensor): sensor name\u001E40",
            },
            new String[]{
                  "INFO 2010-01-02 1:22:33,000 (sensor): sensor name\u001E30",
                  "INFO 2010-01-03 1:22:33,000 (sensor): sensor name\u001E40",
            }
      );
      assertEquals(4, sensorValues.size());

      assertEquals(sensorValues.get(0), user.getAccount(), new Date(110, 0, 2, 1, 22, 33), "sensor name", "30");
      assertEquals(sensorValues.get(1), user.getAccount(), new Date(110, 0, 3, 1, 22, 33), "sensor name", "40");
      assertEquals(sensorValues.get(2), user.getAccount(), new Date(111, 0, 2, 1, 22, 33), "sensor name", "30");
      assertEquals(sensorValues.get(3), user.getAccount(), new Date(111, 0, 3, 1, 22, 33), "sensor name", "40");
   }

   /*
    * This test won't work as long as we'll be using a test DB which doesn't support MAX(time) as
    * this is used in SensorValueServiceImpl
    *       DetachedCriteria criteria = DetachedCriteria.forClass(SensorValue.class)
      .setProjection(Projections.max("time"));
    */
   /*
   public void testDuplicateValues() throws URISyntaxException, IOException, InterruptedException {
      // make sure we start with nothing
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(ControllerCommand.class).setProjection(Projections.rowCount())));
      assertEquals(0, genericDAO.countByDetachedCriteria(DetachedCriteria.forClass(SensorValue.class).setProjection(Projections.rowCount())));

      List<SensorValue> sensorValues = testSensorValues(
            new String[]{
                  "INFO 2011-01-02 1:22:33,000 (sensor): sensor name\u001E30",
                  "INFO 2011-01-03 1:22:33,000 (sensor): sensor name\u001E40",
            }
      );
      assertEquals(2, sensorValues.size());

      sensorValues = testSensorValues(
            new String[]{
                  "INFO 2011-01-02 1:22:33,000 (sensor): sensor name\u001E30",
                  "INFO 2011-01-03 1:22:33,000 (sensor): sensor name\u001E40",
                  "INFO 2011-01-04 1:22:33,000 (sensor): sensor name\u001E50",
            }
      );
      assertEquals(3, sensorValues.size());
      assertEquals(sensorValues.get(0), user.getAccount(), new Date(111, 0, 2, 1, 22, 33), "sensor name", "30");
      assertEquals(sensorValues.get(1), user.getAccount(), new Date(111, 0, 3, 1, 22, 33), "sensor name", "40");
      assertEquals(sensorValues.get(2), user.getAccount(), new Date(111, 0, 4, 1, 22, 33), "sensor name", "50");
   }
   */

   public void testGraphNoData() throws URISyntaxException, IOException{
      testGraph("2000");
   }

   public void testGraphUnknownSensor() throws URISyntaxException, IOException{
      MockHttpResponse response = getGraph("sensor_name", "2000");

      checkImage(response, 500, 500);
   }

   public void testGraphWidthHeight() throws URISyntaxException, IOException{
      MockHttpResponse response = getGraph("sensor_name", "2000", 200, 300);

      checkImage(response, 200, 300);
   }

   private void checkImage(MockHttpResponse response, int width, int height) throws IOException {
      assertEquals(200, response.getStatus());
      assertTrue(response.getOutput().length > 0);
      BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
      assertNotNull(image);
      assertEquals(width, image.getWidth());
      assertEquals(height, image.getHeight());
   }

   private MockHttpResponse getGraph(String sensor, String period) throws URISyntaxException {
      return getGraph(sensor, period, null, null);
   }

   private MockHttpResponse getGraph(String sensor, String period, Integer width, Integer height) throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(GraphRESTTestService.class);
      String url = "/user/stef/graph/"+sensor+"?period="+period;
      if(width != null)
         url += "&width="+width;
      if(height != null)
         url += "&height="+height;
      MockHttpRequest mockHttpRequest = MockHttpRequest.get(url);
      addCredential(mockHttpRequest);
      MockHttpResponse response = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, response);
      return response;
   }

   public void testGraphYear() throws URISyntaxException, IOException{
      Date now = new Date();
      testGraph(String.valueOf(now.getYear() + 1900));
   }

   public void testGraphMonth() throws URISyntaxException, IOException{
      Date now = new Date();
      testGraph((now.getYear() + 1900)+"-"+(now.getMonth()+1));
   }

   public void testGraphDay() throws URISyntaxException, IOException{
      Date now = new Date();
      testGraph((now.getYear() + 1900)+"-"+(now.getMonth()+1)+"-"+now.getDate());
   }

   private void testGraph(String period) throws URISyntaxException, IOException{
      SensorValue sensorValue = new SensorValue(user.getAccount(), "sensor_name", "23", new Date());
      genericDAO.save(sensorValue);
      
      // now get the graph
      MockHttpResponse response = getGraph("sensor_name", period);
      
      checkImage(response, 500, 500);
   }
   
   private List<SensorValue> testSensorValues(String[]... logs) throws URISyntaxException, IOException {
      // add a log upload command
      User u = genericDAO.getByNonIdField(User.class, "username", "stef");
      ControllerCommand controllerCommand = new ControllerCommand(u.getAccount(), Type.UPLOAD_LOGS);
      genericDAO.save(controllerCommand);
      
      // now post to it
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(ControllerCommandRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/user/stef/resources/logs/"+controllerCommand.getOid());
      mockHttpRequest.contentType("application/octet-stream");
      // make a special zip
      File zipFile = zip(logs);

      mockHttpRequest.content(FileUtils.readFileToByteArray(zipFile));
      addCredential(mockHttpRequest);
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      assertEquals(HttpURLConnection.HTTP_OK, mockHttpResponse.getStatus());

      // make sure the values were taken in
      return genericDAO.findByDetachedCriteria(DetachedCriteria.forClass(SensorValue.class).addOrder(Order.asc("time")));
   }

   private void assertEquals(SensorValue sensorValue, Account account, Date date,
         String name, String value) {
      assertEquals(sensorValue.getAccount(), account);
      assertEquals(sensorValue.getTime(), date);
      assertEquals(sensorValue.getSensor(), name);
      assertEquals(sensorValue.getValue(), value);
   }

   private File zip(String[]... logs) throws IOException {
      File logsDir = makeTmpDir();
      File sensorLogsDir = new File(logsDir, "sensor");
      sensorLogsDir.mkdirs();
      File sensorLog = new File(sensorLogsDir, "sensor.log");
      Collection lines = Arrays.asList(logs[0]);
      FileUtils.writeLines(sensorLog, lines);
      for(int i=1;i<logs.length;i++){
         sensorLog = new File(sensorLogsDir, "sensor.log."+i);
         lines = Arrays.asList(logs[i]);
         FileUtils.writeLines(sensorLog, lines);
      }
      File zipFile = File.createTempFile("logs", ".zip");
      ZipUtil.zip(logsDir.getAbsolutePath(), zipFile);
      return zipFile;
   }

   private File makeTmpDir() throws IOException {
      File deployFile = File.createTempFile("test", "dir");
      deployFile.delete();
      deployFile.mkdir();
      deployFile.deleteOnExit();
      return deployFile;
   }

}
