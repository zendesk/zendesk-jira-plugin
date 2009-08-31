package org.agilos.zendesk_jira_plugin.integrationtest.notifications;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

public class AttachmentReceiver extends Resource {
	private static Logger log = Logger.getLogger(AttachmentReceiver.class.getName());		

	public static File ATTACHMENT_DIRECTORY = new File("target"+File.separator+"zendeskstub-attachments");

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
	@Override
	public void acceptRepresentation(Representation entity) {

		if (entity != null) {
			if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType(), true)) {
				Form query = getRequest().getResourceRef().getQueryAsForm(); 
				String attachmentName = query.getFirstValue("filename");

				InputStream inBuffer = null;
				OutputStream outBuffer = null;

				try{
					File attachmentFile = new File(ATTACHMENT_DIRECTORY+File.separator +attachmentName);
					FileOutputStream out = new FileOutputStream(attachmentFile);

					inBuffer = new BufferedInputStream(entity.getStream());
					outBuffer = new BufferedOutputStream(out);

					while(true){
						int bytedata = inBuffer.read();
						if(bytedata == -1)
							break;
						out.write(bytedata);
					}
				} catch (IOException e) {
					log.error("Failed to save attachment file to "+attachmentName, e);
				}

				finally{
					if(inBuffer != null)
						try {
							inBuffer.close();
						} catch (IOException e) {
							log.error("Failed to close atttachment inputBuffer",e);
						}
					if(outBuffer !=null)
						try {
							outBuffer.close();
						} catch (IOException e) {
							log.error("Failed to close atttachment outBuffer",e);
						}
				}

				Representation rep = new StringRepresentation("<uploads token=\"abc123\">\n"+
						"\t<attachments>\n"+
						"\t\t<attachment>789</attachment>\n"+
						"\t</attachments>\n"+
						"</uploads>",	MediaType.TEXT_PLAIN);
				// Set the representation of the resource once the POST request has been handled.
				getResponse().setEntity(rep);
				// Set the status of the response.
				getResponse().setStatus(Status.SUCCESS_OK);
			} else {
				getResponse().setEntity(new StringRepresentation("Please use content type "+MediaType.APPLICATION_WWW_FORM,
						MediaType.TEXT_PLAIN));
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}

		} else {
			// POST request with no entity.
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
