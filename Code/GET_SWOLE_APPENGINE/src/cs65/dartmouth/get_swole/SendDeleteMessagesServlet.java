/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cs65.dartmouth.get_swole;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs65.dartmouth.get_swole.gcm.Message;
import cs65.dartmouth.get_swole.gcm.Sender;


/**
 * Servlet that adds a new delete message to all registered devices.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class SendDeleteMessagesServlet extends BaseServlet {

	private Logger mLogger = Logger.getLogger(Datastore.class.getName());
	Sender mSender;
	final static int MAX_RETRY = 5;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
//		
//		// Create a sender based on servlet settings
//		String key = (String) config.getServletContext().getAttribute(ApiKeyInitializer.API_KEY);
//		mSender = new Sender(key);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// Should process the request to add a new message
		mLogger.log(Level.INFO, "doPost from senddeletemessage");
		
		// Get the list of devices
		List<String> devices = Datastore.getDevices();
		
		
		if (!devices.isEmpty()) {
			Message message = new Message(devices);
			long id = Long.valueOf(req.getParameter("id"));
			message.addData("messages", "delete" + ":" + id);
			
			mSender = new Sender(ApiKeyInitializer.API_KEY);
			mSender.send(message,MAX_RETRY);
			
			// Loop through devices and remove the id
			for (String device : devices) {
				Datastore.deleteHistoryEntry(device, Long.toString(id));
			}
		}
		
		// Redirect after posting
		resp.sendRedirect("/myruns_6_appengine");

	}
	
}
