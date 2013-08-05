package com.ashpro.alertcall;

import java.util.Date;
/**
 * takes in Contact and a Message to alert the contact name with
 * @author ash
 *
 */
public class Rule {
	
	public Contact contactName;
	public String reminderMessage;
	public boolean recurrence;
	public Date startTime, endTime;
	
	public Rule()
	{
		recurrence = false;
	}
	
	public Rule(Contact name , String msg)
	{
		contactName = name;
		reminderMessage = msg;
		recurrence = false;
	}
	/**
	 * Constructor: takes contact name, phone number of contact and reminder message
	 * @param name contact name
	 * @param num phone number 
	 * @param msg reminder message
	 */
	public Rule(String name, Long num,  String msg)
	{
		contactName = new Contact(name, num);
		reminderMessage = msg;
		recurrence = false;
	}
	/**
	 * Constructor: takes contact name, phone number of contact and reminder message
	 * @param name contact name
	 * @param num phone number 
	 * @param msg reminder message
	 * @param rep 1 if rule recurs 
	 */
	public Rule(String name, Long num,  String msg, int rep)
	{
		contactName = new Contact(name, num);
		reminderMessage = msg;
		recurrence = (rep == 1)?true:false;
	}
	
	/**
	 * Constructor: takes contact name, phone number of contact and reminder message
	 * @param name contact name
	 * @param num phone number 
	 * @param msg reminder message
	 * @param rep true if rule recurs 
	 */
	public Rule(String name, Long num,  String msg, boolean rep)
	{
		contactName = new Contact(name, num);
		reminderMessage = msg;
		recurrence = rep;
	}
	/**
	 * converts a rule object to string 
	 */
	@Override 
	public String toString()
	{
		return "When " + contactName.getName() + " calls me, remind me " + reminderMessage;
	}
	/**
	 * Constructor: takes contact name, phone number of contact, reminder message and repeat flag parameter
	 * @param name contact name
	 * @param num phone number 
	 * @param msg reminder message
	 * @param b repeat true or false
	 */
	public Rule(String name, Long num,  String msg, Boolean b)
	{
		contactName = new Contact(name, num);
		reminderMessage = msg;
		recurrence = b;
	}
	
	public Rule(String name,  String msg)
	{
		Long a = Long.valueOf("3133388853");
		contactName = new Contact(name, a);
		reminderMessage = msg;
		recurrence = false;
	}	
}
