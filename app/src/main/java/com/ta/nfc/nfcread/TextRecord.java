package com.ta.nfc.nfcread;

import android.nfc.NdefRecord;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import io.fabric.sdk.android.services.network.HttpRequest;

public class TextRecord implements ParsedNdefRecord {
    private final String mLanguageCode;
    private final String mText;

    public TextRecord(String str, String str2) {
        this.mLanguageCode = (String) Preconditions.checkNotNull(str);
        this.mText = (String) Preconditions.checkNotNull(str2);
    }

    public static boolean isText(NdefRecord ndefRecord) {
        try {
            parse(ndefRecord);
            return true;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public static TextRecord parse(NdefRecord ndefRecord) {
        Preconditions.checkArgument(ndefRecord.getTnf() == 1);
        Preconditions.checkArgument(Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT));
        try {
            byte[] payload = ndefRecord.getPayload();
            String str = (payload[0] & UnsignedBytes.MAX_POWER_OF_TWO) == 0 ? HttpRequest.CHARSET_UTF8 : "UTF-16";
            byte b = (byte) (payload[0] & 63);
            return new TextRecord(new String(payload, 1, b, "US-ASCII"), new String(payload, b + 1, (payload.length - b) - 1, str));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    public String getText() {
        return this.mText;
    }

    public String str() {
        return this.mText;
    }
}
