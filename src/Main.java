import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main {

    public static void main(String[] args) {
        byte[] key = "1231231231231231".getBytes();
        SecretKey secretKey = new SecretKeySpec(key, "AES");

        String filePath = "/home/afshinpy/number.txt";
        String contentFilePath = "/home/afshinpy/test.txt";



        FileCipherUtil fileCipherUtil = new FileCipherUtil(secretKey, "AES/CBC/PKCS5Padding");

        fileCipherUtil.customEncrypt(contentFilePath, filePath);
        String content = fileCipherUtil.customDecrypt(filePath);
        System.out.println(content);
    }
}
