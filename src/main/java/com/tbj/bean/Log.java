package com.tbj.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class Log implements Serializable {

	private static final long serialVersionUID = 8817488339308711380L;

	private BigDecimal money;
	private String companyId;
	private String status;

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Log [money=" + money + ", companyId=" + companyId + ", status="
				+ status + "]";
	}
	
	

}
