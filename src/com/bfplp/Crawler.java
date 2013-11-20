package com.bfplp;

/**
 * This class is used as an example of how to start Website scrapers
 */
public class Crawler {

	public static void main(String[] args){
		Website bizbuysellcom = new Website("bizbuysellcom");
		bizbuysellcom.start();
		Website bizquestcom = new Website("bizquestcom");
		bizquestcom.start();
	}
}
