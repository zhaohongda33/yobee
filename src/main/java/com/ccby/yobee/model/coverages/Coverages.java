package com.ccby.yobee.model.coverages;


/**
 * 
 * 
 * @author
 *
 */
public interface Coverages {

	public String getType();

	public void setType(String type);

	public String getCode();

	public void setCode(String code);

	public String getName();

	public void setName(String name);

	public Integer getOrder();

	public void setOrder(Integer order);

	public String getDescription();

	public void setDescription(String description);

	public String getAmountType();

	public void setAmountType(String amountType);

	public Integer getDefaultAmount();

	public void setDefaultAmount(Integer defaultAmount);

	public Integer getIsSupportExempt();

	public void setIsSupportExempt(Integer isSupportExempt);

	public Integer getIsSupportNonPassenger();

	public void setIsSupportNonPassenger(Integer isSupportNonPassenger);

	public String getDependency();

	public void setDependency(String dependency);
}