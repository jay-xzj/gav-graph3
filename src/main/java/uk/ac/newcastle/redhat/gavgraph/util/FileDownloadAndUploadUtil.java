package uk.ac.newcastle.redhat.gavgraph.util;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FileDownloadAndUploadUtil {

    public static String download(File file, HttpServletResponse response) throws IOException {
        byte[] buffer = new byte[1024];
        try(FileInputStream fis =  new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            /* stream sent to client*/OutputStream os = response.getOutputStream()) {
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, buffer.length);
                os.flush();
                i = bis.read(buffer);
            }
            return "succeeded";
        }catch (Exception e) {
            System.out.println("Download  failed!");
            return "failed";
        }

    }

    public static String upload(String uploadDir,MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File targetFile = new File(uploadDir +File.separator+ fileName);

        if(!targetFile.getParentFile().exists()){
            targetFile.getParentFile().mkdir();
        }
        try(InputStream is = file.getInputStream();
            OutputStream os =  new FileOutputStream(targetFile)){
            FileCopyUtils.copy(is,os);
            return "upload successfully!";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "upload failure";
    }
}