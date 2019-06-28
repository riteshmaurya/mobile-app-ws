package com.rm.app.ws.ui.model.response;

public enum ErrorMessages {

	MISSING_REQUIRED_FIELD("Missing required fields. Please check documentation for required fields"),
	RECORD_ALREADY_EXISTS("Record already  exists"),
	INTERNAL_SERVER_ERROR("Internal Server Error"),
	NO_RECORD_FOUND("No record found"),
	AUTHENTICATION_FAILED("Authentication Failed"),
	COULDNOT_UPDATE_RECORD("Could not update record"),
	COULDNOT_DELETE_RECORD("Could not delete record"),
	EMAIL_NOT_VERIFIED("Email couldnot be verified");
	
	private String errorMessage;

	private ErrorMessages(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
}
