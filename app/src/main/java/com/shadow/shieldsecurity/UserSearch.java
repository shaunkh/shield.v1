package com.shadow.shieldsecurity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipconfig.IPConfiguration;
import com.jsonparser.JSONParser;
import com.model.CrimeDetailsModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class UserSearch extends AppCompatActivity {

    Button btn_more;
    ImageButton btn_search, btn_location;
    EditText txtSearch;
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;
    TextView securitylvl;
    JSONArray jsonArray;
    ProgressDialog progressDialog;
    ArrayList<Object> searchList = new ArrayList<Object>();
    ListView searchlv;
    GPSTracker gps;
    double longitude, latitude;
    String country;

    ArrayList<CrimeDetailsModel> listModel;

    private static String url_search = IPConfiguration.getIP() + "/SecurityDBConnection/CrimeDetailsSV";
    private static final String TAG_SEARCH_NODE = "crimeNodes";
    private static final String TAG_SEARCH_CITYNAME = "cityname";
    private static final String TAG_SEARCH_ROAD = "roadnumber";
    private static final String TAG_SEARCH_DSC = "description";
    private static final String TAG_SEARCH_CRIMETYPE = "crimetype";
    private static final String TAG_SEARCH_LNG= "lng";
    private static final String TAG_SEARCH_LAT = "lat";
    private static final String TAG_SEARCH_COUNTRY = "country";
    private static final String TAG_SEARCH_POSTALCODE = "postalcode";
    private static final String TAG_SEARCH_CRIMEDATE = "crimedate";






    String road;
    String postalCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        gps = new GPSTracker(this);

        // finding from location services
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.e("latitude :", latitude + "");
            Log.e("longitude :", longitude + "");
        }
        //

        progressDialog = new ProgressDialog(UserSearch.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Searching...");



        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_location = (ImageButton)findViewById(R.id.btn_location);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        btn_more = (Button)findViewById(R.id.btnMore);
        searchlv = (ListView)findViewById(R.id.searchlv);
        securitylvl = (TextView)findViewById(R.id.securitylvl);


        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/ADAM.CG PRO.otf");
        securitylvl.setTypeface(face);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                road =txtSearch.getText().toString();
                if (txtSearch.getText().toString().isEmpty()){
                    Toast.makeText(UserSearch.this, "Please enter road/area", Toast.LENGTH_SHORT).show();
                    txtSearch.requestFocus();
                }else if(isOnline()){
                    new CrimeSearch().execute();
                }else{
                    Toast.makeText(UserSearch.this, "No internet connection", Toast.LENGTH_SHORT).show();

                }

            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                if(isOnline()){
                    new CrimeSearch().execute();
                } else{
                    Toast.makeText(UserSearch.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ShowAlertDialogWithListview();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CrimeSearch extends AsyncTask<String, String, String> {
        String out_message;
        String out_code;









        @Override
        protected void onPreExecute() {
            progressDialog.show();

            try{
                searchList.clear();
                searchlv.removeAllViews(); //removes all data before
            }catch(Exception e){

            }




        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();


            listModel = new ArrayList<CrimeDetailsModel>();
            if (searchList != null){
                for(Iterator<Object> i = searchList.iterator(); i.hasNext();){
                    CrimeDetailsModel crimeDetailsModel = new CrimeDetailsModel();

                    crimeDetailsModel.setCityname(i.next().toString());
                    crimeDetailsModel.setRoadnumber(i.next().toString());
                    crimeDetailsModel.setDescription(i.next().toString());
                    crimeDetailsModel.setCrimetype(i.next().toString());
                    crimeDetailsModel.setLng(i.next().toString());
                    crimeDetailsModel.setLat(i.next().toString());
                    crimeDetailsModel.setCountry(i.next().toString());
                    crimeDetailsModel.setPostalcode(i.next().toString());
                    crimeDetailsModel.setCrimedate(i.next().toString());

                    listModel.add(crimeDetailsModel);



                }
                SearchAdapter searchAdapter = new SearchAdapter(UserSearch.this, listModel);
                searchlv.setAdapter(searchAdapter);
            }


        }

        @Override
        protected String doInBackground(String... args) {




            List<NameValuePair> params = new ArrayList<NameValuePair>();


            //params.add(new BasicNameValuePair("postalcode", postalCode));
            params.add(new BasicNameValuePair("roadnumber", road));

            jsonObject = jsonParser.makeHttpRequest(url_search, "POST", params);

            //Log.e("JSON Response", jsonObject.toString());

            try {
                if (jsonObject != null) {
                    jsonArray = jsonObject.getJSONArray(TAG_SEARCH_NODE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        searchList.add(c.getString(TAG_SEARCH_CITYNAME));
                        searchList.add(c.getString(TAG_SEARCH_ROAD));
                        searchList.add(c.getString(TAG_SEARCH_DSC));
                        searchList.add(c.getString(TAG_SEARCH_CRIMETYPE));
                        searchList.add(c.getString(TAG_SEARCH_LNG));
                        searchList.add(c.getString(TAG_SEARCH_LAT));
                        searchList.add(c.getString(TAG_SEARCH_COUNTRY));
                        searchList.add(c.getString(TAG_SEARCH_POSTALCODE));
                        searchList.add(c.getString(TAG_SEARCH_CRIMEDATE));
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
    //End Deposit AMount

    public class SearchAdapter extends ArrayAdapter<CrimeDetailsModel> {

        // View lookup cache

        private  class ViewHolder {

            TextView crimetypevalue;
            TextView cityvalue;
            TextView postalcodevalue ;
            TextView roadvalue ;
            TextView dscvalue;
            TextView countryvalue;



        }



        public SearchAdapter(Context context, ArrayList<CrimeDetailsModel> loandAmomount) {

            super(context, R.layout.search_row, loandAmomount);

        }



        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position

            CrimeDetailsModel model = getItem(position);

            ViewHolder viewHolder; // view lookup cache stored in tag

            if (convertView == null) {

                viewHolder = new ViewHolder();

                LayoutInflater inflater = LayoutInflater.from(getContext());

                convertView = inflater.inflate(R.layout.search_row, parent, false);

                viewHolder.crimetypevalue = (TextView) convertView.findViewById(R.id.crimetypevalue);
                viewHolder.cityvalue = (TextView) convertView.findViewById(R.id.cityvalue);
                viewHolder.postalcodevalue = (TextView) convertView.findViewById(R.id.postcodevalue);
                viewHolder.roadvalue = (TextView) convertView.findViewById(R.id.roadvalue);
                viewHolder.dscvalue = (TextView) convertView.findViewById(R.id.dscvalue);
                viewHolder.countryvalue = (TextView) convertView.findViewById(R.id.countryvalue);



                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            // Populate the data into the template view using the data object

            viewHolder.crimetypevalue.setText(model.getCrimetype());


            viewHolder.cityvalue.setText(model.getCityname());
            viewHolder.postalcodevalue.setText(model.getPostalcode());
            viewHolder.roadvalue.setText(model.getRoadnumber());
            viewHolder.dscvalue.setText(model.getDescription());
            viewHolder.countryvalue.setText(model.getCountry());








            // Return the completed view to render on screen

           /* if (position%2== 0) {
                convertView.setBackgroundColor(Color.parseColor("#F5EEE5"));
            }else{

                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } */

            return convertView;

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
        //postalCode  = addresses.get(0).getPostalCode();
        road = addresses.get(0).getAddressLine(0);
        //city = addresses.get(0).getLocality();
        //String knownName = addresses.get(0).getFeatureName();

        // Toast.makeText(this.getApplicationContext(),address,Toast.LENGTH_LONG).show();

    }
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
        mOptions.add("Local Police");
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
                    case 2:
                        String number = "01755542555";
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        startActivity(callIntent);
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
                        Intent intent = new Intent(UserSearch.this, LoginActivity.class);
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
