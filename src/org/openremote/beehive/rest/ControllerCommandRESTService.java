package org.openremote.beehive.rest;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.dto.ControllerCommandDTO;
import org.openremote.beehive.api.dto.UpdateControllerCommandDTO;
import org.openremote.beehive.api.dto.UploadLogsControllerCommandDTO;
import org.openremote.beehive.api.service.ControllerCommandService;
import org.openremote.beehive.domain.ControllerCommand;
import org.openremote.beehive.domain.ControllerCommand.State;
import org.openremote.beehive.domain.User;

@Path("")
public class ControllerCommandRESTService extends RESTBaseService {

   private static final Logger logger = Logger.getLogger(ControllerCommandRESTService.class);

   //
   // General commands
   
   @Path("/user/{username}/command-queue")
   @GET
   @Produces( MediaType.APPLICATION_XML )
   public Response getControllerCommandsXML(@PathParam("username") String username,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
	   return getControllerCommands(username, credentials, MediaType.APPLICATION_XML_TYPE);
   }
   
   @Path("/user/{username}/command-queue")
   @GET
   @Produces( MediaType.APPLICATION_JSON )
   public Response getControllerCommandsJSON(@PathParam("username") String username,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {
	   return getControllerCommands(username, credentials, MediaType.APPLICATION_JSON_TYPE);
   }
   
   private Response getControllerCommands(String username, String credentials, MediaType mediaType){
      // make sure we have valid credentials
      checkCredentials(credentials);

      UriBuilder updateControllerUriBuilder = getUriBuilder(ResourceRESTService.class, "getResource");
      UriBuilder uploadLogsUriBuilder = getUriBuilder(ControllerCommandRESTService.class, "postLogs");
      UriBuilder closeCommandUriBuilder = getUriBuilder(ControllerCommandRESTService.class, "closeControllerCommand");

      ControllerCommandService controllerCommandService = getControllerCommandService();
      List<ControllerCommandDTO> commands = controllerCommandService.queryByUsername(username);
      
      // inject some URLs in there
      for(ControllerCommandDTO command : commands){
         if(command instanceof UpdateControllerCommandDTO){
            UpdateControllerCommandDTO updateCommand = ((UpdateControllerCommandDTO)command);
            /* We have to do some hacking here to get the proper subfolder inserted and not escaped
             * I (Stef Epardaud) have tried other means to do this, but short of having a special
             * endpoint for which we can get a UriBuilder, this is the best I can do. I also tried
             * with UriBuilder.buildFromEncoded and encoding myself the path so that the slash would
             * not be URL-encoded (since it's meaningful) but this version of RESTEasy seems to encode
             * it anyways (1.0), which is probably a bug. In the end I make a URL to a resource with
             * no path and add the manually encoded path to it. It works but it ain't pretty.
             */
            String path = Constant.CONTROLLER_UPDATES_PATH + "/" + updateCommand.getResourceName();
            updateCommand.setResource(updateControllerUriBuilder.build(username, "").toString()+path);
         }else if(command instanceof UploadLogsControllerCommandDTO){
            UploadLogsControllerCommandDTO uploadLogsCommand = (UploadLogsControllerCommandDTO) command;
            uploadLogsCommand.setResource(uploadLogsUriBuilder.build(username, uploadLogsCommand.getOid()).toString());
         }
         command.setClose(closeCommandUriBuilder.build(command.getOid()).toString());
      }

      /*
       * We have two serialisations for the list of commands. They differ
       * because the JSON mapping doesn't support ordered lists of mixed elements.
       * See http://www.xml.com/lpt/a/1658 for examples.
       */
      if(mediaType == MediaType.APPLICATION_JSON_TYPE)
    	  return buildResponse(new ControllerCommandListingJSON(commands));
      return buildResponse(new ControllerCommandListingXML(commands));
   }

   @Path("/command-queue/{id}")
   @DELETE
   public Response closeControllerCommand(@PathParam("id") Long id,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      // make sure we have valid credentials
      User user = checkCredentials(credentials);

      ControllerCommandService controllerCommandService = getControllerCommandService();
      ControllerCommand controllerCommand = notFoundIfNull(controllerCommandService.findControllerCommandById(id));
      
      // check for permissions
      checkPermissions(user, controllerCommand);
      
      logger.info("Closing controller command "+id+" from "+user.getUsername());
      controllerCommandService.closeControllerCommand(controllerCommand);
      controllerCommandService.update(controllerCommand);
      
      return Response.ok().build();
   }

   //
   // Specific commands
   
   @Path("/user/{username}/command-queue/update-controller")
   @POST
   public Response addUpdateControllerCommand(File resource,
         @PathParam("username") String username,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      // make sure we have valid credentials
      checkCredentials(username, credentials);

      // check that we have data
      checkNotEmpty(resource);

      ControllerCommandService controllerCommandService = getControllerCommandService();
      try {
         logger.info("Saving update-controller command for "+username+" ("+resource.length()+" bytes)");
         controllerCommandService.saveUpdateControllerCommand(username, resource);
      } catch (IOException e) {
         logger.error("Failed to save update-controller command", e);
         throw new WebApplicationException(e,Response.Status.INTERNAL_SERVER_ERROR);
      }

      return Response.ok().build();
   }

   @Path("/user/{username}/command-queue/upload-logs")
   @POST
   public Response addUploadLogsCommand(@PathParam("username") String username,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      // make sure we have valid credentials
      checkCredentials(username, credentials);

      logger.info("Saving upload-logs command for "+username);

      ControllerCommandService controllerCommandService = getControllerCommandService();
      ControllerCommand command = controllerCommandService.saveUploadLogsCommand(username);
      
      URI postLogsURI = getURI(ControllerCommandRESTService.class, "postLogs", username, command.getOid());

      return Response.created(postLogsURI).build();
   }

   @Path("/user/{username}/resources/logs/{id}")
   @POST
   public Response postLogs(@PathParam("id") Long id,
         @PathParam("username") String username,
         File resource,
         @HeaderParam(Constant.HTTP_AUTH_HEADER_NAME) String credentials) {

      // make sure we have valid credentials
      User user = checkCredentials(username, credentials);

      // check that we have data
      checkNotEmpty(resource);

      // find the command
      ControllerCommandService controllerCommandService = getControllerCommandService();
      ControllerCommand controllerCommand = notFoundIfNull(controllerCommandService.findControllerCommandById(id));
      
      // check that we own this command
      checkPermissions(user, controllerCommand);

      // let's not post logs when the command is already done
      if(controllerCommand.getState() == State.DONE){
         return badRequestResponse("Command already closed");
      }

      try {
         logger.info("Saving logs for "+user.getUsername()+" ("+resource.length()+" bytes)");
         controllerCommandService.saveLogs(controllerCommand, user, resource);
      } catch (IOException e) {
         logger.error("Failed to save logs", e);
         throw new WebApplicationException(e,Response.Status.INTERNAL_SERVER_ERROR);
      }
      // mark it as closed
      controllerCommandService.closeControllerCommand(controllerCommand);

      return Response.ok().build();
   }

   // Internal kitchen

   private User checkCredentials(String username, String credentials) {
      if (!authorize(username, credentials, false)) {
         throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
      }
      return getAccountService().loadByUsername(username);
   }
   
   private User checkCredentials(String credentials) {
      if (!authorize(credentials, false)) {
         throw new WebApplicationException(HttpURLConnection.HTTP_UNAUTHORIZED);
      }
      return getAccountService().loadByHTTPBasicCredentials(credentials);
   }
   
   private void checkPermissions(User user, ControllerCommand controllerCommand) {
      if(controllerCommand.getAccount().getOid() != user.getAccount().getOid()){
         throw new WebApplicationException(HttpURLConnection.HTTP_FORBIDDEN);
      }
   }
   
   protected ControllerCommandService getControllerCommandService() {
      return (ControllerCommandService) getSpringContextInstance().getBean("controllerCommandService");
   }

}
