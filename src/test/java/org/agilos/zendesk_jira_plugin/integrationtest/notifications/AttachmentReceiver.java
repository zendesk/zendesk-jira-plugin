package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

public class AttachmentReceiver extends Resource {
	private static Logger log = Logger.getLogger(AttachmentReceiver.class.getName());	

	/**
	 * Constructor with parameters invoked every time a new request is routed to
	 * this resource.
	 * 
	 * @param context
	 *                The parent context.
	 * @param request
	 *                The request to handle.
	 * @param response
	 *                The response to return.
	 */
	public AttachmentReceiver(Context context, Request request, Response response) {
		super(context, request, response);

		// This resource generates only HTML representations.
		getVariants().add(new Variant(MediaType.TEXT_HTML));
	}

	/**
	 * Mandatory. Specifies that this resource supports POST requests.
	 */
	public boolean allowPost() {
		return true;
	}
	/**
	 * Accepts and processes a representation posted to the resource. As
	 * response, the content of the uploaded file is sent back the client.
	 */
	@Override
	public void acceptRepresentation(Representation entity) {
		if (entity != null) {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {

				// The Apache FileUpload project parses HTTP requests which
				// conform to RFC 1867, "Form-based File Upload in HTML". That
				// is, if an HTTP request is submitted using the POST method,
				// and with a content type of "multipart/form-data", then
				// FileUpload can parse that request, and get all uploaded files
				// as FileItem.

				// 1/ Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(1000240);

				// 2/ Create a new file upload handler based on the Restlet
				// FileUpload extension that will parse Restlet requests and
				// generates FileItems.
				RestletFileUpload upload = new RestletFileUpload(factory);
				List<FileItem> items;
				try {
					// 3/ Request is parsed by the handler which generates a
					// list of FileItems
					items = upload.parseRequest(getRequest());

					// Process only the uploaded item called "fileToUpload" and
					// save it on disk
					boolean found = false;
					for (final Iterator<FileItem> it = items.iterator(); it.hasNext() && !found;) {
						FileItem fi = (FileItem) it.next();
						if (fi.getFieldName().equals("fileToUpload")) {
							found = true;
							File file = new File("c:\\temp\\file.txt");
							fi.write(file);
						}
					}

					Representation rep = new StringRepresentation("Response, ToDo",	MediaType.TEXT_PLAIN);
					// Set the representation of the resource once the POST
					// request has been handled.
					getResponse().setEntity(rep);
					// Set the status of the response.
					getResponse().setStatus(Status.SUCCESS_OK);
				} catch (Exception e) {
					// The message of all thrown exception is sent back to
					// client as simple plain text
					getResponse().setEntity(
							new StringRepresentation(e.getMessage(),
									MediaType.TEXT_PLAIN));
					getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					log.error(e);
				}
			}
		} else {
			// POST request with no entity.
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
