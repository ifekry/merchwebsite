package com.ni.merchwebsite.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.ni.merchwebsite.App;
import com.ni.merchwebsite.model.TransactionConfirmResponse;
import com.ni.merchwebsite.model.TransactionInitiateResponse;
import com.ni.merchwebsite.model.TransactionSubmitResponse;
import com.ni.merchwebsite.service.Cryptography;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@Controller
public class TransactionController {
	
	@Value("${paymentGatewayUrl}")
    private String paymentGatewayUrl;

	@Value("${merchId}")
    private String merchId;

	@Value("${merchSecretCode}")
    private String merchSecretCode;
		
	@Value("${merchKey}")
    private String merchEncKey;
	
	private static final Logger log = LoggerFactory.getLogger(App.class);
	
	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		/* Index */
		return "index";
	}
	
	@PostMapping("/payment")
	public String doPayment(Map<String, Object> model, @RequestParam(value="cardnumber") String cardNumber, @RequestParam(value="cvv") String cvv, @RequestParam(value="expirydate") String expiryDate, @RequestParam(value="amount") String amount) {
		log.info("Starting processing payment transaction");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		RestTemplate restTemplate = new RestTemplate();
		
		/* Initiate Transaction */
		log.debug("Initiating Transaction");
		try {
			MultiValueMap<String, String> mapInitParams= new LinkedMultiValueMap<String, String>();
			mapInitParams.add("merchantId", this.merchId);
			mapInitParams.add("merchantSecretCode", this.merchSecretCode);
			HttpEntity<MultiValueMap<String, String>> initRequest = new HttpEntity<MultiValueMap<String, String>>(mapInitParams, headers);
			String initUrl = this.paymentGatewayUrl + "/initiate/";
			ResponseEntity<TransactionInitiateResponse> initResp = restTemplate.postForEntity( initUrl, initRequest , TransactionInitiateResponse.class );
			TransactionInitiateResponse trxInitResp = initResp.getBody();		
			log.info("Transaction initiated [RC: " + trxInitResp.getRespCode() + " , Trx ID: " + trxInitResp.getTransactionUniqueId() + "]");
			if (!trxInitResp.getRespCode().equals("00")) {
				model.put("message", "Payment Failed - Unable to reach payment gateway");
				return "payment";
			}
			/* Submit Transaction */
			String merchKey = merchEncKey; 
			String transactionId = trxInitResp.getTransactionUniqueId();
			String encTrxKey = trxInitResp.getTransactionKey();
			String clrTrxKey = "";
			try {
				/* Decrypt Transaction Key */
				clrTrxKey = Cryptography.decrypt(Cryptography.GetKey(merchKey), encTrxKey);
			} catch (Exception e) {
				log.error("Unable to decrypt transaction key - Payment submission failed");
				model.put("message", "Payment Failed - Unable to process [Cryptographic Error]");
				return "payment";
			}
			if (clrTrxKey == null || clrTrxKey.equals("")) {
				log.warn("Unable to submit transaction - No transaction key");
				model.put("message", "Payment Failed - Unable to process [Cryptographic Error]");
				return "payment";				
			}
			/* Prepare Transaction Data */
			String submitUrl = this.paymentGatewayUrl + "/submit/";
			MultiValueMap<String, String> mapSubmitParams= new LinkedMultiValueMap<String, String>();
			try {
				mapSubmitParams.add("transactionId", transactionId);
				mapSubmitParams.add("merchantId", this.merchId);
				mapSubmitParams.add("cardNumber", Cryptography.encrypt(Cryptography.GetKey(clrTrxKey), cardNumber));
				mapSubmitParams.add("cvv2", Cryptography.encrypt(Cryptography.GetKey(clrTrxKey), cvv));
				mapSubmitParams.add("expiryDate", expiryDate);
				mapSubmitParams.add("transactionAmount", amount);				
			} catch (Exception e) {
				log.error("Error preparing transaction data [Error: " + e.getMessage() + "]");
				model.put("message", "Payment Failed - Unable to process - Invalid Transaction Data");
				return "payment";	
			}
			/* Submit Request */
			HttpEntity<MultiValueMap<String, String>> submitRequest = new HttpEntity<MultiValueMap<String, String>>(mapSubmitParams, headers);		
			ResponseEntity<TransactionSubmitResponse> submitResp = restTemplate.postForEntity( submitUrl, submitRequest , TransactionSubmitResponse.class );
			TransactionSubmitResponse trxSubmitResp = submitResp.getBody();
			String respCode = trxSubmitResp.getRespCode();
			if (respCode == null) {
				model.put("message", "Payment Failed - No response from payment gateway");
				return "payment";	
			}
			log.info("Transaction Processed [RC: " + respCode + "]");
			switch (respCode) {
				case "00": {
					model.put("message", "Payment Successful - Authorization Number: " + trxSubmitResp.getAuthNumber());
					break;
				}
				case "M1": {
					model.put("message", "Payment Failed - Invalid Merchant ID");
					break;
				}
				case "M2": {
					model.put("message", "Payment Failed - Invalid Merchant Secret Code");
					break;
				}
				case "X1": {
					model.put("message", "Payment Failed - Cryptographic Error");
					break;
				}
				case "X2": {
					model.put("message", "Payment Failed - Unable to initiate transaction");
					break;
				}
				case "C1": {
					model.put("message", "Payment Failed - Invalid Transaction ID");
					break;
				}
				case "C2": {
					model.put("message", "Payment Failed - Invalid Card Number");
					break;
				}
				case "C3": {
					model.put("message", "Payment Failed - Invalid Card Verification Value (CVV)");
					break;
				}
				case "C4": {
					model.put("message", "Payment Failed - Invalid Amount");
					break;
				}
				case "C5": {
					model.put("message", "Payment Failed - Transaction Already Submitted");
					break;
				}
				case "F1": {
					model.put("message", "Payment Failed - Failed at payment processor / No response received from payment processor");
					break;
				}
				case "C6": {
					model.put("message", "Payment Failed - Transaction not initiated by the same merchant");
					break;
				}
				case "C7": {
					model.put("message", "Payment Failed - Invalid Expiry Date");
					break;
				}
				case "C8": {
					model.put("message", "Payment Failed - Transaction not processed");
					break;
				}
			}
			
			/* Confirm Transaction */
			try {
				String confirmUrl = this.paymentGatewayUrl + "/confirm/";
				MultiValueMap<String, String> mapConfirmParams= new LinkedMultiValueMap<String, String>();
				mapConfirmParams.add("transactionId", transactionId);
				HttpEntity<MultiValueMap<String, String>> confirmRequest = new HttpEntity<MultiValueMap<String, String>>(mapConfirmParams, headers);
				restTemplate.postForEntity( confirmUrl, confirmRequest , TransactionConfirmResponse.class );				
			} catch (Exception e) {
				log.error("Unable to confrim transaction [Error: " + e.getMessage() + "]");
			}
			return "payment";
		} catch (Exception e) {
			log.error("Error processing payment transaction [Error: " + e.getMessage() + "]");
			model.put("message", "Payment Failed - Unexpected Error");
			return "payment";
		}
	}
	

}
