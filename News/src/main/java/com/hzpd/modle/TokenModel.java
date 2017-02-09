package com.hzpd.modle;

import com.hzpd.utils.CipherUtils;


public class TokenModel {
	private String signature;
	private String timestamp;
	private String nonce;

	public TokenModel() {
		nonce = "nonce";
		timestamp = "" + (System.currentTimeMillis() / 1000);

		StringBuilder sb = new StringBuilder();
		sb.append("oiadshasfdj");
		sb.append(timestamp);
		sb.append(nonce);

		signature = CipherUtils.md5(sb.toString());

	}

	public String getSignature() {
		return signature;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getNonce() {
		return nonce;
	}
}
