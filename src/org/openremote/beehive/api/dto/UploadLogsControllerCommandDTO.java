package org.openremote.beehive.api.dto;

import org.openremote.beehive.domain.ControllerCommand;

@SuppressWarnings("serial")
public class UploadLogsControllerCommandDTO extends ControllerCommandDTO {
	
	public UploadLogsControllerCommandDTO() {
		super(ControllerCommand.Type.UPLOAD_LOGS);
	}

	public UploadLogsControllerCommandDTO(String targetURL) {
		this();
		setResource(targetURL);
	}
}
