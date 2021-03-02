package com.dilip.cloudattendance;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UploadFeeStructureActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final String TAG = UploadFeeStructureActivity.class.getSimpleName();
    private String SERVER_URL = "**** Your SERVER_URL/UploadFeeStructure.php";
    ImageView Attachment;
    Button Upload;
    String cours,yr,img_name,imagePath;
    ProgressDialog pDialog;
    Drawable myDrawable;
    Spinner Cour,Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_fee_structure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Cour=findViewById(R.id.course);
        Year=findViewById(R.id.year);
        Attachment = findViewById(R.id.chose_file);
        Upload = findViewById(R.id.upload);

        myDrawable= getResources().getDrawable(R.drawable.fee);

        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        if (!NetConnectivityCheck.isNetworkAvailable(UploadFeeStructureActivity.this)) {
            ViewDialogNet alert = new UploadFeeStructureActivity.ViewDialogNet();
            alert.showDialog(UploadFeeStructureActivity.this, "Please, enable internet connection before using this app !!");
        }

        List<String> Course = new ArrayList<String>();
        Course.add("Select Course");
        Course.add("B.Tech CTIS");
        Course.add("B.Tech MACT");
        Course.add("B.Tech ITDS");
        Course.add("B.C.A CTIS");
        Course.add("B.C.A MACT");
        Course.add("B.C.A ITDS");

        final List<String> year = new ArrayList<String>();
        year.add("Select Semester");
        year.add("I");
        year.add("II");
        year.add("III");
        year.add("IV");
        year.add("V");
        year.add("VI");
        year.add("VII");
        year.add("VIII");

        Cour.setSelection(0);
        Year.setSelection(0);

        ArrayAdapter<String> CourseAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Course);
        ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, year);

        CourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Cour.setAdapter(CourseAdapter);
        Year.setAdapter(YearAdapter);

        Cour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cours = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Upload.setTag(0);
        Upload.setText("Select image");

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int status =(Integer) v.getTag();
                switch (status) {
                    case 0: {
                        if (cours.contains("Select Course")) {
                            Toast.makeText(getApplicationContext(), "Please select course !", Toast.LENGTH_SHORT).show();
                        } else if (yr.contains("Select Year")) {
                            Toast.makeText(getApplicationContext(), "Please select year !", Toast.LENGTH_SHORT).show();
                        } else {
                            pickImage();
                        }
                        break;
                    }
                    case 1: {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    uploadFile(imagePath);
                                } finally {

                                }
                            }

                        }).start();
                        break;
                    }
                }
            }
        });
    }

    public void pickImage() {
        Intent i=new Intent(getApplicationContext(), ImagePickerActivity.class);
        img_name=cours+"_"+yr+".jpg";
        i.putExtra("filename",img_name);
        startActivityForResult(i,REQUEST_PICK_IMAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        }
    }
    private void setImage(final String imagePath) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Attachment.setImageBitmap(getImageFromStorage(imagePath));
            }
        });
        Upload.setTag(1);
        Upload.setText("Upload Now");
    }

    private Bitmap getImageFromStorage(String path) {

        try {

            File f = new File(path);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 512, 512);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return b;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        return null;
    }

    private int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height;
            final int halfWidth = width;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    imagePath = data.getStringExtra("image_path");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(imagePath);
                        }
                    }).start();
                    break;
            }
        } else {
            System.out.println("Failed to load image");
        }
    }

    public int uploadFile(final String selectedFilePath) {
        int serverResponseCode = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.setMessage("Uploading file...");
                pDialog.show();
            }
        });

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Source File Doesn't Exist: ", Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {

                    try {

                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(getApplicationContext(), "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                try{
                    serverResponseCode = connection.getResponseCode();
                }catch (OutOfMemoryError e){
                    Toast.makeText(getApplicationContext(), "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                }
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File Upload completed..", Toast.LENGTH_SHORT).show();
                            Attachment.setImageDrawable(myDrawable);
                            Upload.setTag(0);
                            Upload.setText("Select image");
                            Cour.setSelection(0);
                            Year.setSelection(0);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Databse Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            pDialog.dismiss();
            return serverResponseCode;
        }
    }

    public class ViewDialogNet {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(UploadFeeStructureActivity.this)) {
                        ViewDialogNet alert = new UploadFeeStructureActivity.ViewDialogNet();
                        alert.showDialog(UploadFeeStructureActivity.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}
