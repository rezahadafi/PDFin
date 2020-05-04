package com.traviredev.pdfin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button button_gallery;
    private Button button_rexport;

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    public static final int PICK_IMAGE = 1;
    private LinearLayout layout;
    LinearLayout.LayoutParams layoutParams;
    ArrayList<String> arr;
    String[] valuesarr;
    LinearLayout layout_export;
    int heightscreen;
    int widthscreen;

    public static String DEST = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu);
        layout = (LinearLayout) findViewById(R.id.img_container);
        layout_export=(LinearLayout)findViewById(R.id.layout_export);
        arr=new ArrayList<String>();
        button = findViewById(R.id.button_capture);
        button_gallery=findViewById(R.id.button_gallery);
        button_rexport=findViewById(R.id.button_export);

        layout_export.setVisibility(View.INVISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightscreen = displayMetrics.heightPixels;
        widthscreen = displayMetrics.widthPixels;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
            }
        });
        button_rexport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File storageDir = getExternalFilesDir("export/");
                File image = null;
                try {
                    image = File.createTempFile("pdfinexport", ".pdf", storageDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DEST = image.getPath();
                try {
                    createPdf(DEST,"Potrait");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public void createPdf(String dest,String orientation) throws IOException, DocumentException {

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        for (String image : valuesarr) {

            Image img = Image.getInstance(image);
            float img_width=img.getWidth();
            float img_height=img.getHeight();

            if(img_height>img_width){
                //potrait
                //Log.d("orientation","potrait");
                document.setPageSize(PageSize.A4);
            }else{
                //landscape
                //Log.d("orientation","landscape");
                document.setPageSize(PageSize.A4.rotate());
            }
            document.setMargins(0f, 0f, 0f, 0f);
            float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float documentHeight = document.getPageSize().getHeight()- document.topMargin() - document.bottomMargin();
            img.scaleAbsolute(documentWidth,documentHeight);
            img.setAbsolutePosition(0, 0);
            document.newPage();
            document.add(img);
        }
        document.close();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }


    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }

}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                //Log.d("pathimage",imageFilePath);
                arr.add(imageFilePath);
                valuesarr=new String[arr.size()];
                valuesarr = arr.toArray(valuesarr);
                layout_export.setVisibility(View.VISIBLE);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (data == null) {
                //Display an error
                return;
            }

            String picturePath = getPath( this.getApplicationContext( ),data.getData() );
            //Log.d("pathimage",picturePath);
            arr.add(picturePath);
            valuesarr=new String[arr.size()];
            valuesarr = arr.toArray(valuesarr);
            layout_export.setVisibility(View.VISIBLE);
                //InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                //Bitmap bmp= BitmapFactory.decodeStream(inputStream);
                //imageView.setImageBitmap(bmp);
        }
        layout.removeAllViews();
        layoutParams = new LinearLayout.LayoutParams(widthscreen/2,heightscreen/3);
        Log.d("size",String.valueOf(arr.size()));
        for (int i = 0; i < valuesarr.length; i++) {

            layoutParams.setMargins(20, 20, 20, 20);
            layoutParams.gravity = Gravity.CENTER;
            ImageView imageView = new ImageView(this);
            //imageView.setImageURI();
            //imageView.setOnClickListener(documentImageListener);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(Uri.parse(valuesarr[i].toString()));
            layout.addView(imageView);
        }
    }

    public String getPath(Context context, Uri uri) {

        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getPath();
        return image;
    }


}

