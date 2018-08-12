package com.example.jaeheekim.sign_up.device;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jaeheekim.sign_up.GlobalVar;
import com.example.jaeheekim.sign_up.R;
import com.example.jaeheekim.sign_up.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    private String deviceMAC = null;
    private String deviceName = null;

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    //private EditText mOutEditText;
    private Button mHistoryButton;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    private boolean historicalOn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
        //mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mHistoryButton = (Button) view.findViewById(R.id.btnHistory);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    sendMessage("start");
                    historicalOn = true;
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    historicalOn = true;
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if(historicalOn) {
                        GlobalVar.setHistoricalData(readMessage);
                    }

                    if (GlobalVar.getFlag() && !getDeviceName().equals("device_none")) {
                        if(readMessage.contains("]")) {
                            GlobalVar.setFlag(false);
                            String url = "http://teamf-iot.calit2.net/API/transfer";
                            String values = null;
                            try {
                                if (!historicalOn) {
                                    values = makeJSONArray(readMessage);
                                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                                }else {
                                    values = makeJSONArray(GlobalVar.getHistoricalData());
                                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + GlobalVar.getHistoricalData());
                                    historicalOn = false;
                                }
                                NetworkTaskTrans networkTaskTrans = new NetworkTaskTrans(url, values);
                                networkTaskTrans.execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);

                    String url = "http://teamf-iot.calit2.net/API/sensor";
                    String value = "function=register-air&token=" + GlobalVar.getToken()
                                + "&id=" + getDeviceMAC() + "&name=" + getDeviceName()
                                + "&latitude=" + GlobalVar.getmLocation().latitude
                                + "&longitude=" + GlobalVar.getmLocation().longitude;

                    if(GlobalVar.getFlag()&&!getDeviceName().equals("device_none")){
                        GlobalVar.setFlag(false);
                        NetworkTaskSensorRegi networkTaskSensorRegi = new NetworkTaskSensorRegi(url, value);
                        networkTaskSensorRegi.execute();
                    }
                    if (null != activity) {
                        Toast.makeText(activity,
                                "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String makeJSONArray(String readMessage) throws JSONException {

        JSONArray jsonArray = new JSONArray(readMessage);

        JSONObject info = new JSONObject();
        try {
            info.put("function","transfer_air");
            info.put("token",GlobalVar.getToken());
            info.put("latitude",String.valueOf(GlobalVar.getmLocation().latitude));
            info.put("longitude",String.valueOf(GlobalVar.getmLocation().longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject info_sen = jsonArray.getJSONObject(0);

        int size = info_sen.getInt("size");
        int type = info_sen.getInt("type");

        ArrayList<JSONObject> resultArray = new ArrayList<JSONObject>();
        resultArray.add(info);
        JSONObject temp = new JSONObject();

        if(type == 1){
            JSONObject realTime = jsonArray.getJSONObject(1);

            temp.put("id", realTime.getString("MAC"));
            temp.put("CTIME", String.valueOf(realTime.getString("CTIME")));
            temp.put("TEMP", realTime.getString("TEMP"));
            temp.put("CO", realTime.getString("CO"));
            temp.put("SO2", realTime.getString("SO2"));
            temp.put("NO2", realTime.getString("NO2"));
            temp.put("O3", realTime.getString("O3"));
            temp.put("CO_AQI", realTime.getString("CO_AQI"));
            temp.put("SO2_AQI", realTime.getString("SO2_AQI"));
            temp.put("NO2_AQI", realTime.getString("NO2_AQI"));
            temp.put("O3_AQI", realTime.getString("O3_AQI"));

            resultArray.add(temp);
            GlobalVar.setRealTimeData(resultArray.toString());
        } else {
            //historicalOn = false;
            JSONArray historicalArray = new JSONArray(GlobalVar.getHistoricalData());

            JSONObject historical = new JSONObject();
            for(int i = 1; i < size+1; i++){
                historical = historicalArray.getJSONObject(i);

                temp.put("id", historical.getString("MAC"));
                temp.put("CTIME", String.valueOf(historical.getString("CTIME")));
                temp.put("TEMP", historical.getString("TEMP"));
                temp.put("CO", historical.getString("CO"));
                temp.put("SO2", historical.getString("SO2"));
                temp.put("NO2", historical.getString("NO2"));
                temp.put("O3", historical.getString("O3"));
                temp.put("CO_AQI", historical.getString("CO_AQI"));
                temp.put("SO2_AQI", historical.getString("SO2_AQI"));
                temp.put("NO2_AQI", historical.getString("NO2_AQI"));
                temp.put("O3_AQI", historical.getString("O3_AQI"));

                resultArray.add(temp);
            }
        }

        String jsonStr = resultArray.toString();

        return jsonStr;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);

        deviceMAC = device.getAddress();
        deviceName = device.getName();
    }

    public String getDeviceMAC(){
        if(deviceMAC == null){
            return "device_none";
        }else{
            return deviceMAC;
        }

    }
    public String getDeviceName(){
        if(deviceName == null){
            return "device_none";
        }else{
            return deviceName;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    // to communication with Server
    public class NetworkTaskSensorRegi extends AsyncTask<Void, Void, String> {
        private String url;                     // Server URL
        private String values;                  // Values passing to Server form Android
        //constructor
        public NetworkTaskSensorRegi(String url, String values) {
            this.url = url;
            this.values = values;
        }
        //start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;                      // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);     // get result from this "url"
            return result;
        }
        // start after done doInBackground, result will be s in this function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg;                         // msg to show to the user
            String title;                       // title of Msg
            try {
                JSONObject json_result = new JSONObject(s);             // make JSONObject to store data from the Server
                title = json_result.getString("status");                // title will be value of s's "status"
                if (title.equals("ok")) {
                    msg = "Your Sensor is registered";
                    //Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    showDialog(title, msg);
                    GlobalVar.setFlag(true);
                    return;
                } else {
                    msg = "Msg : " + json_result.getString("msg");
                    //Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    showDialog(title, msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                //Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                showDialog("Error", msg);
            }
            GlobalVar.setFlag(true);
        }
    }

    // to communication with Server
    public class NetworkTaskTrans extends AsyncTask<Void, Void, String> {
        private String url;                     // Server URL
        private String values;                  // Values passing to Server form Android
        //constructor
        public NetworkTaskTrans(String url, String values) {
            this.url = url;
            this.values = values;
        }
        //start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;                      // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);     // get result from this "url"
            return result;
        }
        // start after done doInBackground, result will be s in this function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg;                         // msg to show to the user
            String title;                       // title of Msg
            try {
                JSONObject json_result = new JSONObject(s);             // make JSONObject to store data from the Server
                title = json_result.getString("status");                // title will be value of s's "status"
                if (title.equals("ok")) {
                    msg = "Successfully passed";
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    //showDialog(title, msg);
                    GlobalVar.setFlag(true);
                    return;
                } else {
                    msg = "Msg : " + json_result.getString("msg");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    //showDialog(title, msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                //showDialog("Error", msg);
            }
            GlobalVar.setFlag(true);
        }
    }

    // use own showDialog to check user can register
    public void showDialog(final String title, String msg){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(title);
        ad.setMessage(msg);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = ad.create();
        alertDialog.show();
    }
}
