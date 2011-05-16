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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.openremote.beehive.api.service.SensorValueService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.SensorValue;

public class SensorValueServiceImpl extends BaseAbstractService<SensorValue> implements SensorValueService{

   /** The logger. */
   private static Logger logger = Logger.getLogger(ControllerCommandServiceImpl.class);

   @Override
   public void updateSensorValues(Account account, File logsFolder) {
      File sensorFolder = new File(logsFolder, "sensor");
      if(!sensorFolder.exists()){
         logger.info("No sensor folder in uploaded logs");
         return;
      }
      // figure out the last update we had
      DetachedCriteria criteria = DetachedCriteria.forClass(SensorValue.class)
      .setProjection(Projections.max("time"));
      Date lastEntry = genericDAO.findOneByDetachedCriteria(criteria);
      /*
       * We get values from the client in milliseconds, but that info is discarded
       * by the MySQL database: http://bugs.mysql.com/bug.php?id=8523
       * So in order to make sure we don't keep storing the same sensor values
       * many times, we'll round our lastEntry up to the next second, thereby
       * skipping any new value that has happened within that second. I prefer
       * skipping a few values within a second than storing duplicate values
       * for that second on new uploads, but feel free to disagree and change that. 
       */
      if(lastEntry != null)
         lastEntry.setTime(lastEntry.getTime() + 1000);
      logger.info("Last entry: "+lastEntry);
      try{
         // read every log file in order, starting with the first one
         File logs = new File(sensorFolder, "sensor.log");
         if(!logs.exists()){
            logger.info("Done reading log files");
            return;
         }
         if(readLogs(account, logs, lastEntry) == LogReadingState.DONE){
            logger.info("Last log read because we encountered an old value");
            return;
         }
         // then all the rolled files
         int i = 1;
         while(true){
            logs = new File(sensorFolder, "sensor.log."+i);
            if(!logs.exists())
               break;
            if(readLogs(account, logs, lastEntry) == LogReadingState.DONE){
               logger.info("Last log read because we encountered an old value");
               return;
            }
            i++;
         }
      }catch(IOException x){
         logger.error("Exception while reading logged sensor values", x);
      }
      logger.info("Done reading log files");
   }

   private final static Pattern pattern = Pattern.compile("^INFO (\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2},\\d{1,3}) \\(sensor\\): ([^\u001E]+)\u001E(.+)$");
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

   private enum LogReadingState {
      CONTINUE, DONE;
   }

   private LogReadingState readLogs(Account account, File file, Date lastEntry) throws IOException  {
      logger.info("Reading logs from "+file.getAbsolutePath());
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      Matcher matcher = null;
      int oldValues = 0, newValues = 0;
      try{
         while((line = reader.readLine()) != null){
            // avoid allocating too much
            if(matcher == null)
               matcher = pattern.matcher(line);
            else
               matcher.reset(line);
            if(!matcher.matches()){
               logger.warn("Skipping invalid log line: "+line);
               continue;
            }
            String dateString = matcher.group(1);
            String id = matcher.group(2);
            String value = matcher.group(3);
            Date date;
            try {
               date = dateFormat.parse(dateString);
            } catch (ParseException e) {
               logger.warn("Invalid date format: "+line, e);
               continue;
            }
            if(lastEntry != null && !date.after(lastEntry)){
               oldValues++;
               continue;
            }
            logger.info("new entry is for "+date);
            SensorValue sensorValue = new SensorValue(account, id, value, date);
            genericDAO.save(sensorValue);
            newValues++;
         }
      }finally{
         try {
            reader.close();
         } catch (IOException e) {
            logger.error(e);
         }
      }
      if(oldValues > 0)
         logger.warn("Ignored "+oldValues+" old values");
      logger.warn("Saved "+newValues+" new values");
      return oldValues > 0 ? LogReadingState.DONE : LogReadingState.CONTINUE;
   }

   @Override
   public Number getAverage(Account account, String sensor, Date from, Date to) {
      DetachedCriteria criteria = DetachedCriteria.forClass(SensorValue.class)
      .add(Restrictions.ge("time", from))
      .add(Restrictions.lt("time", to))
      .add(Restrictions.eq("account", account))
      .add(Restrictions.eq("sensor", sensor))
      // this is convoluted and AVG(value) works OOTB with mysql, but not with the test DB
      // this version works with both DBs however
      .setProjection(Projections.sqlProjection("AVG(CAST(value AS DECIMAL)) AS average", 
            new String[]{"average"}, 
            new Type[]{Hibernate.FLOAT}));
      return genericDAO.<Number>findOneByDetachedCriteria(criteria);
   }

}
