package pact.utils;

import org.json.JSONObject;
import org.json.XML;

public class JSONConverter {

	public static String jsonToXml(String json) {
		JSONObject jsonObject = new JSONObject(json);
		String xml = XML.toString(jsonObject);
		return xml;
	}

	public static String xmlToJSON(String xml) {
		return XML.toJSONObject(xml).toString();
	}

}
