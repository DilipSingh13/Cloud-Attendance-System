package com.dilip.cloudattendance.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dilip.cloudattendance.FilePath;
import com.dilip.cloudattendance.ImagePickerActivity;
import com.dilip.cloudattendance.R;

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

public class FacultyUploadFragment extends Fragment {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final String TAG = FacultyUploadFragment.class.getSimpleName();
    private String SERVER_URL;
    private ImageView Attachment;
    private EditText File_Name;
    Button Upload;
    private ProgressDialog dialog;
    private String upload_type,fileName,imagePath;
    Spinner Upload_Type;
    private Drawable myDrawable;

    public FacultyUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_timetable_faculty, container, false);
        Attachment = view.findViewById(R.id.chose_file);
        Upload = view.findViewById(R.id.upload);
        Upload_Type=view.findViewById(R.id.upload_type);
        File_Name=view.findViewById(R.id.file_name);

        dialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
        dialog.setCancelable(false);

        myDrawable= getActivity().getResources().getDrawable(R.drawable.timetable);
        List<String> Cour = new ArrayList<String>();
        Cour.add("Select Upload Type");
        Cour.add("Exam Duty");
        Cour.add("Regular Lecture");

        Upload_Type.setSelection(0);

        ArrayAdapter<String> UploadTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cour);
        UploadTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Upload_Type.setAdapter(UploadTypeAdapter);

        Upload_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upload_type = parent.getItemAtPosition(position).toString();
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
                fileName = File_Name.getText().toString();
                final int status =(Integer) v.getTag();
                switch (status){
                    case 0:
                    {
                        if (fileName.isEmpty()) {
                            Toast.makeText(getActivity(), "Please type filename", Toast.LENGTH_SHORT).show();
                        } else if (upload_type.equals("Select Upload Type")) {
                            Toast.makeText(getActivity(), "Please select upload type !", Toast.LENGTH_SHORT).show();
                        } else {
                            pickImage();
                        }
                        break;
                    }
                    case 1:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                             try {
                                 uploadFile(imagePath);
                             }
                                finally {
                                 imagePath=null;
                             }
                            }

                        }).start();
                        break;
                }

            }
        });


        return view;
    }

    public void pickImage() {
        Intent i=new Intent(getActivity(), ImagePickerActivity.class);
        fileName=fileName+".jpg";
        i.putExtra("filename",fileName);
        startActivityForResult(i,REQUEST_PICK_IMAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        }
    }

    private void setImage(final String imagePath) {

        getActivity().runOnUiThread(new Runnable() {
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("Uploading file...");
                dialog.show();
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
            dialog.dismiss();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Source File Doesn't Exist: ", Toast.LENGTH_SHORT).show();
                }
            });
            return 0;
        } else {
            try {
                if (upload_type.equals("Exam Duty")) {
                    SERVER_URL="**** Your SERVER_URL/FacultyExamTimetable.php";
                }
                if(upload_type.equals("Regular Lecture")){
                    SERVER_URL="**** Your SERVER_URL/FacultyLectureTimetable.php";
                }

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
                        Toast.makeText(getActivity(), "Insufficient Memory!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                }
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "File Upload completed..", Toast.LENGTH_SHORT).show();
                            Attachment.setImageDrawable(myDrawable);
                            File_Name.setText("");
                            Upload_Type.setSelection(0);
                            Upload.setTag(0);
                            Upload.setText("Select image");
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Databse Error!", Toast.LENGTH_SHORT).show();
                        Attachment.setImageDrawable(myDrawable);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }


}