import java.io.*;
import java.net.HttpURLConnection;

import json.Elements;
import json.Geocoding;
import json.GoogleMaps;
import json.Results;
import json.Rows;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DistanceMatrix {
	
	public static void main(String[] args) {
        
		final String FILE_NAME_IN = args[0];
        final String FILE_NAME_OUT = args[1];
        final String bi = args[2];
        final String API_KEY = args[3]; //API Key from Google Maps API

        ArrayList<String> addressList = new ArrayList<String>();
        String addressFrom = null;
        String addressTo = null;
        String coordinateFrom = null;
        String coordinateTo = null;
        String distance = null;
        String value = null;
        String language = "zh-CN";
        boolean success;
        PrintWriter writer;


        try {
        	File file_in = new File(FILE_NAME_IN);
            writer = new PrintWriter(FILE_NAME_OUT, "UTF-8");

            String line = null;
		    try{
	            BufferedReader addresses = new BufferedReader(new InputStreamReader(new FileInputStream(file_in	),"UTF-8")); 		    	
		        while ((line = addresses.readLine()) != null) {
		           addressList.add(line);
		        }  
		        addresses.close();
		    } catch (Exception e) {
		    	  e.printStackTrace();
		      }

            int total = 0;
            for (int i = 0; i < addressList.size(); i++) {
        		
            	if (addressList.get(i).equals("") || addressList.get(i).trim().isEmpty()) {
                    System.out.println("Address_From is empty.");
                    writer.println("Address_From is empty.");
                    break;
                } else {

                	for (int j = i+1; j < addressList.size(); j++) {

                		if (addressList.get(j).equals("") || addressList.get(j).trim().isEmpty()) {
                			System.out.println("Address_To is empty.");
                			writer.println("Address_To is empty.");
                			break;
                		} else {
                			
                			if(bi.equals("2")) {
                				System.out.println("------------------------" + (total+1) + " & " + (total+2) + "------------------------");
                			} else {
                				System.out.println("------------------------" + (total+1) + "------------------------");
                			}
                			
                			String origin = URLEncoder.encode(addressList.get(i), "UTF-8");
                			String destination = URLEncoder.encode(addressList.get(j), "UTF-8"); 
                					                			
                			/* Geocode From */
                			String urlFrom = "https://ditu.google.com/maps/api/geocode/json?address=" +
													origin + "&language=" + language + "&key=" + API_KEY;
                			addressFrom = DistanceMatrix.getData(urlFrom, addressFrom);
                			success = getOK(addressFrom);
                            if (success) {
                                coordinateFrom = DistanceMatrix.getCoordinate(addressFrom);
                                System.out.println("A: " + addressList.get(i) + " ( " + coordinateFrom + " )");
                            } else {
                                System.out.println(addressList.get(i) + "'s coordinate cannot be found.");
                                writer.println("(" + i + ") " + addressList.get(i) + "'s coordinate cannot be found.");
                                writer.println("TIP: Manually check whether the address of " + addressList.get(i) + 
                                						" is valid on 百度地图 or 谷歌地图.");
                                break;
                            }
                			
                			/* Geocode To */
                			String urlTo = "https://ditu.google.com/maps/api/geocode/json?address=" +
                									destination + "&language=" + language + "&key=" + API_KEY;
                			addressTo = DistanceMatrix.getData(urlTo, addressTo);
                			success = getOK(addressTo);
                            if (success) {
                                coordinateTo = DistanceMatrix.getCoordinate(addressTo);
                                System.out.println("B: " + addressList.get(j) + " ( " + coordinateTo + " )");
                            } else {
                                System.out.println(addressList.get(j) + "'s coordinate cannot be found.");
                                writer.println("(" + j + ") " + addressList.get(j) + "'s coordinate cannot be found.");
                                writer.println("TIP: Manually check the address of " + addressList.get(j) + 
                                						" is valid on 百度地图 or 谷歌地图.");
                                break;
                            }
                            
                            if ( coordinateFrom != null && !coordinateFrom.equals("") && coordinateTo != null && !coordinateTo.equals("")) {

                            	/* Distance From / To */
	                			/* 
	                			 * url based on addresses
	                			 * String urlDistance = "https://ditu.google.com/maps/api/distancematrix/json?origins=" + 
	                										addressList.get(i) + "&destinations=" + addressList.get(j) + 
	                											"&language=" + language + "&key=" + API_KEY; */	                			
	                			/*
	                			 * usl based on coordinates
	                			 */
                            	String urlDistanceFromTo = "https://ditu.google.com/maps/api/distancematrix/json?origins=" + 
	                        						 				coordinateFrom + "&destinations=" + coordinateTo + 
	                        						 						"&language=" + language + "&key=" + API_KEY;
	                			distance = DistanceMatrix.getData(urlDistanceFromTo, distance);
	                            success = getSuccess(distance);
	                            if (success) {
	                                value = DistanceMatrix.getValue(distance);
	                                total++;
	                                System.out.println("A --> B " + "\n" + "距离,     时间" + "\n" + value + "\n");
	                            } else {
	                                System.out.println("Distance between " + addressList.get(i) + " and " 
	                                						+ addressList.get(j) + " cannot be determined.");
	                                writer.println("Distance between " + "(" + i + ") " + addressList.get(i) + " and " 
	                                						+ "(" + j + ") " + addressList.get(j) + " cannot be determined.");
	                                writer.println("TIP: Manually check whether the distance between " + addressList.get(i) + " and " + 
	                                						addressList.get(j) + " can be found on 百度地图或谷歌地图.");
	                                break;
	                            }
	                            
	                            if ( value != null && !value.equals("")) {
	                                writer.println(addressList.get(i) + ",                    " + coordinateFrom + 
	                                						",                    " + addressList.get(j) + ",                    " 
	                                								+ coordinateTo + ",                    " + value);
	                            }
	                            
	                            if (bi.equals("2")) {
	                            	/* Distance To / From */
		                			/* 
		                			 * url based on addresses
		                			 * String urlDistance = "https://ditu.google.com/maps/api/distancematrix/json?origins=" + 
		                										addressList.get(i) + "&destinations=" + addressList.get(j) + 
		                											"&language=" + language + "&key=" + API_KEY; */	                			
		                			/*
		                			 * usl based on coordinates
		                			 */
	                            	String urlDistanceToFrom = "https://ditu.google.com/maps/api/distancematrix/json?origins=" + 
		                        						 				coordinateTo + "&destinations=" + coordinateFrom + 
		                        						 					"&language=" + language + "&key=" + API_KEY;
		                			distance = DistanceMatrix.getData(urlDistanceToFrom, distance);
		                            success = getSuccess(distance);
		                            if (success) {
		                                value = DistanceMatrix.getValue(distance);
		                                total++;
		                                System.out.println("B --> A " + "\n" + "距离,     时间" + "\n" + value + "\n");
		                            } else {
		                                System.out.println("Distance between " + addressList.get(j) + " and " 
		                                						+ addressList.get(i) + " cannot be determined.");
		                                writer.println("Distance between " + "(" + j + ") " + addressList.get(j) + " and " 
		                                						+ "(" + i + ") " + addressList.get(i) + " cannot be determined.");
		                                writer.println("TIP: Manually check whether the distance between " + addressList.get(j) + " and " + 
		                                						addressList.get(i) + " can be found on 百度地图或谷歌地图.");
		                                break;
		                            }
		                            
		                            if ( value != null && !value.equals("")) {
		                                writer.println(addressList.get(j) + ",                    " + coordinateTo + 
                        						",                    " + addressList.get(i) + ",                    " 
                        								+ coordinateFrom + ",                    " + value);
		                            }
	                            }
                            }
                		}
                	}
                }
            }
            writer.close();

        } catch(FileNotFoundException e) {
        	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
        }
    }
    
	/* Get latitude and longitude */
    public static boolean getOK(String data) {

    	Gson gson = new Gson();
    	Geocoding geoCoding = gson.fromJson(data, Geocoding.class);

        return geoCoding.getStatus().equals("OK");
    }

    public static String getCoordinate(String data) {

        Gson gson = new Gson();
        Geocoding geoCoding = gson.fromJson(data, Geocoding.class);

        Results[] results = geoCoding.getResults();
        String latitude = results[0].getGeometry().getLocation().getLat();
        String longitude = results[0].getGeometry().getLocation().getLng();
        
        return latitude + "," + longitude;
    }
    
    /* Get distance and duration */
    public static boolean getSuccess(String data) {

        Gson gson = new Gson();
        GoogleMaps googleMaps = gson.fromJson(data, GoogleMaps.class);

        Rows[] rows = googleMaps.getRows();
        Elements[] elements = rows[0].getElements();
        return elements[0].getStatus().equals("OK");
    }
    
    public static String getValue(String data) {

        Gson gson = new Gson();
        GoogleMaps googleMaps = gson.fromJson(data, GoogleMaps.class);

        Rows[] rows = googleMaps.getRows();
        Elements[] elements = rows[0].getElements();

        String distanceText = elements[0].getDistance().getText();
        distanceText = distanceText.replace(" km", "");
        distanceText = distanceText.replace(" 公里", "");
        distanceText = distanceText.replace(",", "");
        
        String durationText = elements[0].getDuration().getText();
        
        if (durationText.endsWith("hours") || (durationText.endsWith("小时"))) {
        	durationText = durationText.concat(":00:00");
        } else if (!durationText.contains("小时")) {
        	durationText = "0:".concat(durationText);	
        } 

        durationText = durationText.replace(" hours ", ":");
        durationText = durationText.replace(" hours", "");
      	durationText = durationText.replace(" mins", "");
      	durationText = durationText.replace(" 小时 ", ":");
      	durationText = durationText.replace("小时", "");
      	durationText = durationText.replace(" 分钟", "");
      	durationText = durationText.replace("分钟", "");
      	durationText = durationText.concat(":00");

        return distanceText + ",     " + durationText;
    }
    
    /* Get JSON data */
    public static String getData(String url, String data){
    	
		HttpURLConnection c = null;
		try {

			URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type", "text; charset=UTF-8");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

			if ( status >= 400) {
				System.out.println(status);
			    System.out.println(c.getInputStream());
			} else {
			    //System.out.println("Error: " + c.getErrorStream());
			}

            switch (status) {
            	
            	case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    data = String.valueOf(sb);
            }
		} catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		
		return data;
    }
}