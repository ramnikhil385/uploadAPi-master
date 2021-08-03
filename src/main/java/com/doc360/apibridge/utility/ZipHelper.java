package com.doc360.apibridge.utility;

import java.io.*;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;

import java.util.zip.ZipEntry;

@Component
public class ZipHelper {
    private static int BUFFER_SIZE = 32768;

    public static void fileToZip(File file, File zipFile, int compressionLevel) throws Exception {
        zipFile.createNewFile();
        FileOutputStream fout = new FileOutputStream(zipFile);
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new BufferedOutputStream(fout));
//            zout.setLevel(compressionLevel);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++)
                    fileToZip(files[i], zout, file);
            } else if (file.isFile()) {
                fileToZip(file, zout, file.getParentFile());
            }
        } finally {
            if (zout != null)
                zout.close();
        }
    }

    private static void fileToZip(File file, ZipOutputStream zout, File baseDir) throws Exception {
        String entryName = file.getPath().substring(baseDir.getPath().length() + 1);
        if (File.separatorChar != '/')
          entryName = entryName.replace(File.separator, "/");
        if (file.isDirectory()) {
            zout.putNextEntry(new ZipEntry(entryName + "/"));
            zout.closeEntry();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
                fileToZip(files[i], zout, baseDir);
        } else {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(entryName));
                streamCopy(is, zout);
            } finally {
                zout.closeEntry();
                if (is != null)
                    is.close();
            }
        }
    }

    private static void streamCopy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }

}