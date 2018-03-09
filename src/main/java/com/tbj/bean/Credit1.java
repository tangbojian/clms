package com.tbj.bean;

import java.math.BigDecimal;

public class Credit1 {

	private Integer id;
	private String companyId;
	private BigDecimal creditLine;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public BigDecimal getCreditLine() {
		return creditLine;
	}

	public void setCreditLine(BigDecimal creditLine) {
		this.creditLine = creditLine;
	}

}
