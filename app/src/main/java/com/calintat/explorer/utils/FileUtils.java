package com.calintat.explorer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.lang.Object;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.calintat.explorer.App;
import com.calintat.explorer.R;
import com.calintat.explorer.helper.DBHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileUtils {
    static DBHelper dbHelper;

   static ContentValues contentValues = new ContentValues();

    public static File copyFile(File src, File path) throws Exception {

        try {

            if (src.isDirectory()) {

                if (src.getPath().equals(path.getPath())) throw new Exception();

                File directory = createDirectory(path, src.getName());

                for (File file : src.listFiles()) copyFile(file, directory);

                return directory;
            }
            else {

                File file = new File(path, src.getName());

                FileChannel channel = new FileInputStream(src).getChannel();

                channel.transferTo(0, channel.size(), new FileOutputStream(file).getChannel());

                return file;
            }
        }
        catch (Exception e) {

            throw new Exception(String.format("Error copying %s", src.getName()));
        }
    }

    //----------------------------------------------------------------------------------------------

    public static File createDirectory(File path, String name) throws Exception {

        File directory = new File(path, name);

        if (directory.mkdirs()) return directory;

        if (directory.exists()) throw new Exception(String.format("%s already exists", name));

        throw new Exception(String.format("Error creating %s", name));
    }
    public static void del(File file, String password) throws Exception {
        dbHelper = new DBHelper(App.getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        FileInputStream inFile = new FileInputStream(file);

        // encrypted file


        // password to encrypt the file

        int i=0;
        String hh = file.getParent();
        String h = file.getName();
        String h1="";
        char[] c2 = h.toCharArray();

        while (c2[i]!='.'){
            h1+=c2[i];
            i++;
        }

   String h3 = hh+'/'+h+"x";

        FileOutputStream outFile = new FileOutputStream(h3);
        // password, iv and salt should be transferred to the other end
        // in a secure manner

        // salt is used for encoding
        // writing it to a file
        // salt should be transferred to the recipient securely
        // for decryption
        byte[] salt = {0,1,0,1,0,1,1,1};

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                256);
        SecretKey secretKey = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        // iv adds randomness to the text and just makes the mechanism more
        // secure
        // used while initializing the cipher
        // file to store the iv
       // String ivv = hh+"/keys/"+h1+".enc";
        //FileOutputStream ivOutFile = new FileOutputStream(ivv);
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        String jj ="";
        for (int l=0;l<16;l++){
            jj+=iv[l]+",";
        }
        //ivOutFile.write(iv);
        //ivOutFile.close();

        contentValues.put(dbHelper.KEY_WORD,jj);
        contentValues.put(dbHelper.VALUE_WORD,h3);
        database.insert(dbHelper.TABLE_WORDS,null,contentValues);


        //file encryption
        byte[] input = new byte[64];
        int bytesRead;

        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null)
                outFile.write(output);
        }

        byte[] output = cipher.doFinal();
        if (output != null)
            outFile.write(output);

        inFile.close();
        outFile.flush();
        outFile.close();

    }
    public static void del1(File file, String password) throws Exception {

        dbHelper = new DBHelper(App.getContext());
        byte[] salt = {0,1,0,1,0,1,1,1};
        // reading the iv
        String hh = file.getParent();
        String h = file.getName();
        String h5=hh+"/"+h;
        String h1="";
        char[] c2 = h.toCharArray();
int i;
        for (i=0;i<h.length()-1;i++){
            h1+=c2[i];

        }
        i=0;
        String hg="";
        while (c2[i]!='.'){
            hg+=c2[i];
            i++;
        }
        String h3 = hh+'/'+h1;
    /*    String hd = hh+"/keys/"+hg+".enc";

        byte[] iv = new byte[16];
        ivFis.read(iv);
        ivFis.close();
*/
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        byte[] iv = new byte[16];
        String byteKey = "";
        Cursor cursor = database.query(dbHelper.TABLE_WORDS, new String [] {dbHelper.KEY_WORD},
                dbHelper.VALUE_WORD + " = ?",new String[]{h5},null,null,null,null);
        if(cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                byteKey = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_WORD));
            }
        }
        String byteKeyV2[];
        byteKeyV2 = byteKey.split(",");

        byte iv1[]= {Byte.parseByte(byteKeyV2[1]),2,3};
       // for(int k = 0; k<16;k++)
       // iv[k] = byteKeyV2[k].getBytes();

        SecretKeyFactory factory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536,
                256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // file decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(h3);
        byte[] in = new byte[64];
        int read;
        while ((read = fis.read(in)) != -1) {
            byte[] output = cipher.update(in, 0, read);
            if (output != null)
                fos.write(output);
        }

        byte[] output = cipher.doFinal();
        if (output != null)
            fos.write(output);
        fis.close();
        fos.flush();
        fos.close();

    }
    public static File deleteFile(File file, String password, Context context) throws Exception {
        dbHelper = new DBHelper(context);


        if (file.isDirectory()) {

            for (File child : file.listFiles()) {

                deleteFile(child, password,context);
            }
        }

        del(file, password);
        file.delete();

        throw new Exception();
    }
    public static File deleteFile1(File file, String password) throws Exception {

        if (file.isDirectory()) {

            for (File child : file.listFiles()) {

                deleteFile1(child, password);

            }
        }

        del1(file, password);
        file.delete();
        throw new Exception();
    }

    public static File renameFile(File file, String name) throws Exception {

        String extension = getExtension(file.getName());

        if (!extension.isEmpty()) name += "." + extension;

        File newFile = new File(file.getParent(), name);

        if (file.renameTo(newFile)) return newFile;

        throw new Exception(String.format("Error renaming %s", file.getName()));
    }

    public static File unzip(File zip) throws Exception {

        File directory = createDirectory(zip.getParentFile(), removeExtension(zip.getName()));

        FileInputStream fileInputStream = new FileInputStream(zip);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream)) {

            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                byte[] buffer = new byte[1024];

                File file = new File(directory, zipEntry.getName());

                if (zipEntry.isDirectory()) {

                    if (!file.mkdirs()) throw new Exception("Error uncompressing");
                }
                else {

                    int count;

                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                        while ((count = zipInputStream.read(buffer)) != -1) {

                            fileOutputStream.write(buffer, 0, count);
                        }
                    }
                }
            }
        }

        return directory;
    }


    public static File getInternalStorage() {

        //returns the path to the internal storage

        return Environment.getExternalStorageDirectory();
    }

    //----------------------------------------------------------------------------------------------

    public static File getExternalStorage() {

        //returns the path to the external storage or null if it doesn't exist

        String path = System.getenv("SECONDARY_STORAGE");

        return path != null ? new File(path) : null;
    }

    public static File getPublicDirectory(String type) {

        //returns the path to the public directory of the given type

        return Environment.getExternalStoragePublicDirectory(type);
    }

    public static String getAlbum(File file) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        }
        catch (Exception e) {

            return null;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static String getArtist(File file) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }
        catch (Exception e) {

            return null;
        }
    }

    public static String getDuration(File file) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            long milliseconds = Long.parseLong(duration);

            long s = milliseconds / 1000 % 60;

            long m = milliseconds / 1000 / 60 % 60;

            long h = milliseconds / 1000 / 60 / 60 % 24;

            if (h == 0) return String.format(Locale.getDefault(), "%02d:%02d", m, s);

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        }
        catch (Exception e) {

            return null;
        }
    }

    public static String getLastModified(File file) {

        //returns the last modified date of the given file as a formatted string

        return DateFormat.format("dd MMM yyy", new Date(file.lastModified())).toString();
    }

    public static String getMimeType(File file) {

        //returns the mime type for the given file or null iff there is none

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file.getName()));
    }

    public static String getName(File file) {

        //returns the name of the file hiding extensions of known file types

        switch (FileType.getFileType(file)) {

            case DIRECTORY:
                return file.getName();

            case MISC_FILE:
                return file.getName();

            default:
                return removeExtension(file.getName());
        }
    }

    public static String getPath(File file) {

        //returns the path of the given file or null if the file is null

        return file != null ? file.getPath() : null;
    }

    public static String getSize(Context context, File file) {

        if (file.isDirectory()) {

            File[] children = getChildren(file);

            if (children == null) return null;

            return String.format("%s items", children.length);
        }
        else {

            return Formatter.formatShortFileSize(context, file.length());
        }
    }

    public static String getStorageUsage(Context context) {

        File internal = getInternalStorage();

        File external = getExternalStorage();

        long f = internal.getFreeSpace();

        long t = internal.getTotalSpace();

        if (external != null) {

            f += external.getFreeSpace();

            t += external.getTotalSpace();
        }

        String use = Formatter.formatShortFileSize(context, t - f);

        String tot = Formatter.formatShortFileSize(context, t);

        return String.format("%s used of %s", use, tot);
    }

    public static String getTitle(File file) {

        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            retriever.setDataSource(file.getPath());

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
        catch (Exception e) {

            return null;
        }
    }

    private static String getExtension(String filename) {

        //returns the file extension or an empty string iff there is no extension

        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    //----------------------------------------------------------------------------------------------

    public static String removeExtension(String filename) {

        int index = filename.lastIndexOf(".");

        return index != -1 ? filename.substring(0, index) : filename;
    }

    public static int compareDate(File file1, File file2) {

        long lastModified1 = file1.lastModified();

        long lastModified2 = file2.lastModified();

        return Long.compare(lastModified2, lastModified1);
    }

    //----------------------------------------------------------------------------------------------

    public static int compareName(File file1, File file2) {

        String name1 = file1.getName();

        String name2 = file2.getName();

        return name1.compareToIgnoreCase(name2);
    }

    public static int compareSize(File file1, File file2) {

        long length1 = file1.length();

        long length2 = file2.length();

        return Long.compare(length2, length1);
    }

    public static int getColorResource(File file) {

        switch (FileType.getFileType(file)) {

            case DIRECTORY:
                return R.color.directory;

            case MISC_FILE:
                return R.color.misc_file;

            case AUDIO:
                return R.color.audio;

            case IMAGE:
                return R.color.image;

            case VIDEO:
                return R.color.video;

            case DOC:
                return R.color.doc;

            case PPT:
                return R.color.ppt;

            case XLS:
                return R.color.xls;

            case PDF:
                return R.color.pdf;

            case TXT:
                return R.color.txt;

            case ZIP:
                return R.color.zip;

            default:
                return 0;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static int getImageResource(File file) {

        switch (FileType.getFileType(file)) {

            case DIRECTORY:
                return R.drawable.ic_directory;

            case MISC_FILE:
                return R.drawable.ic_misc_file;

            case AUDIO:
                return R.drawable.ic_audio;

            case IMAGE:
                return R.drawable.ic_image;

            case VIDEO:
                return R.drawable.ic_video;

            case DOC:
                return R.drawable.ic_doc;

            case PPT:
                return R.drawable.ic_ppt;

            case XLS:
                return R.drawable.ic_xls;

            case PDF:
                return R.drawable.ic_pdf;

            case TXT:
                return R.drawable.ic_txt;

            case ZIP:
                return R.drawable.ic_zip;

            default:
                return 0;
        }
    }

    public static boolean isStorage(File dir) {

        return dir == null || dir.equals(getInternalStorage()) || dir.equals(getExternalStorage());
    }

    //----------------------------------------------------------------------------------------------

    public static File[] getChildren(File directory) {

        if (!directory.canRead()) return null;

        return directory.listFiles(pathname -> pathname.exists() && !pathname.isHidden());
    }

    //----------------------------------------------------------------------------------------------

    public static ArrayList<File> getAudioLibrary(Context context) {

        ArrayList<File> list = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String data[] = new String[]{MediaStore.Audio.Media.DATA};

        String selection = MediaStore.Audio.Media.IS_MUSIC;

        Cursor cursor = new CursorLoader(context, uri, data, selection, null, null).loadInBackground();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));

                if (file.exists()) list.add(file);
            }

            cursor.close();
        }

        return list;
    }

    //----------------------------------------------------------------------------------------------

    public static ArrayList<File> getImageLibrary(Context context) {

        ArrayList<File> list = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String data[] = new String[]{MediaStore.Images.Media.DATA};

        Cursor cursor = new CursorLoader(context, uri, data, null, null, null).loadInBackground();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));

                if (file.exists()) list.add(file);
            }

            cursor.close();
        }

        return list;
    }

    public static ArrayList<File> getVideoLibrary(Context context) {

        ArrayList<File> list = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String data[] = new String[]{MediaStore.Video.Media.DATA};

        Cursor cursor = new CursorLoader(context, uri, data, null, null, null).loadInBackground();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));

                if (file.exists()) list.add(file);
            }

            cursor.close();
        }

        return list;
    }

    public static ArrayList<File> searchFilesName(Context context, String name) {

        ArrayList<File> list = new ArrayList<>();

        Uri uri = MediaStore.Files.getContentUri("external");

        String data[] = new String[]{MediaStore.Files.FileColumns.DATA};

        Cursor cursor = new CursorLoader(context, uri, data, null, null, null).loadInBackground();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                File file = new File(cursor.getString(cursor.getColumnIndex(data[0])));

                if (file.exists() && file.getName().startsWith(name)) list.add(file);
            }

            cursor.close();
        }

        return list;
    }

    public enum FileType {

        DIRECTORY, MISC_FILE, AUDIO, IMAGE, VIDEO, DOC, PPT, XLS, PDF, TXT, ZIP;

        public static FileType getFileType(File file) {

            if (file.isDirectory())
                return FileType.DIRECTORY;

            String mime = FileUtils.getMimeType(file);

            if (mime == null)
                return FileType.MISC_FILE;

            if (mime.startsWith("audio"))
                return FileType.AUDIO;

            if (mime.startsWith("image"))
                return FileType.IMAGE;

            if (mime.startsWith("video"))
                return FileType.VIDEO;

            if (mime.startsWith("application/ogg"))
                return FileType.AUDIO;

            if (mime.startsWith("application/msword"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-word"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.ms-powerpoint"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.ms-excel"))
                return FileType.XLS;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
                return FileType.DOC;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
                return FileType.PPT;

            if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
                return FileType.XLS;

            if (mime.startsWith("application/pdf"))
                return FileType.PDF;

            if (mime.startsWith("text"))
                return FileType.TXT;

            if (mime.startsWith("application/zip"))
                return FileType.ZIP;

            return FileType.MISC_FILE;
        }
    }
}