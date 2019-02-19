package com.ccby.yobee.model.requireaccountfields;


/**
 * 
 * 
 * @author
 *
 */
public interface RequireAccountFields {

	public String getName();

	public void setName(String name);

	public String getText();

	public void setText(String text);

	public String getType();

	public void setType(String type);

	public String getDescription();

	public void setDescription(String description);

	public Integer getRequired();

	public void setRequired(Integer required);

	public Integer getLoginRequired();

	public void setLoginRequired(Integer loginRequired);

	public Integer getAllowNonAscii();

	public void setAllowNonAscii(Integer allowNonAscii);

	public Integer getOrder();

	public void setOrder(Integer order);

	public long getIcId();

	public void setIcId(long icId);

}