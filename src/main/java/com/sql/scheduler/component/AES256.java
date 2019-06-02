package com.sql.scheduler.component;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class AES256 {
	private static String ENCODING_KEY = "aes256-secretKey";
	private static String INSTANCE_REFERENCE = "AES/CBC/PKCS5Padding";
	private Key keySpec;

	public AES256() throws Exception {
		byte[] keyBytes = new byte[ENCODING_KEY.length()];
		byte[] b = ENCODING_KEY.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0 , len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		this.keySpec = keySpec;
	}

	public String AESEncoder(String targetStr) throws Exception {
		Cipher c = Cipher.getInstance(INSTANCE_REFERENCE);
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ENCODING_KEY.getBytes()));

		byte[] encrypted = c.doFinal(targetStr.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(encrypted));
	}

	public String AESDecoder(String targetStr) throws Exception {
		Cipher c = Cipher.getInstance(INSTANCE_REFERENCE);
		c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ENCODING_KEY.getBytes("UTF-8")));

		byte[] byteStr = Base64.decodeBase64(targetStr.getBytes());
		return new String(c.doFinal(byteStr), "UTF-8");
	}
}
