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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.SensorValueService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.User;

@Path("")
public class GraphRESTService extends RESTBaseService {
   private static final Logger logger = Logger.getLogger(GraphRESTService.class);

   private final static DateFormat yearPattern = new SimpleDateFormat("yyyy");
   private final static DateFormat monthPattern = new SimpleDateFormat("yyyy-MM");
   private final static DateFormat dayPattern = new SimpleDateFormat("yyyy-MM-dd");
   
   @Path("/user/{username}/graph/{sensor}")
   @GET
   @Produces( "image/png" )
   public Response getGraph(@PathParam("username") String username,
         @PathParam("sensor") String sensor,
         @QueryParam("period") String period,
         @QueryParam("width") Integer width,
         @QueryParam("height") Integer height,
         @QueryParam("title") String title,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) throws IOException {
      // check the credentials and load the user
      User user = checkCredentials(username, credentials);
      Account account = user.getAccount();
      
      // check required params
      if(StringUtils.isEmpty(sensor))
         return badRequestResponse("Required parameter missing: sensor");
      if(StringUtils.isEmpty(period))
         return badRequestResponse("Required parameter missing: period");
      
      if(width == null)
         width = 500;
      if(height == null)
         height = 500;

      byte[] bytes;
      if(period.matches("\\d{4}"))
         bytes = makeYearGraph(account, sensor, period, title, width, height);
      else if(period.matches("\\d{4}-\\d\\d?"))
         bytes = makeMonthGraph(account, sensor, period, title, width, height);
      else if(period.matches("\\d{4}-\\d\\d?-\\d\\d?"))
         bytes = makeDayGraph(account, sensor, period, title, width, height);
      else
         return badRequestResponse("Invalid period: "+period);
      return Response.ok(bytes).build();
   }

   private byte[] makeYearGraph(Account account, String sensor, String period, String title,
         Integer width, Integer height) throws IOException {
      GraphData data = getTimeSeries(account, sensor, period, yearPattern, Calendar.MONTH);
      return makeGraph(title, "MMM", data.min, data.max, width, height, data.series);
   }

   private byte[] makeMonthGraph(Account account, String sensor, String period, String title,
         Integer width, Integer height) throws IOException {
      GraphData data = getTimeSeries(account, sensor, period, monthPattern, Calendar.DAY_OF_MONTH);
      return makeGraph(title, "dd", data.min, data.max, width, height, data.series);
   }

   private byte[] makeDayGraph(Account account, String sensor, String period, String title,
         Integer width, Integer height) throws IOException {
      GraphData data = getTimeSeries(account, sensor, period, dayPattern, Calendar.HOUR_OF_DAY);
      return makeGraph(title, "HH", data.min, data.max, width, height, data.series); 
   }

   private static class GraphData {
      public TimeSeries series;
      public Date min, max;

      public GraphData(TimeSeries series, Date min, Date max) {
         this.series = series;
         this.min = min;
         this.max = max;
      }
   }
   
   private GraphData getTimeSeries(Account account, String sensor, String period, DateFormat pattern, int field) {
      Date date;
      try {
         date = pattern.parse(period);
      } catch (ParseException e) {
         // we should have checked it upwards
         throw new RuntimeException(e);
      }
      TimeSeries series = new TimeSeries("");
      Calendar cal = new GregorianCalendar();
      cal.setTime(date);
      int maxField = cal.getActualMaximum(field);
      Date max = date;
      
      for(int i=1;i<=maxField;i++){
         Date prev = cal.getTime();
         cal.add(field, 1);
         Date next = cal.getTime();
         Number val = getSensorValueService().getAverage(account, sensor, prev, next);
         if(val != null){
            RegularTimePeriod timePeriod = getTimePeriod(prev, field);
            logger.info("Time value for "+timePeriod+": "+val);
            series.add(timePeriod, val);
         }
         max = prev;
      }
      return new GraphData(series, date, max);
   }

   private RegularTimePeriod getTimePeriod(Date prev, int field) {
      if(field == Calendar.HOUR_OF_DAY)
         return new Hour(prev);
      else if(field == Calendar.DAY_OF_MONTH)
         return new Day(prev);
      else if(field == Calendar.MONTH)
         return new Month(prev);
      else{
         throw new IllegalArgumentException("Unknown field type: "+field);
      }
   }

   private byte[] makeGraph(String title, String dateFormat, Date min, Date max, Integer width, Integer height, TimeSeries series) throws IOException {
      // Add the series to a data set
      TimeSeriesCollection dataset = new TimeSeriesCollection();
      dataset.addSeries(series);
      // Make the graph
      JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // Title
              null, // x-axis Label, null saves space
              null, // y-axis Label, null saves space
              dataset, // Dataset
              true, // Show Legend
              false, // No tooltips
              false // No URLs
          );
      // make the whole background transparent
      chart.setBackgroundPaint(new Color(255,255,255,0));
      // remove the bottom legend per line (ex: blue line is foo)
      chart.removeLegend();
      XYPlot plot = chart.getXYPlot();
      // remove the plot border
      plot.setOutlineVisible(false);
      // make the plot background transparent
      plot.setBackgroundAlpha(0);
      // overwrite the date format
      DateAxis axis = (DateAxis) plot.getDomainAxis();
      axis.setDateFormatOverride(new SimpleDateFormat(dateFormat));
      if(min != null)
         axis.setMinimumDate(min);
      if(max != null)
         axis.setMaximumDate(max);
      // overwrite the number format
      NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setNumberFormatOverride(new DecimalFormat("#"));
      // make the line blue
      plot.getRenderer().setSeriesPaint(0, Color.blue);
      // make the line thicker
      plot.getRenderer().setSeriesStroke(0, new BasicStroke(2));
      BufferedImage image = chart.createBufferedImage(width, height);
      // we want a PNG with an alpha channel and uncompressed
      return ChartUtilities.encodeAsPNG(image, true, 0);
   }

   private User checkCredentials(String username, String credentials) {
      if (!authorize(username, credentials, false)) {
         throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
      }
      return getAccountService().loadByUsername(username);
   }
   
   protected SensorValueService getSensorValueService() {
      return (SensorValueService) getSpringContextInstance().getBean("sensorValueService");
   }

}
