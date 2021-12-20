package com.ta.nfc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ta.nfc.nfcread.NdefMessageParser;
import com.ta.nfc.nfcread.ParsedNdefRecord;

import java.io.IOException;
import java.util.List;

public class NFCReadActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_ACCESSLOCATION = 10;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_HANDLE_NFC_PERM = 1;
    /* access modifiers changed from: private */

    /* renamed from: k */
    IntentFilter[] f5044k;

    /* renamed from: l */

    /* renamed from: m */
    SharedPreferences.Editor f5046m;
    /* access modifiers changed from: private */
    /* access modifiers changed from: private */
    private NfcAdapter mNfcAdapter;

    /* renamed from: n */
    PendingIntent pendingIntent;
    private View navHeader;
    private FrameLayout nfc_layout;

    /* renamed from: o */

    /* renamed from: p */

    /* access modifiers changed from: private */
    public String versionCheck;
    private FloatingActionButton writeFab;

    private void checkDeviceNFCFeature() {
        if (!getPackageManager().hasSystemFeature("android.hardware.nfc")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your device doesn't have NFC feature.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        } else if (ActivityCompat.checkSelfPermission(this, "android.permission.NFC") != 0) {
            requestNFCPermission();
        } else if (this.mNfcAdapter == null || !this.mNfcAdapter.isEnabled()) {
            gotoSettings();
        } else {
            this.mNfcAdapter.enableForegroundDispatch(this, this.pendingIntent, this.f5044k, (String[][]) null);
        }
    }

    /* access modifiers changed from: private */
    public void copyText(String str) {
     /*   if (Build.VERSION.SDK_INT < 11) {
            ((ClipboardManager) getSystemService("clipboard")).setText(str);
        } else {
            ((android.content.ClipboardManager) getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("scan result", str));
        }
        Toast makeText = Toast.makeText(this, "Copied To Clipboard!", 0);
        makeText.setGravity(17, 0, 0);
        makeText.show();
        ((Vibrator) getSystemService("vibrator")).vibrate(80);*/
    }

    private void displayMsgs(NdefMessage[] ndefMessageArr) {
        if (ndefMessageArr != null && ndefMessageArr.length != 0) {
            StringBuilder sb = new StringBuilder();
            List<ParsedNdefRecord> parse = NdefMessageParser.parse(ndefMessageArr[0]);
            int size = parse.size();
            for (int i = 0; i < size; i++) {
                if(parse.get(i)!=null) {
                    sb.append(parse.get(i).str());
                    sb.append("\n");
                }
            }
            if (sb.toString() != null) {
                showResultonDailog(sb.toString());
            }
        }
    }

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ");
        sb.append(toHex(id));
        sb.append(10);
        sb.append("ID (reversed hex): ");
        sb.append(toReversedHex(id));
        sb.append(10);
        sb.append("ID (dec): ");
        sb.append(toDec(id));
        sb.append(10);
        sb.append("ID (reversed dec): ");
        sb.append(toReversedDec(id));
        sb.append(10);
        sb.append("Technologies: ");
        for (String substring : tag.getTechList()) {
            sb.append(substring.substring("android.nfc.tech.".length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String str : tag.getTechList()) {
            if (str.equals(MifareClassic.class.getName())) {
                sb.append(10);
                String str2 = "Unknown";
                try {
                    MifareClassic mifareClassic = MifareClassic.get(tag);
                    switch (mifareClassic.getType()) {
                        case 0:
                            str2 = "Classic";
                            break;
                        case 1:
                            str2 = "Plus";
                            break;
                        case 2:
                            str2 = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(str2);
                    sb.append(10);
                    sb.append("Mifare size: ");
                    sb.append(mifareClassic.getSize() + " bytes");
                    sb.append(10);
                    sb.append("Mifare sectors: ");
                    sb.append(mifareClassic.getSectorCount());
                    sb.append(10);
                    sb.append("Mifare blocks: ");
                    sb.append(mifareClassic.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }
            if (str.equals(MifareUltralight.class.getName())) {
                sb.append(10);
                String str3 = "Unknown";
                switch (MifareUltralight.get(tag).getType()) {
                    case 1:
                        str3 = "Ultralight";
                        break;
                    case 2:
                        str3 = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(str3);
            }
        }
        return sb.toString();
    }

/*
    private void getDataFromServer() {
        DatabaseReference reference = this.f5050q.getReference("nfc_verison");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                String unused = NFCReadActivity.this.versionCheck = String.valueOf(((Double) dataSnapshot.getValue(Double.class)).doubleValue());
                NFCReadActivity.this.versionCheckUpdate();
            }
        });
    }
*/

    private void gotoSettings() {
        showMessageOKCancel("Check your device NFC status its may be OFF. Please Enable/ON the NFC in settings.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                NFCReadActivity.this.startActivityForResult(new Intent("android.settings.SETTINGS"), 8);
            }
        });
    }

    private void init() {
    }

    private void readFromNFC(Ndef ndef) {
        AlertDialog.Builder builder;
        if (ndef != null) {
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                if (ndefMessage == null || ndefMessage.getRecords()[0].getPayload() == null) {
                    builder = new AlertDialog.Builder(this);
                    builder.setMessage("The tag is empty.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                } else {
                    String str = new String(ndefMessage.getRecords()[0].getPayload());
                    Log.d("TAG", "readFromNFC: " + str);
                    if (!str.equalsIgnoreCase("")) {
                        if (!str.equalsIgnoreCase((String) null)) {
                            showResultonDailog(str);
                            ndef.close();
                        }
                    }
                    builder = new AlertDialog.Builder(this);
                    builder.setMessage("The tag is empty.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                }
                builder.show();
                ndef.close();
            } catch (FormatException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestNFCPermission() {
        Log.w("TAG", "Camera permission is not granted. Requesting permission");
        String[] strArr = {"android.permission.NFC"};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.NFC")) {
            ActivityCompat.requestPermissions(this, strArr, 1);
        }
    }

    private void resolveIntent(Intent intent) {
        Log.i("+tag discovered","+");
        NdefMessage[] ndefMessageArr =null;
        String action = intent.getAction();
        if ("android.nfc.action.TAG_DISCOVERED".equals(action) || "android.nfc.action.TECH_DISCOVERED".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) {
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            if (parcelableArrayExtra != null) {
                ndefMessageArr = new NdefMessage[parcelableArrayExtra.length];
                for (int i = 0; i < parcelableArrayExtra.length; i++) {
                    ndefMessageArr[i] = (NdefMessage) parcelableArrayExtra[i];
                }
            } else {

                ndefMessageArr = new NdefMessage[]{new NdefMessage(new NdefRecord[]{new NdefRecord((short)5, new byte[0], intent.getByteArrayExtra("android.nfc.extra.ID"), dumpTagData((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")).getBytes())})};

                ndefMessageArr = new NdefMessage[]{new NdefMessage(new NdefRecord[]{
                        new NdefRecord((short)5, new byte[0],
                                intent.getByteArrayExtra("android.nfc.extra.ID"),
                                dumpTagData((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")).getBytes())})
                };
            }
            displayMsgs(ndefMessageArr);
            Log.i("+tag discovered","+");
        }
    }


    private void showResultonDailog(final String str) {
        Log.i("++tag data",str);
       /* Utils.f5130b++;
        String format = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String str2 = (str.contains("http://") || str.contains("https://")) ? "url" : "text";
        if (this.f5045l.uploadData(format, str2, str) == -1) {
            Toast.makeText(this, "Not stored in History", 0).show();
        }
        new ToneGenerator(3, 120).startTone(44, ModuleDescriptor.MODULE_VERSION);
        ((Vibrator) getSystemService("vibrator")).vibrate(100);*/
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.result_dailog_layout);
        TextView textView = (TextView) dialog.findViewById(R.id.date);
        TextView textView2 = (TextView) dialog.findViewById(R.id.result);
        TextView textView3 = (TextView) dialog.findViewById(R.id.open);
        TextView textView4 = (TextView) dialog.findViewById(R.id.share);
        TextView textView5 = (TextView) dialog.findViewById(R.id.search);
        TextView textView6 = (TextView) dialog.findViewById(R.id.copy);
        Button button = (Button) dialog.findViewById(R.id.history);
     /*   if (!str2.equalsIgnoreCase("url")) {
            textView3.setVisibility(8);
        }*/
      /*  if (Utils.isStoreVersion(this)) {
            final AdView adView = (AdView) dialog.findViewById(R.id.madView);
            AdRequest build = new AdRequest.Builder().build();
            adView.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(0);
                }
            });
            adView.loadAd(build);
        }*/
        //   textView.setText(format);
        textView2.setText(str);
        textView2.setMovementMethod(new ScrollingMovementMethod());
        textView2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NFCReadActivity.this.copyText(str);
            }
        });
        textView6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NFCReadActivity.this.copyText(str);
            }
        });
       /* textView3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    NFCReadActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                } catch (ActivityNotFoundException unused) {
                    Toast.makeText(NFCReadActivity.this, "you don't have any apps for perform this action...", 0).show();
                }
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        // window.setFlags(262144, 262144);
        //  window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setAttributes(attributes);
        dialog.show();
    }

    private long toDec(byte[] bArr) {
        long j = 0;
        long j2 = 1;
        for (byte b : bArr) {
            j += (((long) b) & 255) * j2;
            j2 *= 256;
        }
        return j;
    }

    private String toHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int length = bArr.length - 1; length >= 0; length--) {
            byte b = bArr[length]; // UnsignedBytes.MAX_VALUE;
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
            if (length > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long toReversedDec(byte[] bArr) {
        long j = 0;
        long j2 = 1;
        for (int length = bArr.length - 1; length >= 0; length--) {
            j += (((long) bArr[length]) & 255) * j2;
            j2 *= 256;
        }
        return j;
    }

    private String toReversedHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bArr.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            byte b = bArr[i];// & UnsignedBytes.MAX_VALUE;
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
/*
    public void versionCheckUpdate() {
        if (this.versionCheck != null && !this.versionCheck.isEmpty() && !this.versionCheck.equalsIgnoreCase(BuildConfig.VERSION_NAME) && !isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon((int) R.C1201drawable.nfc);
            builder.setTitle((CharSequence) "App Update");
            builder.setMessage((CharSequence) "Update the app to latest version " + this.versionCheck + " from google play store and get new features with good performance");
            builder.setPositiveButton((CharSequence) "Update Now", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    NFCReadActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(NFCReadActivity.this.getResources().getString(R.string.applink))));
                }
            });
            builder.setNegativeButton((CharSequence) "Later", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                }
            });
            androidx.appcompat.app.AlertDialog create = builder.create();
            if (!isFinishing()) {
                create.show();
            }
            Button button = create.getButton(-2);
            Button button2 = create.getButton(-1);
            if (button != null) {
                button.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
            if (button2 != null) {
                button2.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            }
        }
    }
*/

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView( R.layout.read_layout);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    /* public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.C1204menu.main, menu);
        return true;
    }*/

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }



    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        String str;
        DialogInterface.OnClickListener r3;
        if (i != 1) {
            return;
        }
        if (iArr.length == 0 || iArr[0] != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.NFC")) {
                str = "NFC Permission required for this app.";
                r3 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(NFCReadActivity.this, new String[]{"android.permission.CAMERA"}, 1);
                    }
                };
            } else {
                str = "NFC Permission required for this app. Please Enable the NFC access permission in settings permission.";
                r3 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", NFCReadActivity.this.getPackageName(), (String) null));
                        NFCReadActivity.this.startActivityForResult(intent, 5);
                    }
                };
            }
            showMessageOKCancel(str, r3);
        } else if (this.mNfcAdapter == null || !this.mNfcAdapter.isEnabled()) {
            gotoSettings();
        } else {
            this.mNfcAdapter.enableForegroundDispatch(this, this.pendingIntent, this.f5044k, (String[][]) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mNfcAdapter != null) {
            if (!this.mNfcAdapter.isEnabled()) {
                checkDeviceNFCFeature();
            }
            this.mNfcAdapter.enableForegroundDispatch(this, this.pendingIntent, (IntentFilter[]) null, (String[][]) null);
        }
     /*   this.f5049p.getMenu().getItem(0).setChecked(true);*/
        checkDeviceNFCFeature();
    }

    public void showMessageOKCancel(String str, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher_background).setMessage(str).setCancelable(false).setPositiveButton("OK", onClickListener).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).create().show();
    }
}
