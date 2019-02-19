package com.ccby.yobee.model.vehicle;

import javax.persistence.Column;

import com.ccby.module.base.BaseObjectImpl;

/**
 * 
 * 
 * @author
 *
 */
public class VehicleImpl extends BaseObjectImpl implements Vehicle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3654557769831252202L;

	/**
	 * 车辆元数据类型
	 */
	@Column(name = "type")
	private String type;
	
	/**
	 * 代码
	 */
	@Column(name = "code")
	private String code;
	
	/**
	 * 显示文本
	 */
	@Column(name = "name")
	private String name;
	
	/**
	 * 排序号（升序）
	 */
	@Column(name = "order")
	private Integer order;
	
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}