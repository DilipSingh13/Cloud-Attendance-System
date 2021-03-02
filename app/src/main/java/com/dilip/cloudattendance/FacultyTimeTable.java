package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.SessionManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FacultyTimeTable extends AppCompatActivity {

    PhotoView Img;
    CardView cardView;
    TextView Result_lable;
    private RequestHandler requestHandler;
    String fetch,id;
    private DatabaseHandler db;
    private ProgressBar progressBar;
    private HashMap<String, String> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_time_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Img=findViewById(R.id.image);
        cardView=findViewById(R.id.card_view);
        progressBar=findViewById(R.id.progressBar);
        Result_lable=findViewById(R.id.result_lable);

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        if (!NetConnectivityCheck.isNetworkAvailable(FacultyTimeTable.this)) {
            ViewDialogNet alert = new FacultyTimeTable.ViewDialogNet();
            alert.showDialog(FacultyTimeTable.this, "Please, enable internet connection before using this app !!");
        }

        id = user.get("id_no");
        fetch=id+".jpg";

        requestHandler = new RequestHandler() {
            @Override
            public boolean canHandleRequest(Request data) {
                return false;
            }

            @Override
            public Result load(Request data) throws IOException {
                return null;
            }
        };

        new FacultyTimeTable.DownloadImageTask((PhotoView) findViewById(R.id.image))
                .execute("**** Your SERVER_URL *****"+fetch);
    }
    private  class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        PhotoView bmImage;

        private DownloadImageTask(PhotoView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        cardView.setVisibility(View.GONE);
                        Result_lable.setText("Exam duty plan not available contact admin!");
                    }
                });
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Result_lable.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.VISIBLE);
            bmImage.setImageBitmap(result);
        }
    }
    public class ViewDialogNet {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(FacultyTimeTable.this)) {
                        ViewDialogNet alert = new FacultyTimeTable.ViewDialogNet();
                        alert.showDialog(FacultyTimeTable.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}