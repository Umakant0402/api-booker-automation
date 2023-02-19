package testscripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Demo {
	@Test
	public void phoneNumbersTypeTest() {
		RestAssured.baseURI = "https://0e686aed-6e36-4047-bcb4-a2417455c2d7.mock.pstmn.io";
		
		Response res = RestAssured.given()
			.headers("Accept", "application/json")
			.when()
			.get("/test");
		
		//System.out.println(res.asPrettyString());
		List<String> listOfType = res.jsonPath().getList("phoneNumbers.type");
		List<String> expectedList = new ArrayList<String>();
		expectedList.add("iPhone");
		expectedList.add("home");
		Assert.assertEquals(expectedList, listOfType);
	}
	
	@Test
	public void phoneNumbersTest() {
		RestAssured.baseURI = "https://0e686aed-6e36-4047-bcb4-a2417455c2d7.mock.pstmn.io";
		
		Response res = RestAssured.given()
			.headers("Accept", "application/json")
			.when()
			.get("/test");
		
		//System.out.println(res.asPrettyString());
		List<Object> listOfPhoneNumber = res.jsonPath().getList("phoneNumbers");
		//System.out.println(listOfPhoneNumber);
		
		for(Object obj: listOfPhoneNumber) {
			Map<String,String> mapOfPhoneNumber = (Map<String,String>)obj;
			if(mapOfPhoneNumber.get("type").equals("iPhone")) 
				Assert.assertEquals("3456", mapOfPhoneNumber.get("number").substring(0, 4));
			else if(mapOfPhoneNumber.get("type").equals("home")) 
				Assert.assertEquals("0123", mapOfPhoneNumber.get("number").substring(0, 4));
		}
	}
}
