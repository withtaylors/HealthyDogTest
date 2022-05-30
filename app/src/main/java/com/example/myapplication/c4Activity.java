package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class c4Activity extends AppCompatActivity {

    ImageView imageView; //촬영사진
    ImageButton picture; //촬영버튼
    Button btn2; //측정버튼
    int imageSize = 224;
    float confidences_right, confidences_left; //오른쪽 왼쪽 눈 혼탁 증상률

    int maxPos = 0; //큰 번호 값 저장
    float maxConfidence = 0; //큰 정확률 값

    String[] classes = {"혼탁 증상 확률이 높다", "혼탁 증상 확률이 낮다"};

    String result_info = ""; //혼탁 증상 확률이 높을 경우 출력되는 '수의사 측정 요망' 문구


    //앱 카메라 허용 시 사진 촬영 가능
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c4);

        imageView = findViewById(R.id.imageView);
        picture = (ImageButton)findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    //사진 촬영 후 비트맵으로 이미지 띄우기
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i<imageSize; i++){
                for(int j=0; j<imageSize; j++){
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result. Model 돌리기 및 결과 값 가져오기
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            String toastMessage = "정확도가 낮아요! 재촬영이 필요합니다.";


            //큰 값 저장하기
            for(int i =0; i<confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }


            //눈 혼탁 증상률이 높다고 판정될 경우, 전문 수의사의 진단이 필요함을 안내하는 문구
            if( maxPos == 0 ){
                result_info = "각막의 혼탁이 부분적으로 나타날 경우 지방이나 칼슘의 침착, 이전 상처에 대한 흉터일 가능성도 있어요. 전반적인 각막의 혼탁이 나타난다면 각막 부종이나 녹내장 등과 같은 질환일 수 있으니 동물병원에서 정확한 원인을 체크받길 추천해요.";
            }

            //정확도가 90% 미만일 경우 토스트 메시지 출력
            if( maxConfidence * 100 < 90 ) {
                Toast.makeText(c4Activity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }


            //측정 값(눈 혼탁률) 저장 (unHealthy_eye)
            confidences_right = confidences[0] * 100;


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }


        //측정하기 버튼 클릭했을 때 결과 값 c5로 보내주기 + 증상이 높을 경우 수의사 진단 필요함을 안내하는 'result_info' 보내주기
        String main_result , main_result_info;
        float main_confidences;

        main_result = classes[maxPos] ;
        main_result_info = result_info;
        main_confidences = confidences_right;

        Intent intent = new Intent(this, c5Activity.class);
        intent.putExtra("result",main_result);
        intent.putExtra("confidences", main_confidences);
        intent.putExtra("result_info",main_result_info);

        //측정하기 버튼 클릭했을 때 인텐트 c5 이동
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}