/**
 * 
 */
package com.ashpro.alertcall;

// import java.util.ArrayList;

/**
 * @author ash
 *
 */
public class Contact {
	public String name;
	public Long number;
	
	
	public Contact()
	{
		
	}
	public Contact(String name , Long number)
	{
		this.name = name;
		this.number = number;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the numbers
	 */
	public Long getNumber() {
		return number;
	}
	/**
	 * @param numbers the numbers to set
	 */
	public void setNumbers(Long number) {
		this.number = number;
	}

}
