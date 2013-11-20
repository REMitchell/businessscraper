package com.bfplp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Website objects take a string argument, sitename that will determine which website they will crawl. 
 * To crawl a new website, a method should be added of the form "domainnamecom" (for domainname.com) and a check
 * should be added to the start() method of the form: if(sitename=="domainanemcom){domainnamecom();}
 * A working internet connection is essential for running this. 
 * Errors may occur if the internet connection is broken at any time for more than several seconds
 * @author Ryan Mitchell
 *
 */

public class Website {
	public static String sitename;
	private static Connection dbConn;
	public static String[] 	states = {"alabama", "alaska", "arizona", "arkansas", "california", "colorado", "connecticut", "delaware", "district-of-columbia", "washington-dc", "florida", "georgia", "hawaii", "idaho", "illinois", "indiana", "iowa", "kansas", "kentucky", "louisiana", "maine", "maryland", "massachusetts", "michigan", "minnesota", "mississippi", "missouri", "montana", "nebraska", "nevada", "new-hampshire", "new-jersey", "new-mexico", "new-york", "north-carolina", "north-dakota", "ohio", "oklahoma", "oregon", "pennsylvania", "rhode-island", "south-carolina", "south-dakota", "tennessee", "texas", "utah", "vermont", "virginia", "washington", "west-virginia", "wisconsin", "wyoming"};
	public static String[] statesAbb = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "DC", "FL", "GA", "HI", "ID", "IL", "IN"                                                                              , "IA", "KS", "KY", "LA",                                          "ME", "MD", "MA", "MI",                                                    "MN", "MS", "MO", "MT", "NE",                                                 "NV", "NH", "NJ", "NM", "NY", "NC",    "ND", "OH","OK","OR","PA","RI","SC","SD" ,                                          "TN", "TX", "UT", "VE","VA", "WA", "WV", "WI", "WY"  };

	/**
	 * Constructor method, sets sitename to the provided string
	 * @param websiteName
	 */
	Website(String websiteName){
		sitename = websiteName;
	}
	
	/**
	 * This method is called to actually begin crawling
	 * It first creates a new connection to the database, then calls a specific crawling method based on the sitename given
	 */
	public static void start() {
		
		//Creates a connection to the database "brookwood," at localhost, username: root, empty password
		String dbURL = "jdbc:mysql://localhost:3306/brookwood";
		Properties connectionProps = new Properties();
		connectionProps.put("user", "root");
		connectionProps.put("password", "");
		dbConn = null;
		try {
			dbConn = DriverManager.getConnection(dbURL, connectionProps);
		} catch (SQLException e) {
			System.out.println("There was a problem connecting to the database");
			e.printStackTrace();
		}

		PreparedStatement useStmt;
		try {
			useStmt = dbConn.prepareStatement("USE brookwood");
			useStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//As new methods are added, add new "if" statements here
		if(sitename == "bizbuysellcom"){
			bizbuysellcom();
		}
		if(sitename == "bizquestcom"){
			bizquestcom();
		}
	}
	
	/**
	 * Crawler for the website bizquest.com 
	 * First enters http://www.bizquest.com/buy-a-business-for-sale/ and paginates through all subsequent pages 
	 * Writes all business listings found to the database. Terminates when a page is reached that contains no business listings
	 */
	public static void bizquestcom(){
		String url = "";
		Document doc;
		String title = "";
		String bizUrl = "";
		String id = "";
		String askingPrice = "";
		String grossRevenue = "";
		String cashFlow = "";
		String inventory = "";
		String inventoryIncluded = "no";
		String realEstate = "";
		String realEstateIncluded = "no";
		String ffande = "";
		String ffeIncluded = "no";
		String listedByName = "";
		String listedByBiz = "";
		String bizDescription = "";
		
		Document bizDoc;
		
		//i is the current page number
		int i = 1;
		//Continues to go through URLs until it reaches a page where no listings are found, then breaks
		while(true){
			url = "http://www.bizquest.com/buy-a-business-for-sale/page-"+i+"/?q=bz0x";
			if(i == 1){
				//Removes the "/page-1/" on the first page. It is not needed
				url = "http://www.bizquest.com/buy-a-business-for-sale/?q=bz0x";
			}
			doc = getUrl(url);
			
			//Checks to see if the getUrl method failed to find a page. Will try the next page, if so
			if(doc.select(".emptyPage").text() == "Page not found"){
				System.out.println("page was not found");
			}else{
				//Website was found. Collect records off of it
			if(doc.select(".ColLeft").html().contains("Sorry,")){
				//This page does not contain any records. We have reached the end of the business listings
				System.out.println("No more records");
				break;
			}
			
			Elements listings = doc.select(".list");
			for(int j = 0; j < listings.size(); j++){
				bizUrl = "http://www.bizquest.com"+listings.get(j).select(".col_645").select(".colLeft").select("h2").select("a").attr("href"); 
				System.out.println(bizUrl);
				bizDoc = getUrl(bizUrl);
				title = bizDoc.select("h1").html();
				title = title.replace("<span>"+bizDoc.select("h1").select("span").html()+"</span>", "");
				id = bizDoc.select("h1").select("span").html();
				id = id.replace("ID:&nbsp;", "").replace("(", "").replace(")", "");
				System.out.println(title);
				System.out.println(id);
				Elements tableRows = bizDoc.select(".details").select("table").select("tbody").select("tr");
				for(int k = 1; k < tableRows.size(); k++){
					String label = tableRows.get(k).select("td").first().text().trim();
					System.out.println("label is: "+label);
					if(label.contains("Asking Price:")){
						System.out.println("Asking price!");
						askingPrice = tableRows.get(k).select("td").get(1).text().trim();
					}
					if(label.contains("Gross Revenue:")){
						System.out.println("Gross revenue!");
						grossRevenue = tableRows.get(k).select("td").get(1).text().trim();
					}
					if(label.contains("Cash Flow:")){
						System.out.println("Cash flow!");
						cashFlow = tableRows.get(k).select("td").get(1).text().trim();
					}
					if(label.contains("Inventory:")){
						System.out.println("Inventory!");
						inventory = tableRows.get(k).select("td").get(1).text().trim();
						if(inventory.contains("**")){
							inventoryIncluded = "no";
							inventory = inventory.replace("**", "");
							System.out.println("Inventory is included");
						}
						if(realEstate.contains("++")){
							inventoryIncluded = "yes";
							inventory = inventory.replace("++", "");
							System.out.println("Inventory is not included");
						}
					}
					if(label.contains("Real Estate:")){
						realEstate = tableRows.get(k).select("td").get(1).text().trim();
						if(realEstate.contains("**")){
							//Not included in asking price
							realEstateIncluded = "no";
							realEstate = realEstate.replace("**", "");
							System.out.println("Real Estate is not included");
						}
						if(realEstate.contains("++")){
							//Included in asking price
							realEstateIncluded = "yes";
							realEstate = realEstate.replace("++", "");
							System.out.println("Real Estate is included");
						}
					}
					if(label == "FF&E:"){
						ffande = tableRows.get(k).select("td").get(1).text().trim();
						if(ffande.contains("**")){
							ffeIncluded = "no";
							ffande = ffande.replace("**", "");
							System.out.println("Inventory is included");
						}
						if(ffande.contains("++")){
							ffeIncluded = "yes";
							ffande = ffande.replace("++", "");
							System.out.println("Inventory is not included");
						}
					}
				}
				
				//Get broker information

				if(!bizDoc.select(".listed").isEmpty() && bizDoc.select(".listed").select("span").select("strong, em").size() > 2){
					System.out.println("Broker information exists");
					Elements listingLines = bizDoc.select(".listed").select("span").select("strong, em");
					if(bizDoc.select(".listed").select("span").text().contains("Contact Broker:")){
						System.out.println("Contains a 'contact broker'");
						//Contains weird 'contact broker' section.
						for(int l = 0; l < listingLines.size(); l++){
							System.out.println("Line "+i+" is "+listingLines.get(l));
							if(listingLines.get(l).text().contains("Contact Broker:")){
								listedByName = listingLines.get(l+1).text();
								listedByBiz = listingLines.get(l+2).text();
							}
						}
					}else{
						System.out.println("Listed by name should be "+listingLines.get(1));
						System.out.println("Listed by business should be "+listingLines.get(2));
						listedByName = listingLines.get(1).text();
						listedByBiz = listingLines.get(2).text();
					}
				}else{
					System.out.println("No broker information");
				}
				String state = "";
				String country = "USA";
				System.out.println("States array length is: "+states.length);

				if(bizDoc.select(".country a").size() > 1){
					state = bizDoc.select(".country a").get(1).text().replace(" ", "-").toLowerCase();
					System.out.println("State is: "+state);
					for (int stateIndex = 0; stateIndex < states.length; stateIndex++) {
						System.out.println("stateIndex is: "+stateIndex+" and state is "+states[stateIndex]);
						  if (states[stateIndex].equals(state)) {
						    state = statesAbb[stateIndex];
						    break;
						}
					}
					
					if(state.length() > 2){
						//This is not one of the known states in the United States. It is likely a country name, instead
						country = state;
						state = "";
					}
					
					System.out.println("State, country is: "+state+", "+country);
				}
				
				bizDescription = bizDoc.select(".listingDescription").html();
				
				System.out.println("bizUrl is: "+bizUrl);
				System.out.println("title is: "+title);
				System.out.println("id is "+id);
				System.out.println("askingPrice is "+askingPrice);
				System.out.println("grossRevenue: "+grossRevenue);
				System.out.println("cashFlow: "+cashFlow);
				System.out.println("inventory: "+inventory);
				System.out.println("realEstate: "+realEstate);
				System.out.println("Broker Name: "+listedByName);
				System.out.println("Broker: "+listedByBiz);
				//System.out.println("Description: "+bizDescription);
				writeToDB(title/*title*/, /*address*/ "", /*city*/ "", /*state*/ state, /*zip*/ 0, /*county*/ "", /*country*/ country, /*region*/ "", /*price*/ cleanPrice(askingPrice), /*revenue*/ cleanPrice(grossRevenue), /*cashflow*/ cleanPrice(cashFlow), /*ebitda*/ 0,/*realestatevalue*/ cleanPrice(realEstate), /*realestatevalueinvluded*/ realEstateIncluded, /*ffevalue*/ cleanPrice(ffande), /*ffeincluded*/ ffeIncluded, /*inventory*/ cleanPrice(inventory),/*inventoryincluded*/ inventoryIncluded, /*industry*/ "", /*description*/ bizDescription, /*sellerfinancingavailable*/ "", /*squarefeet*/ 0, /*numberemployees*/ "", /*bcompany*/listedByBiz, /*bname*/listedByName, /*bphone*/ "", /*baddress*/ "", /*bwebsite*/ "", /*url*/ bizUrl, /*linfo*/ "", /*lid*/ id);
			}//End "website was found"
			}
			i = i+1;
			System.out.println("Moving to page "+i);
		}
	}
	
	public static void bizbuysellcom(){
		String firstURL = "";
		Document bizPageDoc = null;
		for(Integer i = 0; i < states.length; i++){
			//Pagination loop goes through all 30 search pages
			if(states[i] != "washington-dc"){//"washington-dc" does not exist. They use district-of-columbia instead. Skip this
			for(Integer k = 1; k <= 30; k++){
				Document searchPageDoc = getUrl("http://www.bizbuysell.com/"+states[i]+"-businesses-for-sale/"+k+"/");
				if(searchPageDoc.select(".emptyPage").text() != "Page not found"){ //If page is not null
				Elements allURLs = searchPageDoc.select("[id^=ctl00_ctl00_Content_ContentPlaceHolder1_ListingRow]");
				if(k != 1){
					if(allURLs.get(0).attr("href") == firstURL){
						//They're showing the first URL again -- we must have passed the last page of results
						//Break out of the pagination loop, back into the States loop
						break;
					}
				}
				for(Integer j = 0; j < allURLs.size(); j++){
					if(k == 1 && j == 0){
						//This is the first URL on the page. Save it to see when we've reached the end of the listings
						firstURL = cleanURL(allURLs.get(0).attr("href"));
						System.out.println("First URL is: "+cleanURL(firstURL));
					}
					String pageURL = cleanURL(allURLs.get(j).attr("href"));
					bizPageDoc = getUrl(pageURL);
					if(bizPageDoc.select(".emptyPage").text() != "Page not found"){ //If page is not null

					
					//Collect the information from the page
					String title = bizPageDoc.select("h1").text();
					Elements financialElements = bizPageDoc.select("div.financials .span4 b");
					//Financial elements are: asking price, gross income, cash flow, EBITDA, FF&E, Inventory, Real Estate, Established, Employees
					
					System.out.println("Asking price is: "+financialElements.get(0).select("b").text());
					System.out.println("Gross Income: "+financialElements.get(1).select("b").text());
					System.out.println("Cash Flow: "+financialElements.get(2).select("b").text());
					System.out.println("EBITDA: "+financialElements.get(3).select("b").text());
					System.out.println("FF&E: "+financialElements.get(4).select("b").text());
					System.out.println("Inventory: "+financialElements.get(5).select("b").text());
					System.out.println("Real Estate: "+financialElements.get(6).select("b").text());
					System.out.println("Established: "+financialElements.get(7).select("b").text());
					System.out.println("Employees: "+financialElements.get(8).select("b").text());
					String idNum = bizPageDoc.select(".disclaimer").select("p").first().select("b").text().replace("Ad#:", "").trim();
					bizPageDoc.select("#listingActions").remove();
					bizPageDoc.select("#mainPhoto").remove();
					bizPageDoc.select(".financials").remove();
					bizPageDoc.select("script").remove();
					String description = bizPageDoc.select(".span8").html();
					System.out.println(description);
					writeToDB(/*title*/ title, /*address*/ "", /*city*/ "", /*state*/ statesAbb[i], /*zip*/ 0, /*county*/ "", /*country*/ "USA", /*region*/ "", /*price*/ cleanPrice(financialElements.get(0).select("b").text()), /*revenue*/ 0, /*cashflow*/ 0, /*ebitda*/ cleanPrice(financialElements.get(3).select("b").text()),/*realestatevalue*/ cleanPrice(financialElements.get(6).select("b").text()), /*realestatevalueincluded*/ "", /*ffevalue*/ cleanPrice(financialElements.get(4).select("b").text()), /*ffeincluded*/ "", /*inventory*/ cleanPrice(financialElements.get(5).select("b").text()),/*inventoryincluded*/ "", /*industry*/ "", /*description*/ description, /*sellerfinancingavailable*/ "", /*squarefeet*/ 0, /*numberemployees*/ financialElements.get(8).select("b").text(), /*bcompany*/ "", /*bname*/ "", /*bphone*/ "", /*baddress*/ "", /*bwebsite*/ "", /*url*/ pageURL, /*linfo*/ "", /*lid*/ idNum);
				}//End of business page is found
				}//End of search page is found
				}//End if state is not "washington-dc"
				}
			}
		}
	}
	
	public static Integer cleanPrice(String dirtyPrice){
		dirtyPrice = dirtyPrice.trim();
		dirtyPrice = dirtyPrice.replace("$", "");
		dirtyPrice = dirtyPrice.replace(",", "");
		try { 
	        Integer cleanPrice = Integer.parseInt(dirtyPrice);
	        return cleanPrice;
	    } catch(NumberFormatException e) { 
	    	//Not a number
	        return 0; 
	    }
	}
	public static String cleanURL(String dirtyURL){
		Integer questionmarkIndex = dirtyURL.indexOf("?");
		if (questionmarkIndex != -1)
		{
		    dirtyURL = dirtyURL.substring(0, questionmarkIndex);
		}
		return dirtyURL;
	}
	
	public static Document getUrl(String url){
		System.out.println("Connecting to "+url);
		
		Document searchPageDoc;
		try {
			searchPageDoc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").timeout(0).get();
			return searchPageDoc;
		} catch (IOException e) {
			System.out.println("Problem connecting to page "+url);
			return Jsoup.parse("<div class='emptyPage'>Page not found</div>");
			//e.printStackTrace();
		}
	}
	
	private static void writeToDB(String title, String address, String city, String state, Integer zip, String county, String country, String region, Integer price, Integer revenue, Integer cashflow, Integer ebitda, Integer realestatevalue, String realestateincluded, Integer ffevalue, String ffeincluded, Integer inventory, String inventoryincluded, String industry, String description, String sellerfinancingavailable, Integer squarefeet, String numberemployees, String bcompany, String bname, String bphone, String baddress, String bwebsite, String url, String linfo, String lid){
		PreparedStatement useStmt;
		try {
			useStmt = dbConn.prepareStatement("INSERT INTO data (title, address, city, state, zip, county, country, region, price, revenue, cashflow, ebitda, realestatevalue, realestateincluded, ffevalue, ffeincluded, inventory, inventoryincluded, industry, description, sellerfinancingavailable, squarefeet, numberemployees, bcompany, bname, bphone, baddress, bwebsite, url, linfo, lid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			useStmt.setString(1, title);
			useStmt.setString(2, address);
			useStmt.setString(3, city);
			useStmt.setString(4, state);
			useStmt.setInt(5, zip);
			useStmt.setString(6, county);
			useStmt.setString(7, country);
			useStmt.setString(8, region);
			useStmt.setInt(9, price);
			useStmt.setInt(10, revenue);
			useStmt.setInt(11, cashflow);
			useStmt.setInt(12, ebitda);
			useStmt.setInt(13, realestatevalue);
			useStmt.setString(14, realestateincluded);
			useStmt.setInt(15, ffevalue);
			useStmt.setString(16, ffeincluded);
			useStmt.setInt(17, inventory);
			useStmt.setString(18, inventoryincluded);
			useStmt.setString(19, industry);
			useStmt.setString(20, description);
			useStmt.setString(21, sellerfinancingavailable);
			useStmt.setInt(22, squarefeet);
			useStmt.setString(23, numberemployees);
			useStmt.setString(24, bcompany);
			useStmt.setString(25, bname);
			useStmt.setString(26, bphone);
			useStmt.setString(27, baddress);
			useStmt.setString(28, bwebsite);
			useStmt.setString(29, url);
			useStmt.setString(30, linfo);
			useStmt.setString(31, lid);
			useStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
