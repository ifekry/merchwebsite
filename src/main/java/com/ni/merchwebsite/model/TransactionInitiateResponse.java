package com.ni.merchwebsite.model;

public class TransactionInitiateResponse {
	
	private String respCode;
	private String respDescription;
	private String transactionUniqueId;
	private String transactionKey;
	
	public String getTransactionUniqueId() {
		return transactionUniqueId;
	}
	public void setTransactionUniqueId(String transactionUniqueId) {
		this.transactionUniqueId = transactionUniqueId;
	}
	public String getTransactionKey() {
		return transactionKey;
	}
	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDescription() {
		return respDescription;
	}
	public void setRespDescription(String respDescription) {
		this.respDescription = respDescription;
	}

}
