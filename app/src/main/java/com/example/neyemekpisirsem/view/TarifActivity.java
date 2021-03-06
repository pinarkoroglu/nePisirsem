package com.example.neyemekpisirsem.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neyemekpisirsem.R;
import com.example.neyemekpisirsem.model.Foods;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TarifActivity extends AppCompatActivity {
    private static Context ctx;
    private MobileServiceTable<Foods> foodTable;
    private MobileServiceList<Foods> _content;
    private MobileServiceClient mClient;
    private ProgressDialog mProgressBar;
    TextView description;
    TextView content2;
    TextView title;
    String searchedText;
    int rand_deger=0;
    private static ImageView image;
    Random rand = new Random();
    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nepisirsem_recipe);
        ctx = this.getApplicationContext();
        content2=(TextView)findViewById(R.id.content);
        title=(TextView)findViewById(R.id.titleText);
        image=(ImageView)findViewById(R.id.imageContent);
        description=(TextView)findViewById(R.id.description);
        mProgressBar=new ProgressDialog(this);

        try {

            mClient = new MobileServiceClient(
                    "https://neyemekpisirsem.azurewebsites.net",
                    this);

            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            foodTable = mClient.getTable(Foods.class);


        }catch(Exception e){
            Log.e("Error...:","Hata"+e);
        }

        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("name");
        mProgressBar.setMessage("Yemek getiriliyor..");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {

                    final MobileServiceList<Foods> result =
                            //    mUser.select("email").execute().get();
                     foodTable.where().field("name").eq(value).execute().get();
                    _content=result;
                    searchedText=result.get(0).getTag();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rand_deger = rand.nextInt(result.getTotalCount());
                           title.setText(result.get(rand_deger).getName());
                           content2.setText(result.get(rand_deger).getContent());
                           description.setText(result.get(rand_deger).getDescription());
                            LoadImageFromWebOperations(result.get(rand_deger).getPhoto());
                            mProgressBar.cancel();

                        }
                    });

                } catch (Exception e) {
                    Log.d("hata", "" + e);
                }

                return null;

            }

        }.execute();



    }


    public static Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.WHITE)
            .borderWidthDp(3)
            .cornerRadiusDp(30)
            .oval(true)
            .build();

    public static void LoadImageFromWebOperations(String url) {

        Picasso.with(ctx.getApplicationContext()).load(url).placeholder(R.color.white)
                .error(R.color.white)
                .transform(transformation)
                .resize(500,500)
                .into(image,new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {
                        Log.d("tag","BASARILI");
                    }

                    @Override
                    public void onError() {
                        Log.d("tag","BASARISIZ");

                    }
                });

    }

    public void onBackPressed(View view) {
        finish();
    }





}
