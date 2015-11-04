package webroot.com.webrootencrypt;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // casts view as button via (Button)
        Button button = (Button) findViewById(R.id.dummy_file_button);
        // Callback when button is clicked:
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSDCardInfo();
                final String state = Environment.getExternalStorageState();
                if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...
                    System.out.println("Encrypting...");
                    encrypt();
//                    getAllFilesOfDir(Environment.getExternalStorageDirectory());
                    System.out.println("Done...");
                }
            }
        });
    }
    private void getAllFilesOfDir(File directory) {
        System.out.println("Directory: " + directory.getAbsolutePath() + "\n");
//        System.out.println("Directory Exists? " + directory.exists());
        if(directory.canRead()) {
//            System.out.println("Directory: " + directory.getAbsolutePath() + "\n");

            File[] files = directory.listFiles();
//            System.out.println(files);
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                            getAllFilesOfDir(file);
                        } else {  // it is a file...
                            System.out.println("File: " + file.getAbsolutePath() + "\n");
                        }
                    }
                }
            }
        } else {
            System.out.println("could not get read permissions of files");
        }
    }

    private boolean checkSDCardInfo() {
        String state = Environment.getExternalStorageState();
        System.out.println(state);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            System.out.println("R/W");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            System.out.println("R");
            return true;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            System.out.println("something wrong");
            return false;
        }
    }

    private void encrypt() {
        try {
            EditText pathToEncrypt = (EditText)findViewById(R.id.pathToEncrypt);

            // Here you read the cleartext.
            FileInputStream fis = new FileInputStream(pathToEncrypt.getText().toString());
            // This stream write the encrypted text. This stream will be wrapped by another stream.
            FileOutputStream fos = new FileOutputStream("data/encrypted/sched.PNG");

            EditText password  = (EditText)findViewById(R.id.textPassword);

            // Length is 16 byte
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(192);
            Key encryptionKey = generator.generateKey();

            // Create cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);

            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];

            while((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }

            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

//    static void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
//        FileInputStream fis = new FileInputStream("data/encrypted");
//
//        FileOutputStream fos = new FileOutputStream("data/decrypted");
//        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, sks);
//        CipherInputStream cis = new CipherInputStream(fis, cipher);
//        int b;
//        byte[] d = new byte[8];
//        while((b = cis.read(d)) != -1) {
//            fos.write(d, 0, b);
//        }
//        fos.flush();
//        fos.close();
//        cis.close();
//    }

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

}
