package com.ccby.yobee.model.requireaccountfields;

import javax.persistence.Column;

import com.ccby.module.base.BaseObjectImpl;

/**
 * 
 * 
 * @author
 *
 */
public class RequireAccountFieldsImpl extends BaseObjectImpl implements RequireAccountFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3654557769831252202L;

	/**
	 * 字段名
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 字段文本
	 */
	@Column(name = "text")
	private String text;

	/**
	 * 字段类型
	 */
	@Column(name = "type")
	private String type;
	
	/**
	 * 字段类型
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 是否必选
	 */
	@Column(name = "required")
	private Integer required;

	/**
	 * 是否为登录所需字段
	 */
	@Column(name = "login_required")
	private Integer loginRequired;
	
	/**
	 * 是否允许非ASCII字符
	 */
	@Column(name = "allow_non_ascii")
	private Integer allowNonAscii;

	/**
	 * 排序号（升序）
	 */
	@Column(name = "order")
	private Integer order;

	/**
	 * 保险公司id
	 */
	@Column(name = "ic_id")
	private long icId;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Integer getRequired() {
		return required;
	}

	@Override
	public void setRequired(Integer required) {
		this.required = required;
	}

	@Override
	public Integer getLoginRequired() {
		return loginRequired;
	}

	@Override
	public void setLoginRequired(Integer loginRequired) {
		this.loginRequired = loginRequired;
	}

	public Integer getAllowNonAscii() {
		return allowNonAscii;
	}

	public void setAllowNonAscii(Integer allowNonAscii) {
		this.allowNonAscii = allowNonAscii;
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
	public long getIcId() {
		return icId;
	}

	@Override
	public void setIcId(long icId) {
		this.icId = icId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}