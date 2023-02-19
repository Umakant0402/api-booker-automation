package testscripts;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import constants.StatusCode;

import org.testng.annotations.BeforeMethod;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.request.createbooking.BookingDates;
import pojo.request.createbooking.CreateBookingRequest;

public class CreateBookingTest {
	String token;
	int bookingId;
	CreateBookingRequest payload;
	@BeforeMethod
	public void generateToken() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com";

		Response res = RestAssured.given()
				// .log().all()
				.headers("Content-Type", "application/json")
				.body("{\r\n" 
						+ "    \"username\" : \"admin\",\r\n"
						+ "    \"password\" : \"password123\"\r\n" 
						+ "}")
				.when()
				.post("/auth")
				// .then().assertThat().statusCode(200)
				// .extract()
				// .response()
				;

		// System.out.println(res.statusCode());
		Assert.assertEquals(res.statusCode(), StatusCode.OK);
		// System.out.println(res.asPrettyString());
		token = res.jsonPath().getString("token");
		// System.out.println(token);
	}

	@Test(enabled=false)
	public void createBookingTest() {
		Response res = RestAssured.given()
				//.log().all()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.body("{\r\n" 
				+ "\"firstname\" : \"Alivia\",\r\n"
						+ "    \"lastname\" : \"Gaylord\",\r\n"
						+ "    \"totalprice\" : 111,\r\n" 
						+ "    \"depositpaid\" : true,\r\n"
						+ "    \"bookingdates\" : {\r\n" 
						+ "        \"checkin\" : \"2023-05-02\",\r\n"
						+ "        \"checkout\" : \"2023-05-04\"\r\n" 
						+ "    },\r\n"
						+ "    \"additionalneeds\" : \"Breakfast\"\r\n"
						+ "}")
				.when()
				.post("/booking");

		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
	}
	
	@Test
	public void createBookingTestWithPOJO() {
		
		BookingDates bookingDates = new BookingDates();
		bookingDates.setCheckin("2023-05-02");
		bookingDates.setCheckout("2023-05-05");
		
		payload = new CreateBookingRequest();
		payload.setFirstname("Booking");
		payload.setLastname("Test");
		payload.setTotalprice(111);
		payload.setDepositpaid(true);
		payload.setBookingdates(bookingDates);
		payload.setAdditionalneeds("breakfast");
		
		Response res = RestAssured.given()
				.headers("Content-Type", "application/json")
				.headers("Accept", "application/json")
				.body(payload)
				//.log().all()
				.when()
				.post("/booking");

		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		bookingId = res.jsonPath().getInt("bookingid");
		Assert.assertTrue(bookingId>0);
		validateResponse(res,"booking.");
	}
	
	@Test(priority=1)
	public  void getAllBookingTest() {
		Response res = RestAssured.given()
				.headers("Accept", "application/json")
				//.log().all()
				.when()
				.get("/booking");
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		//System.out.println(res.asPrettyString());
		List<Integer> listOfBookingIds = res.jsonPath().getList("bookingid");
		Assert.assertTrue(listOfBookingIds.size()>0);
		Assert.assertTrue(listOfBookingIds.contains(bookingId));
	}
	
	@Test(enabled=false)
	public  void getBookingIdTest() {
		Response res = RestAssured.given()
				.headers("Accept", "application/json")
				//.log().all()
				.when()
				.get("/booking/"+bookingId);
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		validateResponse(res,"");
	}
	
	@Test(priority=2)
	public  void getBookingIdDeserializeTest() {
		//bookingId = 2522;
		Response res = RestAssured.given()
				.headers("Accept", "application/json")
				//.log().all()
				.when()
				.get("/booking/"+bookingId);
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		
		Assert.assertTrue(payload.equals(responseBody));
	}
	
	@Test(priority=3)
	public  void updateBookingIdTest() {
		//bookingId = 2522;
		payload.setFirstname("Update");
		Response res = RestAssured.given()
				.headers("Accept", "application/json")
				.headers("Content-Type", "application/json")
				.headers("Cookie", "token="+token)
				.body(payload)
				//.log().all()
				.when()
				.put("/booking/"+bookingId);
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		
		Assert.assertTrue(payload.equals(responseBody));
	}
	
	@Test(priority=4)
	public  void partialUpdateBookingIdTest() {
		payload.setFirstname("Partial");
		payload.setLastname("Update");
		String strPayload = "{\r\n" 
				+ "    \"firstname\" : \""+payload.getFirstname()+"\",\r\n" 
				+ "    \"lastname\" : \""+payload.getLastname()+"\"\r\n" 
				+ "}";
		Response res = RestAssured.given()
				.headers("Accept", "application/json")
				.headers("Content-Type", "application/json")
				.headers("Cookie", "token="+token)
				.body(strPayload)
				//.log().all()
				.when()
				.patch("/booking/"+bookingId);
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		CreateBookingRequest responseBody = res.as(CreateBookingRequest.class);
		
		Assert.assertTrue(payload.equals(responseBody));
	}
	
	@Test(priority=5)
	public  void deleteBookingIdTest() {
		Response res = RestAssured.given()
				.headers("Content-Type", "application/json")
				.headers("Cookie", "token="+token)
				//.log().all()
				.when()
				.delete("/booking/"+bookingId);
		Assert.assertEquals(res.getStatusCode(), StatusCode.Created);
		
		res = RestAssured.given()
				.headers("Accept", "application/json")
				.when()
				.get("/booking");
		Assert.assertEquals(res.getStatusCode(), StatusCode.OK);
		List<Integer> listOfBookingIds = res.jsonPath().getList("bookingid");
		Assert.assertFalse(listOfBookingIds.contains(bookingId));
	}
	
	

	@Test(enabled=false)
	public void createBookingTestInPlanMode() {

		String payload = "{\r\n" 
				+ "    \"username\" : \"admin\",\r\n" 
				+ "    \"password\" : \"password123\"\r\n" 
				+ "}";

		RequestSpecification reqSpec = RestAssured.given();
		reqSpec.baseUri("https://restful-booker.herokuapp.com");
		reqSpec.headers("Content-Type", "application/json");
		reqSpec.body(payload);
		Response res = reqSpec.post("/auth");

		Assert.assertEquals(res.statusCode(), StatusCode.OK);
		//System.out.println(res.asPrettyString());
	}
	
	private void validateResponse(Response res, String Object) {
		Assert.assertEquals(res.jsonPath().getString(Object + "firstname"), payload.getFirstname());
		Assert.assertEquals(res.jsonPath().getString(Object + "lastname"), payload.getLastname());
		Assert.assertEquals(res.jsonPath().getInt(Object + "totalprice"), payload.getTotalprice());
		Assert.assertEquals(res.jsonPath().getBoolean(Object + "depositpaid"), payload.isDepositpaid());
		Assert.assertEquals(res.jsonPath().getString(Object + "additionalneeds"), payload.getAdditionalneeds());
		Assert.assertEquals(res.jsonPath().getString(Object + "bookingdates.checkin"),
				payload.getBookingdates().getCheckin());
		Assert.assertEquals(res.jsonPath().getString(Object + "bookingdates.checkout"),
				payload.getBookingdates().getCheckout());
	}

}
