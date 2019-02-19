package com.ccby.yobee.utils;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * PATCH方法提交数据
 * 
 * @author zhaohongda
 */
public class PatchMethod extends PostMethod {

	public PatchMethod() {
	}

	public PatchMethod(String uri) {
		super(uri);
	}

	/**
	 * @see org.apache.commons.httpclient.HttpMethodBase#getName()
	 */
	@Override
	public String getName() {
		return "PATCH";
	}
}
