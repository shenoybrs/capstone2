package com.capstone.offerbank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.offerbank.gson.GsonParser;
import com.capstone.offerbank.service.AppSyncService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class BankSelectActivity extends ActionBarActivity {
    private static final String SHARED_PREFS_FILE = "BankSelectActivity";
    private static final String BANKS = "BANKS";
    private String[] intentString;
    private ArrayList<BankNames> bankNamesList = new ArrayList<BankNames>();
    MyCustomAdapter dataAdapter = null;
    private Intent intent;
    String filename = "time.ser";

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int count = 0; count < bankNamesList.size(); count++) {
            Prefs.getInstance().storeBankObject(count, bankNamesList.get(count));
        }
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bank_selection_main);
        //analytics tracking
        ((AppApplication) getApplication()).startTracking();


        intent = new Intent(this,AppsGridActivity.class);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        loadInterstitialAd();

        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        GsonParser<BankNames> parser = new GsonParser<BankNames>(
                BankNames.class);


        BankNames storedbanknames = null;
        String storedBankString= null;
        for (int i=0;i<=6;i++)
        {
            storedBankString = Prefs.getInstance().getAppObject(i);
            if (!storedBankString.equals("")) {
                try {
                    storedbanknames = parser.parse(storedBankString);
                    bankNamesList.add(storedbanknames);

                } catch (Exception e) {
                    break;

                }
            }else
            {
                break;
            }
        }
        if (bankNamesList.size()==0) {
            //Generate list View from ArrayList
            displayListView();
        }

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.bank_info, bankNamesList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        checkButtonClick();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void displayListView() {

        //Array list of countries

        BankNames bankNames = new BankNames("AMEX","American Express",false);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("CITI","CITI Bank",true);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("ICICI","ICICI Bank",false);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("AXIS","AXIS BANK",true);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("HDFC","HDFC BANK",true);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("CAN","CANNARA BANK",false);
        bankNamesList.add(bankNames);
        bankNames = new BankNames("VIJ","VIJAYA BANK",false);
        bankNamesList.add(bankNames);





    }

    private class MyCustomAdapter extends ArrayAdapter<BankNames> {

        private ArrayList<BankNames> bankNamesList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<BankNames> bankNamesList) {
            super(context, textViewResourceId, bankNamesList);
            this.bankNamesList = new ArrayList<BankNames>();
            this.bankNamesList.addAll(bankNamesList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.bank_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        BankNames bankNames = (BankNames) cb.getTag();
                        bankNames.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            BankNames bankNames = bankNamesList.get(position);
            holder.code.setText(" (" +  bankNames.getCode() + ")");
            holder.name.setText(bankNames.getName());
            holder.name.setChecked(bankNames.isSelected());
            holder.name.setTag(bankNames);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelectedbank);
        new Thread() {
            public void run() {
                AppSyncService.setAlarm(getApplicationContext());
            }
        }.run();
        if (myButton != null) {
            myButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    StringBuffer responseText = new StringBuffer();
                    responseText.append("The following were selected...\n");
                    ArrayList<String> selectedBank = new ArrayList<String>();
                    ArrayList<BankNames> bankNamesList = dataAdapter.bankNamesList;
                    for(int i = 0; i< bankNamesList.size(); i++){
                        BankNames bankNames = bankNamesList.get(i);
                        if(bankNames.isSelected()){
                            responseText.append("\n" + bankNames.getName());
                            selectedBank.add(bankNames.getCode());

                            }
                        }


                    mInterstitialAd.show();
                    if (selectedBank.size()>0) {
                        intentString = new String[selectedBank.size()];
                        intentString = selectedBank.toArray(intentString);
                        //
                        intent.putExtra("banks", intentString);

                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                R.string.select_one_offer, Toast.LENGTH_LONG).show();
                    }


                }
            });
        }

    }

    private InterstitialAd mInterstitialAd;

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("835D0177C8E30169C70763EA8806262B")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public static class BankNames implements Parcelable {

        String code = null;
        String name = null;
        boolean selected = false;

        public BankNames(String code, String name, boolean selected) {
            super();
            this.code = code;
            this.name = name;
            this.selected = selected;
        }

        protected BankNames(Parcel in) {
            code = in.readString();
            name = in.readString();
            selected = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(name);
            dest.writeByte((byte) (selected ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BankNames> CREATOR = new Creator<BankNames>() {
            @Override
            public BankNames createFromParcel(Parcel in) {
                return new BankNames(in);
            }

            @Override
            public BankNames[] newArray(int size) {
                return new BankNames[size];
            }
        };

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }

}
