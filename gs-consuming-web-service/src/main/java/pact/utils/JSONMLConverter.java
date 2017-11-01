package pact.utils;

import org.json.JSONML;
import org.json.JSONObject;

public class JSONMLConverter {

	public static String jsonToXml(String json) {
		JSONObject jsonObject = new JSONObject(json);
		String xml = JSONML.toString(jsonObject);
		return xml;
	}

	public static String xmlToJSON(String xml) {
		return JSONML.toJSONObject(xml).toString();
	}

}
