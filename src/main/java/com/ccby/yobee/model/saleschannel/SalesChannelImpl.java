package com.ccby.yobee.model.saleschannel;

import javax.persistence.Column;

import com.ccby.module.base.BaseObjectImpl;

/**
 * 
 * 
 * @author
 *
 */
public class SalesChannelImpl extends BaseObjectImpl implements SalesChannel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3654557769831252202L;

	/**
	 * 支持的销售渠道名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 支持的销售渠道代码
	 */
	@Column(name = "code")
	private String code;

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
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	public long getIcId() {
		return icId;
	}

	public void setIcId(long icId) {
		this.icId = icId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}