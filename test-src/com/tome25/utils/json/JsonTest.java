package com.tome25.utils.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

public class JsonTest {

	@Test
	public void fastParsingTest() throws ParseException {
		// test whether the basic most things work
		String jsonString = "{\"testString\":\"parsing test\",\"testInt\":123}";
		JsonElement json = new JsonObject("testString", "parsing test");
		json.put("testInt", 123);
		assertEquals(jsonString, json.toString());
		// test whether the fast parser works
		JsonElement parsedJson = JsonParser.parseStringFast(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with double quotes at the end as that was a problem in
		// the past
		json.add("quoteTest", "double quotes:\"");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with a backslash at the end as that was a problem in the
		// past
		json.add("backslashTest", "backslash:\\");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
	}

	@Test
	public void parsingTest() throws ParseException, CloneNotSupportedException {
		// test whether the basic most things work
		String jsonString = "{\"testString\":\"Just a simple Test\",\"testInt\":51223,\"testJson\":{\"simple\":\"json\"}}";
		JsonElement simpleJson = new JsonObject("simple", "json");
		JsonElement json = new JsonObject("testString", "Just a simple Test");
		json.add("testInt", 51223);
		json.add("testJson", simpleJson);
		assertEquals(jsonString, json.toString());
		// test the parser with some basic things
		JsonElement parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test other numeric values
		json.put("testDouble", 123.45);
		json.put("testLong", Integer.MAX_VALUE * 2l);
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test some special characters
		json.add("characterTest", "^°!\"§$%&/()=?`´@ł€¶ŧ←↓→øþ+*~#'’<>|,;·.:…-_–");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with double quotes at the end as that was a problem in
		// the past
		json.add("quoteTest", "double quotes:\"");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with a backslash at the end as that was a problem in the
		// past
		json.add("backslashTest", "backslash:\\");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test null handling of json objects
		json.add("nullTest", null);
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test basic json array functions
		String jsonArrayString = "[1,\"test\",531.12,{\"simple\":\"json\"}]";
		JsonElement jsonArray = new JsonArray(1, "test", 531.12, simpleJson);
		assertEquals(jsonArrayString, jsonArray.toString());
		// test the parser with arrays
		JsonElement parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
		// test json array order
		assertEquals(jsonArray.get(2), parsedJsonArray.get(2));
		// test json array null handling
		((JsonArray) jsonArray).add(null);
		jsonArrayString = jsonArray.toString();
		parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
		// test json array subjson parsing
		assertEquals(simpleJson, parsedJsonArray.get(3));
		// test parsing json objects containing arrays
		json.add("array", jsonArray.clone());
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test the parsed array inside the json object
		assertEquals(parsedJson.get("array"), jsonArray);
		// test parsing arrays with complex jsons inside
		((JsonArray) jsonArray).add(json);
		jsonArrayString = jsonArray.toString();
		parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
	}

	@Test
	public void cloningTest() throws CloneNotSupportedException {
		// test the basics of the json object clone function
		JsonElement simpleJson = new JsonObject("simple", "json");
		JsonElement json = new JsonObject("testString", "test");
		json.add("testJson", simpleJson);
		JsonElement clonedJson = json.clone();
		assertEquals(json, clonedJson);
		// test that the clone isn't the same instance as the original
		assertFalse(json == clonedJson);
		// test that the subjsons get cloned too
		assertFalse(simpleJson == clonedJson.get("testJson"));
		assertEquals(simpleJson, clonedJson.get("testJson"));
		// test cloning not recursively
		clonedJson = json.clone(false);
		assertTrue(simpleJson == clonedJson.get("testJson"));
		// test the basics of the json array clone function
		JsonElement jsonArray = new JsonArray(1, "test", 531.12, simpleJson);
		JsonElement clonedJsonArray = jsonArray.clone();
		assertEquals(jsonArray, clonedJsonArray);
		// test that the clone isn't the same instance as the original
		assertFalse(jsonArray == clonedJsonArray);
		// test that the subjsons get cloned too
		assertFalse(simpleJson == clonedJsonArray.get(3));
		assertEquals(simpleJson, clonedJsonArray.get(3));
		// test cloning not recursively
		clonedJsonArray = jsonArray.clone(false);
		assertTrue(simpleJson == clonedJsonArray.get(3));
	}

	@Test
	public void deduplcationTest() throws CloneNotSupportedException {
		// test the basic deduplication functionality
		JsonElement json1 = new JsonObject("stringTest", "String Test");
		json1.add("longTest", Integer.MAX_VALUE * 5l);
		json1.add("doubleTest", 654.321);
		JsonElement json2 = json1.clone();
		((JsonObject) json2).remove("doubleTest");
		json2.add("intTest", 468);
		JsonElement deduplicatedJson = new JsonObject("doubleTest", null);
		deduplicatedJson.add("intTest", 468);
		assertEquals(deduplicatedJson, json2.changes(json1));
		// test reconstruction
		JsonElement reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test deduplication of subjsons
		json1.add("jsonTest", json2);
		json2 = json1.clone();
		((JsonObject) json2.get("jsonTest")).put("stringTest", "Another Test String");
		deduplicatedJson = new JsonObject("jsonTest", new JsonObject("stringTest", "Another Test String"));
		assertEquals(deduplicatedJson, json2.changes(json1));
		// test reconstruction of subjsons
		reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test cloning of subjsons with recursive deduplication disabled
		deduplicatedJson = json2.changes(json1, false);
		assertFalse(json2.get("jsonTest") == deduplicatedJson.get("jsonTest"));
		assertEquals(json2.get("jsonTest"), deduplicatedJson.get("jsonTest"));
		// test not recursive reconstruction
		reconstructedJson = deduplicatedJson.reconstruct(json1, false);
		assertEquals(json2, reconstructedJson);
		// test deduplication and reconstruction of a json containing a json array
		JsonElement jsonArray = new JsonArray("array", 123);
		json1.add("arrayTest", jsonArray);
		jsonArray = jsonArray.clone();
		((JsonArray) jsonArray).addAll(Integer.MAX_VALUE * 2l, "test");
		json2.add("arrayTest", jsonArray);
		deduplicatedJson = json2.changes(json1);
		reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
	}

}
