package com.hcmut.smarthome.service.test;

import static org.junit.Assert.assertThat;

import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class SHA1Test {
	
	@Test
	public void should_convert_to_SHA1() throws Exception {
		String str = "Nguyen Thanh Tung";
		assertThat(DigestUtils.sha1Hex(str), CoreMatchers.is("f3db989d561b2157ae675aa7ed8186eb6dde8d43"));
		System.out.println(DigestUtils.sha1Hex("123456"));
	}

}
