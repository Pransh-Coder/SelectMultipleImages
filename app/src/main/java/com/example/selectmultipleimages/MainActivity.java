package com.example.selectmultipleimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    List<ImagesPojo> imagesPojoList = new ArrayList<>();
    ArrayList<Uri> uriList = new ArrayList<>();

    Button btnChooser,btnUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setLayoutManager(layoutManager);

        btnChooser = findViewById(R.id.choose);
        btnUpload = findViewById(R.id.upload);

        adapter=new ImageRecyclerAdapter(this,uriList);
        recyclerView.setAdapter(adapter);

        btnChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Pictures: "), 1);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {               //getClipData()- contains an optional list of content URIs if there is more than one item to preview.
                    int count = data.getClipData().getItemCount();
                    Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < count; i++) {
                        //filling our uriList
                        uriList.add(data.getClipData().getItemAt(i).getUri());
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
                Toast.makeText(this, ""+imagePath, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public  ArrayList<Uri> getFileDataFromArrayList() {
        ArrayList<Uri> uriArrayList = new ArrayList<>();

 /*       uriArrayList.add(new Offers_Pojo("1", "Jyoti Dental Clinic", "Paldi", "50% OFF + 15% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Dhawanil Dental Clinic", "Satellite", "50% OFF + 20% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Aashray Dental Clinic", "Paldi", "50% OFF + 20% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Hardik Shah Dental Clinic", "Anjali", "200Rs. OFF + 10% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Ved Dental Clinic", "Anjali", "300Rs. OFF + 10% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Ved Dental Clinic", "Anjali", "300Rs. OFF + 10% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Ved Dental Clinic", "Anjali", "300Rs. OFF + 10% on Second Onwards"));
        uriArrayList.add(new Offers_Pojo("1", "Ved Dental Clinic", "Anjali", "300Rs. OFF + 10% on Second Onwards"));*/

        return uriArrayList;
    }

    public void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext

        //our custom volley request
        /*progressDialog.setTitle("Extracting Colors");
        progressDialog.show();*/
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://damp-wave-49064.herokuapp.com/home/10/",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        //progressDialog.dismiss();
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            Log.e("Json", obj.toString());
                            Toast.makeText(MainActivity.this, ""+response.data, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Uploading Image"+error, Toast.LENGTH_SHORT).show();
                        Log.e("Error", error.toString());
                        // progressDialog.dismiss();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("files", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);


        volleyMultipartRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

}
