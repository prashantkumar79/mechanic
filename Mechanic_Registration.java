package com.samantpc.mechno;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Mechanic_Registration extends Fragment {
Button Choose,Create,Upload;
CheckBox Bike,Car,Truck;
    EditText Name,ShopName,Phone,Address;
    String NAME,SHOPNAME,PHONE,SERVICES,ADDRESS;
    ImageView imageView;
    String url="https://samanttanwar98.000webhostapp.com/registerMechanic.php";
    RequestQueue queue;
    ImageButton camera,gallery;
    Uri filePath;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Dialog dialog;




    public Mechanic_Registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_mechanic__registration, container, false);
       databaseReference= FirebaseDatabase.getInstance().getReference(Refrences.DATABASE_REFRENCE);
        storageReference= FirebaseStorage.getInstance().getReference(Refrences.STORAGE_REFRENCE);


        Create=view.findViewById(R.id.button);
        Bike=view.findViewById(R.id.checkBox);
        Car=view.findViewById(R.id.checkBox4);
        Truck=view.findViewById(R.id.checkBox3);
        Name=view.findViewById(R.id.editText4);
        ShopName=view.findViewById(R.id.editText7);

        queue= Volley.newRequestQueue(getActivity());
         Choose=view.findViewById(R.id.button2);
        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.choose_camorgalley);
                Phone=dialog.findViewById(R.id.editText3);
                Address=dialog.findViewById(R.id.address);
               camera= dialog.findViewById(R.id.imageButton);
                gallery=dialog.findViewById(R.id.imageButton2);
                Upload=dialog.findViewById(R.id.upload);
                imageView=dialog.findViewById(R.id.imageView12);
        camera.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent,0);


            }
});
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                         Intent intent2=new Intent();
                            intent2.setType("image/*");
                        intent2.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent2,1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    }
                });
                Upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


Upload();

                    }
                });
                dialog.show();
            }
        });
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAME=Name.getText().toString();
                SHOPNAME=ShopName.getText().toString();

                String check1="",check2="",check3="";
                if(Bike.isChecked()){
                    check1=Bike.getText().toString();
                }
                if (Car.isChecked()){
                    check2=Car.getText().toString();

                }
                if (Truck.isChecked()){
                    check3=Truck.getText().toString();
                }
                SERVICES=" "+check1+" "+check2+" "+check3;
                StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), ""+response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                     Map<String,String> parms=new HashMap<String, String>();
                        parms.put("name", NAME.trim());
                        parms.put("shopname", SHOPNAME.trim());
                        parms.put("service", SERVICES.trim());
                        parms.put("phone",PHONE.trim());



                        return parms;



                    }
                };
                queue.add(request);


            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK && data!=null) {

            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
            Toast.makeText(getActivity(), "Picture Selected", Toast.LENGTH_SHORT).show();

        }
        else if(resultCode==RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Please Click Picture or Select from Gallery", Toast.LENGTH_SHORT).show();
        }


        if(requestCode==1 && resultCode == RESULT_OK && data!=null){
            filePath=data.getData();
            imageView.setImageURI(filePath);
            super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(getActivity(), "Picture Selected", Toast.LENGTH_SHORT).show();
        }else if(resultCode==RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Please Click Picture or Select from Gallery", Toast.LENGTH_SHORT).show();
        }




    }
    public void Upload(){

        if(filePath!=null){

            final ProgressDialog progressDialog=new ProgressDialog(getActivity());

            progressDialog.setMessage("Uploading....");

            progressDialog.show();

            StorageReference storageReference1=   storageReference.child(Refrences.STORAGE_REFRENCE+System.currentTimeMillis()+"."+getFileExtension(filePath));

            storageReference1.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();

dialog.dismiss();
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                    String image=taskSnapshot.getDownloadUrl().toString();

                    String name=ShopName.getText().toString();
                    PHONE=Phone.getText().toString();
                    ADDRESS=Address.getText().toString();
                    Upload upload=new Upload(name,image,PHONE,ADDRESS);
                    String uploadId=databaseReference.push().getKey();
                    databaseReference.child(name).setValue(upload);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();




                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();


                    progressDialog.setMessage("Uploading... "+((int) progress ) + "%...");

                }
            });


        }else {
            Toast.makeText(getActivity(), "Please Select Picture...", Toast.LENGTH_SHORT).show();
        }

    }
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton(); //MimeTypeMap used to store design pattern
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



}


/*


         */


 