package com.sinzore.printer_library_version_one_cs10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sinzore.cs10_printer_versionone.PrintActivityVersionOne;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringBuffer sb = new StringBuffer();
        sb.append("Hello Benjamin \n");
        sb.append("================================\n");
        sb.append("How are you doing? \n");
        new PrintActivityVersionOne().printBluetooth(sb);
    }
}