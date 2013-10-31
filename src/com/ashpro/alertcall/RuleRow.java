package com.ashpro.alertcall;

public class RuleRow {
	
	long id;
	public Rule rule;
	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}
	/**
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	public String toString()
	{
		return rule.toString();
	}

}
