package edu.xpu.hcp.qrcode.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

public class FileUtil {

    public static File mult2file(MultipartFile multfile){
        if(multfile == null){
            return null;
        }
        // 获取文件名
        String fileName = multfile.getOriginalFilename();
        // 获取文件后缀
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        // 用uuid作为文件名，防止生成的临时文件重复
        File file = null;
        try {

            file = File.createTempFile(UUID.randomUUID().toString(), prefix);
            // MultipartFile to File
            file = multipartToFile(multfile);
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

    /**
     * MultipartFile 转换成File
     *
     * @param multfile 原文件类型
     * @return File
     * @throws IOException
     */
    private static  File multipartToFile(MultipartFile multfile) throws IOException {
        File f = null;
        if(multfile.equals("")||multfile.getSize()<=0){
            multfile = null;
        }else{
            InputStream ins = multfile.getInputStream();
            f=new File(multfile.getOriginalFilename());
            inputStreamToFile(ins, f);
        }
        return f;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
