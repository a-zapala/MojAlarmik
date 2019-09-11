package com.example.mojalarmik;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataManager {
    private static final String BARCODE_FILE_NAME = "BARCODE_FILE";
    private static final String NUMBER_FILE_NAME = "NUMBER_FILE";
    private static final String URI_MUSIC = "MUSIC_FILE";

    private static void writeToFile(Context context, String string, String fileName) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(Context context, String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                int ch;
                while ((ch = fileInputStream.read()) != -1) {
                    builder.append((char) ch);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    public static void writeBarcode(Context context, String contents) {
        writeToFile(context, contents, BARCODE_FILE_NAME);
    }

    public static String readBarcode(Context context) {
        return readFromFile(context, BARCODE_FILE_NAME);
    }

    public static void writeNumber(Context context, String number) {
        writeToFile(context, number, NUMBER_FILE_NAME);
    }

    public static String readNumber(Context context) {
        return readFromFile(context, NUMBER_FILE_NAME);
    }

    public static void writeURIMusic(Context context, String number) {
        writeToFile(context, number, URI_MUSIC);
    }

    public static String readURIMusic(Context context) {
        return readFromFile(context, URI_MUSIC);
    }
}
