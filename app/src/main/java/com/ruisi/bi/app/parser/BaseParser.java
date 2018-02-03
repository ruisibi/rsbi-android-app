package com.ruisi.bi.app.parser;

import org.json.JSONException;

public abstract class BaseParser {
	public abstract <T> T parse(String jsonStr) throws JSONException;
}
