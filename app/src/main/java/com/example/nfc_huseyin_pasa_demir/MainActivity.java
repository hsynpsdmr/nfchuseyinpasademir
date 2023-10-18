package com.example.nfc_huseyin_pasa_demir;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    private PendingIntent pendingIntent;
    private IntentFilter[] readFilters;

    private NfcAdapter nfcAdapter;

    Tag myTag;



    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.nfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter== null) {
            Toast.makeText(this, "This device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_ALTERNATIVE);
        readFilters = new IntentFilter [] {tagDetected};


//        processNFC(getIntent());
//        try {
//            Intent intent = new Intent(this, getClass());
//            intent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            IntentFilter textFilter = new IntentFilter (NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain");
//            readFilters = new IntentFilter [] {textFilter};
//        } catch (IntentFilter.MalformedMimeTypeException e) {
//            e.printStackTrace();
//        }
    }

    private void enableRead() {
        if(nfcAdapter==null)
        {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, readFilters, null);
    }

    private void disableRead() {
        if(nfcAdapter==null)
        {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        nfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        enableRead();
    }
    @Override
    protected void onPause() {
        super.onPause();
        disableRead();
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        //processNFC(intent);
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    private void processNFC (Intent intent) {
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ;
        textView.setText("");
        if (messages != null) {
            for (Parcelable message : messages) {
                NdefMessage ndefMessage = (NdefMessage) message;
                for (NdefRecord record : ndefMessage.getRecords()) {
                    switch (record.getTnf()) {
                        case NdefRecord.TNF_WELL_KNOWN:
                            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                textView.append("TEXT: ");
                                textView.append(new String(record.getPayload()));
                                textView.append("\n");
                            }
                    }
                }
            }
        }
    }



    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage [rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) { msgs[i]= (NdefMessage) rawMsgs[1];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();

        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;
        try {
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        textView.setText("NFC Content:" + text);
    }
}