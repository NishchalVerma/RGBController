package com.example.nishchal.rgbcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    public final static String PREF_IP = "PREF_IP_ADDRESS";
    public final static String PREF_PORT = "PREF_PORT_NUMBER";

    private EditText editTextIPAddress, editTextPortNumber;

    TextView rvalue, gvalue, bvalue,hexvalue;
    SeekBar rseekbar, gseekbar, bseekbar;
    Button colorviewer,getcolor, sendButton;
    EditText gethexcode;
    int r=0,g=0,b=0,myColor;
    /*int c=0,m=0,y=0,k=0;*/
    String hex="#000000", iPAddress,portNumber,rgbString="0:0:0",getHex;

    ColorPickerDialog colorPickerDialog;
    int color = Color.parseColor("#000000");

    // shared preferences objects used to save the IP address and port so that the user doesn't have to
    // type them next time he/she opens the app.
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("HTTP_HELPER_PREFS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        rvalue = (TextView) findViewById(R.id.rvalue);
        rseekbar = (SeekBar) findViewById(R.id.rseekbar);
        gvalue = (TextView) findViewById(R.id.gvalue);
        gseekbar = (SeekBar) findViewById(R.id.gseekbar);
        bvalue = (TextView) findViewById(R.id.bvalue);
        bseekbar = (SeekBar) findViewById(R.id.bseekbar);
        hexvalue = (TextView) findViewById(R.id.hexvalue);
        colorviewer = (Button) findViewById(R.id.colorviewer);

        getcolor = (Button)findViewById(R.id.getcolor);
        gethexcode = (EditText)findViewById(R.id.gethexcode);

        sendButton = (Button)findViewById(R.id.sendbutton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String rval = Integer.toString(r);
                String gval = Integer.toString(g);
                String bval = Integer.toString(b);

                while(rval.length()<3)
                {
                    rval = "0"+rval;
                }
                while(gval.length()<3)
                {
                    gval = "0"+gval;
                }
                while(bval.length()<3)
                {
                    bval = "0"+bval;
                }

                rgbString=String.valueOf(rval);
                rgbString+=gval;
                rgbString+=bval;



                Toast.makeText(MainActivity.this, "RGB values to send is...: "+rgbString, Toast.LENGTH_SHORT).show();

                try {

                    // execute HTTP request
                    if(iPAddress.length()>0 && portNumber.length()>0) {
                        new HttpRequestAsyncTask(
                                view.getContext(), rgbString, iPAddress, portNumber, "rgb"
                        ).execute();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Please set IP Address and Port number before sending data.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getHexCode();

        rseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                String s = String.valueOf(progressChanged);
                rvalue.setText(s);
                r = progressChanged;
                getHexCode();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String s = String.valueOf(progressChanged);
                rvalue.setText(s);
                r = progressChanged;
                getHexCode();

            }
        });


        gseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                String s = String.valueOf(progressChanged);
                gvalue.setText(s);
                g = progressChanged;
                getHexCode();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String s = String.valueOf(progressChanged);
                gvalue.setText(s);
                g = progressChanged;
                getHexCode();
            }
        });


        bseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                String s = String.valueOf(progressChanged);
                bvalue.setText(s);
                b = progressChanged;
                getHexCode();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String s = String.valueOf(progressChanged);
                bvalue.setText(s);
                b = progressChanged;
                getHexCode();

            }
        });

        getcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorStr = gethexcode.getText().toString();
                hex = colorStr;
                char c = colorStr.charAt(0);
                if(c == '#' && colorStr.length() >=4 && colorStr.length() <= 7) {
                    for(int i=colorStr.length();i<7;i++)
                    {
                        int x=0;
                        colorStr = colorStr + x;
                    }
                    hexvalue.setText(colorStr);
                    myColor = Color.parseColor(colorStr);
                    colorviewer.setBackgroundColor(myColor);
                    r = Integer.valueOf(colorStr.substring(1, 3), 16);
                    g = Integer.valueOf(colorStr.substring(3, 5), 16);
                    b = Integer.valueOf(colorStr.substring(5, 7), 16);
                    rseekbar.setProgress(r);
                    gseekbar.setProgress(g);
                    bseekbar.setProgress(b);
                }
                else {
                    Toast.makeText(MainActivity.this, "Enter a valid Hexcode string", Toast.LENGTH_SHORT).show();
                }


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
        if (id == R.id.wifi_settings) {

            //declaring alert dialog box and opening setup_wifi custom dialog box on action bar wifi button click
            // Create custom dialog object
            final Dialog dialog = new Dialog(MainActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.setup_wifi);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            dialog.show();

            editTextIPAddress = (EditText)dialog.findViewById(R.id.getipaddress);
            editTextPortNumber = (EditText)dialog.findViewById(R.id.getport);


            editTextIPAddress.setText(sharedPreferences.getString(PREF_IP,""));
            editTextPortNumber.setText(sharedPreferences.getString(PREF_PORT,""));

            Button setBtn = (Button) dialog.findViewById(R.id.setbtn);
            // if decline button is clicked, close the custom dialog
            setBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    iPAddress = editTextIPAddress.getText().toString().trim();
                    portNumber = editTextPortNumber.getText().toString().trim();

                    // save the IP address and port for the next time the app is used
                    editor.putString(PREF_IP,iPAddress); // set the ip address value to save
                    editor.putString(PREF_PORT,portNumber); // set the port number to save
                    editor.commit(); // save the IP and PORT

                    Toast.makeText(MainActivity.this, "ip address : "+iPAddress+" and port number : "+portNumber+" .", Toast.LENGTH_SHORT).show();

                    //close dialog
                    dialog.dismiss();}
            });

            return true;
        }
        else if (id == R.id.colorpicker_dialog) {

            //connect color picker
            colorPicker();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getHexCode()
    {
        hex = String.format("#%02X%02X%02X", r, g, b);
        hexvalue.setText(hex);
        myColor = Color.parseColor(hex);
        colorviewer.setBackgroundColor(myColor);

    }


    public void colorPicker() {

        colorPickerDialog = new ColorPickerDialog(MainActivity.this, color);
        colorPickerDialog.setAlphaSliderVisible(true);
        colorPickerDialog.setHexValueEnabled(true);
        colorPickerDialog.setTitle("Select Color");
        colorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                color = i;
                colorviewer.setBackgroundColor(color);

                String Hex=Integer.toHexString(color);

                getHex = "#";
                getHex+=Hex.substring(2).toString();

                hex = getHex;

                hexvalue.setText(getHex);

                r = Integer.valueOf(Hex.substring(2, 4), 16);
                g = Integer.valueOf(Hex.substring(4, 6), 16);
                b = Integer.valueOf(Hex.substring(6, 8), 16);
                rseekbar.setProgress(r);
                gseekbar.setProgress(g);
                bseekbar.setProgress(b);



            }
        });

        colorPickerDialog.show();

    }



    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply,ipAddress, portNumber;
        private Context context;
        private AlertDialog alertDialog;
        private String parameter;
        private String parameterValue;

        /**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         * @param context the application context, needed to create the dialog
         * @param parameterValue the hex value for rgb light
         * @param ipAddress the ip address to send the request to
         * @param portNumber the port number of the ip address
         */
        public HttpRequestAsyncTask(Context context, String parameterValue, String ipAddress, String portNumber, String parameter)
        {
            this.context = context;

            alertDialog = new AlertDialog.Builder(this.context)
                    .setTitle("HTTP Response From IP Address:")
                    .setCancelable(true)
                    .create();

            this.ipAddress = ipAddress;
            this.parameterValue = parameterValue;
            this.portNumber = portNumber;
            this.parameter = parameter;
        }

        /**
         * Name: onPreExecute
         * Description: This function is executed before the HTTP request is sent to ip address.
         * The function will set the dialog's message and display the dialog.
         */
        @Override
        protected void onPreExecute() {
            alertDialog.setMessage("Sending data to server, please wait...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }
        }

        /**
         * Name: doInBackground
         * Description: Sends the request to the ip address
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            alertDialog.setMessage("Data sent, waiting for reply from server...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }
            requestReply = sendRequest(parameterValue,ipAddress,portNumber, parameter);
            return null;
        }

        /**
         * Name: onPostExecute
         * Description: This function is executed after the HTTP request returns from the ip address.
         * The function sets the dialog's message with the reply text from the server and display the dialog
         * if it's not displayed already (in case it was closed by accident);
         * @param aVoid void parameter
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            alertDialog.setMessage(requestReply);
            if(!alertDialog.isShowing())
            {
                alertDialog.show(); // show dialog
            }
        }



    }


    /**
     * Description: Send an HTTP Get request to a specified ip address and port.
     * Also send a parameter "parameterName" with the value of "parameterValue".
     * @param parameterValue the pin number to toggle
     * @param ipAddress the ip address to send the request to
     * @param portNumber the port number of the ip address
     * @param parameterName
     * @return The ip address' reply text, or an ERROR message is it fails to receive one
     */
    public String sendRequest(String parameterValue, String ipAddress, String portNumber, String parameterName) {
        String serverResponse = "ERROR";

        try {

            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            URI website = new URI("http://"+ipAddress+":"+portNumber+"/?"+parameterName+"="+parameterValue);
            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request
            HttpResponse response = httpclient.execute(getRequest); // execute the request
            // get the ip address server's reply
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            serverResponse = in.readLine();
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return serverResponse;
    }



}
