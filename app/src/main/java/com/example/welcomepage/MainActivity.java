package com.example.welcomepage;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //RecyclerView recyclerFeed;
    ViewPager2 viewPager2;
    ArrayList<ViewModel> viewModels;
    ViewAdapter viewAdapter;

    Button choose,upload;
    Button chooseNep,uploadNep;
    EditText nameImage,nameImageNep;
    ImageView imgResult,imgResultNep;
    TextView uriImage,uriImageNep;

    public static final int SELECTED = 100;
    public static final int SELECTEDNEP = 200;
    Uri imageURL,imageURLNep;
    FirebaseStorage storage;
    StorageReference imageref, storageref;
    ProgressDialog progressDialog, progressDialogNep;
    UploadTask uploadTask, uploadTaskNep;
    private String fileName, fileNameNep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewpager);
        viewModels = new ArrayList<>();
        viewAdapter = new ViewAdapter(viewModels, viewPager2);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(viewAdapter);
        //viewPager2.setAdapter(new ViewAdapter(viewModels,viewPager2));
        //viewPager2.setAdapter(new ViewAdapter(this));

        //viewPager2.setClipToPadding(false);
        //viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        fetchFeedimage();


        /*CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(0));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull @NotNull View page, float position) {

                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r* 0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);*/


        choose = findViewById(R.id.choose);
        upload = findViewById(R.id.upload);
        nameImage = findViewById(R.id.nameImage);
        imgResult = findViewById(R.id.imgResult);
        uriImage = findViewById(R.id.uriImage);

        chooseNep = findViewById(R.id.chooseNep);
        uploadNep= findViewById(R.id.uploadNep);
        nameImageNep = findViewById(R.id. nameImageNep);
        imgResultNep = findViewById(R.id.imgResultNep);
        uriImageNep = findViewById(R.id.uriImageNep);

        storage = FirebaseStorage.getInstance();
        storageref = storage.getReference();

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker = new Intent(Intent.ACTION_PICK);
                photopicker.setType("image/*");
                startActivityForResult(photopicker, SELECTED);
            }
        });

        nameImage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                upload.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UploadImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



       /* chooseNep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopickerNep = new Intent(Intent.ACTION_PICK);
                photopickerNep.setType("image/*");
                startActivityForResult(photopickerNep, SELECTEDNEP);
            }
        });*/
        chooseNep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopickerNep = new Intent(Intent.ACTION_PICK);
                photopickerNep.setType("image/*");
                startActivityForResult(photopickerNep, SELECTEDNEP);

            }
        });

        nameImageNep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                upload.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        uploadNep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UploadImageNep();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECTED:
                if (resultCode==RESULT_OK){
                    assert data != null;
                    imageURLNep = data.getData();
                    String name[]= imageURLNep.toString().split("/");
                    fileNameNep=name[(name.length-1)];
                    *//*try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURL);
                        //imgResult.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();*//*
                    }
                }
    }*/

    private void UploadImageNep() throws IOException{

        //imageref = storageref.child("Images/"+nameImage.getText().toString()+"."+GetExtension(imageURL));
        imageref= storageref.child("ImagesNep/" + fileNameNep + "." + GetExtensionNep(imageURLNep));
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),  imageURLNep);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        progressDialogNep = new ProgressDialog(this);
        progressDialogNep.setMax(100);
        progressDialogNep.setMessage("UPLOADING........");
        progressDialogNep.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialogNep.show();
        progressDialogNep.setCancelable(false);
          uploadTaskNep = imageref.putBytes(data);
        uploadTaskNep .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                  double progress = (100.0 * snapshot.getBytesTransferred())/
                  snapshot.getTotalByteCount();
                  progressDialogNep.incrementProgressBy((int) progress);
              }
          });

        uploadTaskNep.addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull @NotNull Exception e) {
                  Toast.makeText(MainActivity.this, "Failed !!!!", Toast.LENGTH_SHORT).show();
                  progressDialogNep.dismiss();
              }
          }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Toast.makeText(MainActivity.this, "Mozz Garrr Bhai", Toast.LENGTH_SHORT).show();
                  progressDialogNep.dismiss();
                  //Uri downloaddUri = taskSnapshot.getDownload
                  /*Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                  result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          String photoStringLink = uri.toString();
                          uriImage.setText(photoStringLink);

                      }
                  });*/
                  Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                  result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          String photoStringLinkNep = uri.toString();
                          uriImageNep.setText(photoStringLinkNep);

                      }
                  });
              }
          });


    }

    private String GetExtensionNep(Uri imageURLNep) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageURLNep));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case SELECTED:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    imageURL = data.getData();
                    String name[] = imageURL.toString().split("/");
                    fileName = name[(name.length - 1)];

                    /*imageURLNep = data.getData();
                    String nameNep[] = imageURLNep.toString().split("/");
                    fileNameNep = nameNep[(nameNep.length - 1)];
*/
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURL);
                        imgResult.setImageBitmap(bitmap);
                       /*
                        Bitmap bitmapNep = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURLNep );
                        imgResultNep.setImageBitmap(bitmapNep);*/

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }


                }
                break;

            case SELECTEDNEP:

                if (resultCode == RESULT_OK) {
                    /*assert data != null;
                    imageURL = data.getData();
                    String name[] = imageURL.toString().split("/");
                    fileName = name[(name.length - 1)];*/

                    imageURLNep = data.getData();
                    String nameNep[] = imageURLNep.toString().split("/");
                    fileNameNep = nameNep[(nameNep.length - 1)];
                    try {
                        /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURL);
                        imgResult.setImageBitmap(bitmap);*/
                        Bitmap bitmapNep = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURLNep );
                        imgResultNep.setImageBitmap(bitmapNep);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }


                }

        }
    }

    private void UploadImage()throws IOException{

        //imageref = storageref.child("Images/"+nameImage.getText().toString()+"."+GetExtension(imageURL));
        imageref= storageref.child("Images/" + fileName + "." + GetExtension(imageURL));
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),  imageURL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("UPLOADING........");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        uploadTask = imageref.putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred())/
                        snapshot.getTotalByteCount();
                progressDialog.incrementProgressBy((int) progress);
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed !!!!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "Mozz Garrr Bhai", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //Uri downloaddUri = taskSnapshot.getDownload
                  /*Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                  result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          String photoStringLink = uri.toString();
                          uriImage.setText(photoStringLink);

                      }
                  });*/
                Task<Uri> resulten = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                resulten.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoStringLink = uri.toString();
                        uriImage.setText(photoStringLink);

                    }
                });
            }
        });


    }

    private String GetExtension(Uri imageURL) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageURL));
    }
















    private void fetchFeedimage() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("MainFeed").child("FeedImages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue() != null) {

                        Log.v("BeautyPage", "ok " + Objects.requireNonNull(dataSnapshot.getValue()).toString());
                        List<ViewModel> feedList = new ArrayList<>();
                        //feedList.clear();

                        for (DataSnapshot feedSnapshot : dataSnapshot.getChildren()) {
                            ViewModel feedItem = feedSnapshot.getValue(ViewModel.class);
                            feedList.add(feedItem);
                        }


                        viewModels.clear();
                        viewModels.addAll(feedList);

                        // refreshing recycler view
                        viewAdapter.notifyDataSetChanged();

                    }  //todo
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

                //  Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}