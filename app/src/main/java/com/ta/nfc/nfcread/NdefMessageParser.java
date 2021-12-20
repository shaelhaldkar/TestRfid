package com.ta.nfc.nfcread;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {
    private NdefMessageParser() {
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] ndefRecordArr) {
        Object parse = null;
        ArrayList arrayList = new ArrayList();
        for (final NdefRecord ndefRecord : ndefRecordArr) {
            /*if (UriRecord.isUri(ndefRecord)) {
                parse = UriRecord.parse(ndefRecord);
            }
            else*/
                if (TextRecord.isText(ndefRecord)) {
                parse = TextRecord.parse(ndefRecord);
            }
              /*  else if (SmartPoster.isPoster(ndefRecord)) {
                parse = SmartPoster.parse(ndefRecord);
            }*/
                else {
                arrayList.add(new ParsedNdefRecord() {
                    public String str() {
                        return new String(ndefRecord.getPayload());
                    }
                });
            }
            arrayList.add(parse);
        }
        return arrayList;
    }

    public static List<ParsedNdefRecord> parse(NdefMessage ndefMessage) {
        return getRecords(ndefMessage.getRecords());
    }
}
