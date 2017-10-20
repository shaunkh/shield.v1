package com.shadow.shieldsecurity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipconfig.IPConfiguration;
import com.jsonparser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewPost extends AppCompatActivity {

    RadioButton manualrb, autorb;
    Spinner sp_city,sp_crimetype, sp_country;
    EditText et_rd, et_dsc, et_postalCode, et_date;
    Button btn_post, btn_clear, btn_feed, btnMore;
    ImageButton btn_date;
    TextView txtheader;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    LinearLayout addresslayout, citylayout, countrylayout, postalCodelayout;
    String userid;
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;
    JSONArray jsonArray;
    Animation animScroll;
    double longitude, latitude;
    String lng, lat;

    private static String url_post = IPConfiguration.getIP() + "/SecurityDBConnection/PostSV";
    private static final String TAG_POST_NODE = "postNodes";
    private static final String TAG_MESSAGE = "out_message";
    private static final String TAG_OUTCODE = "out_code";


    //
    String city ;//= sp_city.getSelectedItem().toString();
    String crimetype;//=sp_crimetype.getSelectedItem().toString();
    String country;
    String postalCode;
    String crimedate;

    String address; //= et_rd.getText().toString();
    String desc;// = et_dsc.getText().toString();
//
    String postType = "M";

    private com.shadow.shieldsecurity.GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pos);
        gps = new GPSTracker(this);
        LoadGbobalVariables();
        addresslayout = (LinearLayout)findViewById(R.id.addresslayout);
        citylayout = (LinearLayout)findViewById(R.id.citylayout);
        countrylayout=(LinearLayout)findViewById(R.id.countrylayout);
        postalCodelayout = (LinearLayout) findViewById(R.id.layout_postalCode);
        txtheader = (TextView) findViewById(R.id.txtheader);
        btn_feed = (Button)findViewById(R.id.btnFeed);
        btnMore = (Button) findViewById(R.id.btnMore);

        animScroll = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation);

        ///

        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            lng = longitude+ "";
            lat = latitude+ "";
            Log.e("latitude :", latitude+"");
            Log.e("longitude :", longitude + "");
        }
        //



        //

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.check(R.id.manualrb);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.autorb) {
                    postType = "A";
                    citylayout.setVisibility(View.GONE);
                    addresslayout.setVisibility(View.GONE);
                    countrylayout.setVisibility(View.GONE);
                    postalCodelayout.setVisibility(View.GONE);
                    et_rd.setText("");
                    et_dsc.setText("");
                    txtheader.setText("");

                } else if (checkedId == R.id.manualrb) {
                    postType = "M";
                    citylayout.setVisibility(View.VISIBLE);
                    addresslayout.setVisibility(View.VISIBLE);
                    countrylayout.setVisibility(View.VISIBLE);
                    postalCodelayout.setVisibility(View.VISIBLE);
                    lng = "";
                    lat = "";


                }
            }

        });

        manualrb = (RadioButton) findViewById(R.id.manualrb);
        autorb = (RadioButton) findViewById(R.id.autorb);
        sp_city = (Spinner) findViewById(R.id.sp_city);
        sp_crimetype = (Spinner) findViewById(R.id.sp_crimetype);
        sp_country=(Spinner) findViewById(R.id.sp_country);
        et_rd = (EditText) findViewById(R.id.et_rd);
        et_dsc = (EditText) findViewById(R.id.et_dsc);
        btn_post = (Button) findViewById(R.id.btn_post);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        et_postalCode = (EditText) findViewById(R.id.et_postalCode);
        btn_date = (ImageButton) findViewById(R.id.btn_date);
        et_date = (EditText) findViewById(R.id.et_Date);
        progressDialog = new ProgressDialog(NewPost.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Posting...");

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (postalCodelayout.getVisibility() == View.VISIBLE && et_postalCode.getText().toString().isEmpty()) {
                    txtheader.setText("Please Enter Postal Code");
                    txtheader.setTextColor(Color.parseColor("#F50000"));
                    txtheader.startAnimation(animScroll);

                    et_postalCode.requestFocus();

                } else if (addresslayout.getVisibility() == View.VISIBLE && et_rd.getText().toString().isEmpty()) {
                    txtheader.setText("Please Enter Address");
                    txtheader.setTextColor(Color.parseColor("#F50000"));
                    txtheader.startAnimation(animScroll);

                    et_rd.requestFocus();
                } else if (et_dsc.getText().toString().isEmpty()) {
                    txtheader.setText("Please Enter A Short Description");
                    txtheader.setTextColor(Color.parseColor("#F50000"));
                    txtheader.startAnimation(animScroll);

                    et_dsc.requestFocus();
                } else if (et_date.getText().toString().isEmpty()) {
                    txtheader.setText("Please Select Crime Date");
                    txtheader.setTextColor(Color.parseColor("#F50000"));
                    txtheader.startAnimation(animScroll);

                    et_date.requestFocus();
                } else if (isOnline()) {
                    txtheader.setText("");
                    new postCrime().execute();
                } else {
                    txtheader.setText("No internet connection");
                    txtheader.setTextColor(Color.parseColor("#F50000"));
                    txtheader.startAnimation(animScroll);
                }
            }
        });

      btn_clear.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              doClear2();
          }
      });

btn_date.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showDialog(0);
    }
});

        btn_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPost.this, MainActivity.class);
                startActivity(intent);
            }
        });

btnMore.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ShowAlertDialogWithListview();
    }
});

    }



    private class postCrime extends AsyncTask<String, String, String> {
        String out_message;
        String out_code;









        @Override
        protected void onPreExecute() {
            progressDialog.show();

            if (postType.equals("A")){
                crimetype=sp_crimetype.getSelectedItem().toString();
                desc = et_dsc.getText().toString();
                crimedate = et_date.getText().toString();
                getLocation();
            }else{
                city = sp_city.getSelectedItem().toString();
                address = et_rd.getText().toString();
                crimetype=sp_crimetype.getSelectedItem().toString();
                desc = et_dsc.getText().toString();
                country = sp_country.getSelectedItem().toString();
                postalCode= et_postalCode.getText().toString();
                crimedate = et_date.getText().toString();
                lat = "";
                lng = "";

            }




        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();


            if("0".equals(out_code)){
                doClear();
                Toast.makeText(getApplicationContext(), out_message, Toast.LENGTH_SHORT).show();
                txtheader.setText(out_message);

                txtheader.setTextColor(Color.parseColor("#4F8A10"));
                txtheader.startAnimation(animScroll);



            } else{
                Toast.makeText(getApplicationContext(), out_message, Toast.LENGTH_SHORT).show();
                txtheader.setText(out_message);

                txtheader.setTextColor(Color.parseColor("#F50000"));
                txtheader.startAnimation(animScroll);
            }

        }

        @Override
        protected String doInBackground(String... args) {




            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("post_type", postType));
            params.add(new BasicNameValuePair("country", country));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("crime", crimetype));
            params.add(new BasicNameValuePair("address", address)); //address
            params.add(new BasicNameValuePair("desc", desc));
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("lng", lng));
            params.add(new BasicNameValuePair("lat", lat));
            params.add(new BasicNameValuePair("postalcode", postalCode));
            params.add(new BasicNameValuePair("crimedate", crimedate));

            jsonObject = jsonParser.makeHttpRequest(url_post, "POST", params);
            try {
                if (jsonObject != null) {
                    jsonArray = jsonObject.getJSONArray(TAG_POST_NODE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        out_message = c.getString(TAG_MESSAGE);
                        out_code = c.getString(TAG_OUTCODE);
                    }

                    // Log.d("error Code", errorCode);
                } else {
                    // Log.d("Server Unreachable",
                    // "Unable to Perform Your request.Server Is Unreachable! Please Try Later.");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    void getLocation(){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.ENGLISH);
        //


        try {


            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        // String state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode  = addresses.get(0).getPostalCode();
        address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getLocality();
        Log.e("address:",address);
        //String knownName = addresses.get(0).getFeatureName();

       // Toast.makeText(this.getApplicationContext(),address,Toast.LENGTH_LONG).show();

    }
    private void LoadGbobalVariables() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);

        userid = sp.getString("userid", "");

    }

    void doClear (){
        et_postalCode.setText("");
        et_rd.setText("");
        et_dsc.setText("");
        sp_country.setSelection(0);
        sp_crimetype.setSelection(0);
        sp_city.setSelection(0);
        et_date.setText("");


    }
    void doClear2 (){
        et_postalCode.setText("");
        et_rd.setText("");
        et_dsc.setText("");
        sp_country.setSelection(0);
        sp_crimetype.setSelection(0);
        sp_city.setSelection(0);
        et_date.setText("");
        txtheader.setText("");

    }
    //
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view,int year, int month, int day) {

            String month1 = null;
            String day1 = null;
            if (month < 9) {
                month1 = "0" + (month + 1);
            }else{
                month1 = ""+(month + 1);
            }
            if (view.getDayOfMonth() <= 9) {
                day1 = "0" + view.getDayOfMonth();
            }else{
                day1 = ""+view.getDayOfMonth();
            }

            et_date.setText(day1 + "/" + month1 + "/" + year);



        }
    };
    //
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if (getCurrentFocus() != null){
            InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        return super.dispatchTouchEvent(ev);
    }
    public void ShowAlertDialogWithListview()
    {
        List<String> mOptions = new ArrayList<String>();
        mOptions.add("Log Out");
        mOptions.add("Contact");
        //Create sequence of items
        final CharSequence[] Animals = mOptions.toArray(new String[mOptions.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Please select one:");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //String selectedText = Animals[item].toString();  //Selected item in listview
                //Toast.makeText(getApplicationContext(), selectedText + item,Toast.LENGTH_LONG).show();
                switch (item) {
                    case 0:
                        alertMessage();
                        break;
                    case 1:

                        break;
                }

            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked\
                        Intent intent = new Intent(NewPost.this, LoginActivity.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Log out now?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}

