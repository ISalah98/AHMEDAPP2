//Main File
//Another Comment
package com.esmcegypt.ahmedapp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.esmcegypt.ahmedapp2.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class intro extends AppCompatActivity {

    TextView result, confidence;
    ImageView mohamed;
    Button picture;
    int imageSize = 224;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        picture = findViewById(R.id.button);


        //١- هنا بتنشء أكشن في حالة الضغط على زرار التقاط الصورة
        picture.setOnClickListener(new View.OnClickListener()
        {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            //٢- هنا تنفيذ الأكشن لما فعلياً يتم الضغط على الزرار
            public void onClick(View view) {

                //٣- دة كوندشن لو صلاحيات فتح الكاميرا مفعلة هتشتغل - ولو مش مفعلة بيظهرلك عايز تفتح الكاميرا ولا لا
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    //لو صلاحيات الكاميرا مفعلة هيدخل هنا ويفتح صفحة الكاميراً
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // هنا هيفتح صفحة الكاميرا ورقم ٣ اللي مكتوب مجرد "اي دي" لصفحة الكاميرا بحيث تعرف انهي صفحة اتفتحت بعدين.
                    startActivityForResult(cameraIntent, 3);
                } else {
                    // لو مفيش صلاحيات هيدخل هنا ويطلب صلاحية لتشغيل الكاميرا
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

    }


    /* 4 - Getting image from Camera Intent*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //٤- هيدخل هنا لو فعلاً الصفحة اشتلغت
        if(resultCode == RESULT_OK){

            //هنا بتقوله افتحلي الصفحة اللي كان الـ "اي دي" بتاعها رقمه تلاتة
            if(requestCode == 3){


                //٥- هنا بتاخد الصورة اللي اتصورت وتسجلها في متغير نوعه صورة
                Bitmap image = (Bitmap) data.getExtras().get("data");

                //٦-  هنا بتقص الصورة على هيئة مربع من النص
                //بتاخد عرض الصورة وبتاخد طول الصورة وبتدخلهم في فانكشن تجبلك انهي الاصغر فيهم وتسجل الرقم في متغير اسمه دايمنشن
                int dimension = Math.min(image.getWidth(), image.getHeight());

                //٧- بتستخدم مكتبة بتديها الصورة والديمنشن وهي اللي بتقص الصورة
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

                // ٨- هنا بتعرض الصورة بعد ما اتقصت على الشاشة
                //mohamed.setImageBitmap(image);

                // ٩- هنا بتعدل طول وعرض الصورة تاني لـ ٢٢٤ ف ٢٢٤ علشان المودل بياخد الطول والعرض دة
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                //١٠- هنا بتبعت الصورة للفنكشن اللي هتبدأ تدخل الصورة للمودل وتديلك النتيجة
                classifyImage(image,image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void classifyImage(Bitmap image, Bitmap imageDisplay){
        try {

            //١١- بنكريت اوبجيكت من الموديل
            Model model = Model.newInstance(getApplicationContext());

            //١٢ - بنكريت صورة افتراضية
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            //١٣- بنحجز مكان في الميموري نوعه بايت بافر
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            //١٤- جهز ارراي يتخزن فيه بكسلات
            int[] pixelValues = new int[imageSize * imageSize];

            //١٥- فك البكسلات وضافها في الارراي
            image.getPixels(pixelValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;

            //١٦- هنمسك البكسبلاات واحدة واحدة ونخزنها في البايت بافر اللي رايح للمودل
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = pixelValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            //١٧- الصورة الفاضية اللي كريتناها فوق, هنملاها بالبكسلات
            inputFeature0.loadBuffer(byteBuffer);

            //١٨- شغل الموديل وحط النتيجة بتاعته في الاوتبوتس
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //١٩- اضافة اللي خرج في ارراي
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }


            String[] classes ={"تفاحة جيدة","موز جيد","برتقالة جيدة","تفاحة غير جيدة","موز غير جيد", "برتقالة غير جيدة","أعد إلتقاط الصورة"};

            String p1 = String.format("%s: %.1f%%", classes[0], confidences[0] * 100);
            String p2 = String.format("%s: %.1f%%", classes[2], confidences[2] * 100);
            String p3 = String.format("%s: %.1f%%", classes[3], confidences[3] * 100);
            String p4 = String.format("%s: %.1f%%", classes[5], confidences[5] * 100);


            if(maxConfidence < 0.08 || maxPos == 4)
            {
                maxPos = 6;
                p1 = "---";
                p2 = "---";
                p3 = "---";
                p4 = "---";
            }

            //String[] classes = {"A","B","C","D","E","F"};

            //result.setText(classes[maxPos]);

            Intent i = new Intent(this, ScreenResult.class);
            i.putExtra("PERCENTAGEONE",p1);
            i.putExtra("PERCENTAGETWO",p2);
            i.putExtra("PERCENTAGETHREE",p3);
            i.putExtra("PERCENTAGEFOUR",p4);
            i.putExtra("IMAGE",imageDisplay);

            i.putExtra("RESULT",classes[maxPos]);
            startActivity(i);

            //confidence.setText(s);

            // Releases model resources if no longer used.
            //model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

}