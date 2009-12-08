package it.org.agilos.zendesk_jira_plugin.integrationtest;

import org.apache.log4j.Logger;

public class IssueEditor {
	private Logger log = Logger.getLogger(NotificationTest.class.getName());
	
	private static final String SUMMERY_FIELD="summary";
	private static final String DESCRIPTION_FIELD="description";
	private static final String COMMENT_FIELD="comment";

	IssueEditor(String issueKey) {
//		fthFatory.getTester().setWorkingForm("quicksearch");
//		fthFatory.getTester().setFormElement("searchString", issueKey);
//        submit();

//        fthFatory.getTester().clickLinkWithText("Edit");
//        fthFatory.getTester().assertTextPresent("Edit Issue");
//		fthFatory.getTester().clickLink("edit_issue");
	}
	
//	public void setSummery(String newSummery) {
//		log.debug("Setting summery to "+newSummery);
//		fthFatory.getTester().setFormElement(SUMMERY_FIELD, newSummery);
//	}
//	
//	public void setDescription(String newDescription) {
//		log.debug("Setting description to "+newDescription);
//		fthFatory.getTester().setFormElement(DESCRIPTION_FIELD, newDescription);
//	}
//
//	public void setComment(String comment) {
//		log.debug("Setting comment to "+comment);
//		
//		fthFatory.getTester().setFormElement(COMMENT_FIELD, comment);
//	}
//	
//	public void submit() {
//		log.info("Submitting issue changes ");
//		fthFatory.getTester().submit("Update");
//		try {
//			log.debug("Response was:"+fthFatory.getTester().getDialog().getResponse().getText());
//		} catch (IOException e) {
//			log.error(e);
//		}
//
//		fthFatory.getTester().submit("Update");
//		}
}
