/*
 * Copyright 2014 A.C.R. Development
 */
package com.example.administrator.baofen.base;

import android.content.Context;
import android.content.Intent;

public final class Utils {

	private Utils() {
	}

	public static Intent newEmailIntent(Context context, String address, String subject,
                                        String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}
}
