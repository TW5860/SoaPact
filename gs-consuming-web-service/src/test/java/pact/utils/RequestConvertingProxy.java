package pact.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;

public class RequestConvertingProxy extends ReverseProxy {

	public RequestConvertingProxy(String hostName, int port, String backServerURL) {
		super(hostName, port, backServerURL);
	}

	@Override
	protected String changeResponse(String response) {
		// This simple reverse proxy does not alter the response
		return response;
	}

	@Override
	protected String changeRequest(InputStream inputStream) throws IOException {
		// This simple reverse proxy does not alter the response
		// It just converts the stream to a string
		String input = IOUtils.toString(inputStream);
		return input;
	}
}
