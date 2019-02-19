package com.ccby.yobee.model.coverages;

import javax.persistence.Column;

import com.ccby.module.base.BaseObjectImpl;

/**
 * 
 * 
 * @author
 *
 */
public class CoveragesImpl extends BaseObjectImpl implements Coverages {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3654557769831252202L;

	/**
	 * 分组代码
	 */
	@Column(name = "type")
	private String type;
	
	/**
	 * 险种代码
	 */
	@Column(name = "code")
	private String code;
	
	/**
	 * 险种名称
	 */
	@Column(name = "name")
	private String name;
	
	/**
	 * 排序号（升序）
	 */
	@Column(name = "order")
	private Integer order;
	
	/**
	 * 险种描述
	 */
	@Column(name = "description")
	private String description;
	
	/**
	 * 险种保额类型
	 */
	@Column(name = "amount_type")
	private String amountType;
	
	/**
	 * 推荐默认值
	 */
	@Column(name = "default_amount")
	private Integer defaultAmount;
	
	/**
	 * 是否支持不计免赔特约险
	 */
	@Column(name = "is_support_exempt")
	private Integer isSupportExempt;
	
	/**
	 * 是否支持非客车投保
	 */
	@Column(name = "is_support_non_passenger")
	private Integer isSupportNonPassenger;
	
	/**
	 * 依赖险种代码
	 */
	@Column(name = "dependency")
	private String dependency;

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	@Override
	public Integer getDefaultAmount() {
		return defaultAmount;
	}

	@Override
	public void setDefaultAmount(Integer defaultAmount) {
		this.defaultAmount = defaultAmount;
	}

	@Override
	public Integer getIsSupportExempt() {
		return isSupportExempt;
	}

	@Override
	public void setIsSupportExempt(Integer isSupportExempt) {
		this.isSupportExempt = isSupportExempt;
	}

	@Override
	public Integer getIsSupportNonPassenger() {
		return isSupportNonPassenger;
	}

	@Override
	public void setIsSupportNonPassenger(Integer isSupportNonPassenger) {
		this.isSupportNonPassenger = isSupportNonPassenger;
	}

	@Override
	public String getDependency() {
		return dependency;
	}

	@Override
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}