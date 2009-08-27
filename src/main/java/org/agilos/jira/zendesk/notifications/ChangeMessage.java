package org.agilos.jira.zendesk.notifications;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ChangeMessage {
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private Document document;
	private Element comments = null;	
	private Node ticket;
	public static boolean publicComments = true;
	private String author;
	private String issueString;
	private StringBuffer changeString = new StringBuffer();
	private String jiraComment = null;

	public ChangeMessage(String author, String jiraIssue) throws ParserConfigurationException {
		this.author = author;
		this.issueString = jiraIssue;
		document = factory.newDocumentBuilder().getDOMImplementation().createDocument(null,null,null); 
		
		ticket = document.createElement("ticket");
	}
	
	public void addSummeryChange(String newValue, String oldValue) { addChange("subject", "Summery", newValue, oldValue); }
	public void addDescriptionChange(String newValue, String oldValue) { addChange("description", "Description", newValue, oldValue); }
	public void addKeyChange(String newValue, String oldValue) { addChange("external-id", "Key", newValue, oldValue); }
	
	/**
	 * Adds a change of a a Zendesk mapped attribute to the change message
	 * @param zendeskFieldID The Zendesk attribute the JIRA field should be mapped to. If null the no attribute update element is added to the message, 
	 * eg. corresponds to the {@link #addChange(String, String)} method
	 * @param jiraFieldID
	 * @param newValue
	 */
	public void addChange(String zendeskFieldID, String jiraFieldID, String newValue, String oldValue) {
		if (zendeskFieldID != null) {
			Element element = document.createElement(zendeskFieldID);
			element.setTextContent(newValue);
			ticket.appendChild(element);	
		}

		addChange(jiraFieldID, newValue, oldValue);
	}
	
	public void addChange(String jiraFieldID, String newValue, String oldValue) {
		changeString.append("\n"+capitalizeFirstLetter(jiraFieldID)+ ": "+newValue);//+" (was: "+oldValue+"\n");
		//changeString.append("<i>"+jiraFieldID+"</i>" + " changed to "+newValue+"<i></i> (was: "+oldValue+"<br>");
	}
	
	public void addComment(String comment) {
		this.jiraComment = comment;
	}
	
	private void createComment(String message) {
		Element comment = document.createElement("comment");

		Element isPublic = document.createElement("is-public");
		if (publicComments) {
			isPublic.setTextContent("true");
		} else {
			isPublic.setTextContent("false");
		}
		comment.appendChild(isPublic);

		Element value = document.createElement("value");		
		value.setTextContent(message);		
		comment.appendChild(value);   	
		
		// Zendesk will only accept a ticket update, if at least one field is change
		if (changeString.length() > 0) {
			comments = document.createElement("comments");
			comments.setAttribute("type", "array");
			comments.appendChild(comment);		
			ticket.appendChild(comments);
			document.appendChild(ticket);	
		} else {// In case of no Zendesk field updates, the 'naked' comment is sendt
			document.appendChild(comment);
		}
	}	
	
	public boolean isEmpty() {
		return (changeString.length() == 0 &&
				jiraComment == null);
	}
	
	public Document getDocument() {
		StringBuffer comment = new StringBuffer();
		comment.append(author+" has updated JIRA issue "+issueString+" with:");
		
		if (changeString.length() != 0) comment.append(changeString+"\n");
		
		if (jiraComment != null) {
			comment.append("Comment: "+jiraComment+"\n");
		}
		createComment(comment.toString());
		return document;
	}

	private String capitalizeFirstLetter(String inputWord) {
		String firstLetter = inputWord.substring(0,1);  // Get first letter
		String remainder   = inputWord.substring(1);    // Get remainder of word.
		return firstLetter.toUpperCase() + remainder.toLowerCase();
	}
}
