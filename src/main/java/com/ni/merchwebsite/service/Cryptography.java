package com.ni.merchwebsite.service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import javax.crypto.spec.DESedeKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;


public class Cryptography {
		    
	    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	    
	    /* Decrypt a string */
	    public static String encrypt(SecretKey key, String data) throws Exception {
	        try {
	            /* Create and initialize the encryption engine */
	            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	            cipher.init(Cipher.ENCRYPT_MODE, key);

	            Cipher cipher_dec = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	            cipher_dec.init(Cipher.DECRYPT_MODE, key);
	            
	            byte[] in_bytes = data.getBytes();
	            byte[] enc_bytes = cipher.doFinal(in_bytes);

	            return toHexString(enc_bytes);
	        } catch (Exception e){
	            /* Cryptographic Error */
	            throw new Exception("Cryptographic Error - " + e.getMessage());
	        }
	    }
	    
	    /* Encrypt a string */
	    public static String decrypt(SecretKey key, String data) throws Exception {
	        try {
	            // Create and initialize the encryption engine
	            Cipher cipher_dec = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	            cipher_dec.init(Cipher.DECRYPT_MODE, key);
	          
	            byte[] enc_bytes = toByteArray(data);
	            byte[] dec_bytes = cipher_dec.doFinal(enc_bytes);
	            String str_decrypted = new String(dec_bytes);

	            return str_decrypted;
	        } catch (Exception e){
	        	/* Cryptographic Error */
	            throw new Exception("Cryptographic Error - " + e.getMessage());
	        }
	    }
	    
	    /* Convert a hex string to SecretKey */
	    public static SecretKey GetKey(String hex_key) throws Exception {
	        try {
	        	/* Convert hex string to raw bytes */
	            String str_key = hex_key;
	            if (str_key.length() < 48){
	                str_key = String.format("%1$48s", str_key).replace(" ", "F");
	            }
	            byte[] mykey = toByteArray(str_key);

	            /* Convert the raw bytes to a secret key */ 
	            DESedeKeySpec keyspec = new DESedeKeySpec(mykey);
	            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
	            SecretKey key = keyfactory.generateSecret(keyspec);
	            return key;
	            
	        } catch (Exception e){
	        	/* Cryptographic Error */
	            throw new Exception("Cryptographic Error - " + e.getMessage());
	        }
	    }
	    
	    /* Generate a random encryption key */
	    public static String GenKey() throws Exception {
	    	try {
				KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
				SecureRandom secRandom = new SecureRandom();
				keyGen.init(secRandom);
				SecretKey key = keyGen.generateKey();
				byte[] byt_key = key.getEncoded();
				return toHexString(byt_key);
	    	} catch (Exception e) {
	        	/* Cryptographic Error */
	            throw new Exception("Cryptographic Error - " + e.getMessage());
	    	}
	    }
	    
	    /* Convert String to Hex */
	    public static String CharToHex(String CharString) {
	        return String.format("%040x", new BigInteger(1, CharString.getBytes()));
	    }
	    
	    /* Convert byte array to Hex */
	    public static String toHexString(byte[] array) {
	        return DatatypeConverter.printHexBinary(array);
	    }
	    
	    /* Convert Hex to byte array */
	    public static byte[] toByteArray(String s) {
	        return DatatypeConverter.parseHexBinary(s);
	    }
	  
	    
	}


