package com.cssllc.tensorflowlitetest1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private EditText inputNumber;
    private TextView outputValue;
    private Button calculate;
    private Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNumber = findViewById(R.id.editText);
        outputValue = findViewById(R.id.outputTv);
        calculate = findViewById(R.id.button);

        try {
            tflite = new Interpreter(loadModelFile(this.getAssets(),"linear.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float result = sendToModel(Float.parseFloat(inputNumber.getText().toString()));
                outputValue.setText(String.valueOf(result));
            }
        });


    }

    private Float sendToModel(float data){

        float[] inputValue = new float[1];
        inputValue[0] = data;

        float[][] output = new float[1][1];

        tflite.run(inputValue,output);

        float inferredValue = output[0][0];

        return inferredValue;


    }

    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
