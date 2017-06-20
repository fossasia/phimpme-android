package org.wordpress.android;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

public class AddCatagory {
	private XMLRPCClient client;

	public void getCategories(String url,String username,String password){
    	
    	//gets the categories via xmlrpc call to wp blog   
        Object result[] = null;

        Object[] params = {
        		url,
        		username,
        		password,
        };

        client = new XMLRPCClient(url, username, password);

        boolean success = false;

        try {
        	result = (Object[]) client.call("wp.getCategories", params);
        	success = true;
        } catch (XMLRPCException e) {
        	e.printStackTrace();
        }

        if (success){
        	int size = result.length;
        	boolean check=false;
        	for(int i=0; i<size; i++)
        	{
        		HashMap<?, ?> curHash = (HashMap<?, ?>) result[i];
        		String categoryName = curHash.get("categoryName").toString();
        		//Log.e("Add Catagory","List catagory : "+categoryName);
        		if(categoryName.equals("phimpme mobile")){
        			//Log.e("Add Catagory","phimpme catagory exists !");
        			check=true;
        		}
        	}
        	if(check==false){
        		//Log.e("AddCatagory", "creata phimpme catagory");
        		addCategory(url, username, password);
        	}

        	
        }
        else{
        	//Log.e("Add Catagory", "get catagory fail !");
        }



    	
    }
	
	public void addCategory(String url, String username,String password) {
		
		//	Store the parameters for wp.addCategory
	    Map<String, Object> struct = new HashMap<String, Object>();
	    struct.put("name", "phimpme mobile");

	    client = new XMLRPCClient(url, username, password);
	    
	    Object[] params = {
	    		url,
	    		username,
	    		password,
	    		struct
	    };
	    
	    Object result = null;
	    try {
			result = client.call("wp.newCategory", params);
		} catch (XMLRPCException e) {
			e.printStackTrace();
		}
    	
		if (result == null) {
			//Log.e("AddCatagory","Add catagory fail !");
		}
		else {	
			//Log.e("AddCatagory","Add catagory success !");
			
		}
		
    	
    }

}
