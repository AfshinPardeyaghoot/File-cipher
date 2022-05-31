import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileCipherUtil {

    private Cipher cipher;
    private SecretKey secretKey;


    FileCipherUtil(SecretKey secretKey, String transformation) {
        this.secretKey = secretKey;
        try {
            this.cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            System.out.println("Exception in creating Cipher instance : " + e.getMessage());
        }
    }

    void customEncrypt(String filePath, String encryptedFilePath) throws RuntimeException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream outputStream = new FileOutputStream(encryptedFilePath);
             FileInputStream inputStream = new FileInputStream(filePath)) {

            byte[] encryptedContent = cipher.doFinal(inputStream.readAllBytes());

            outputStream.write(encryptedContent);

        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }


    String customDecrypt(String filePath) throws RuntimeException {
        String decryptedContent = null;


        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {

            byte[] fileIv = new byte[16];
            fileInputStream.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

            byte[] encryptedContent = fileInputStream.readAllBytes();
            byte[] content = cipher.doFinal(encryptedContent);
            decryptedContent = new String(content);

        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return decryptedContent;
    }


    void encrypt(String contentPath, String fileName) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            System.out.println("Exception in init Cipher " + e.getMessage());
        }


        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
             FileInputStream fileInputStream = new FileInputStream(contentPath)) {

            byte[] iv = cipher.doFinal(fileInputStream.readAllBytes());
            fileOut.write(iv);
            cipherOut.write(fileInputStream.readAllBytes());

        } catch (IOException e) {
            System.out.println("Exception encrypting file :  " + e.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    String decrypt(String fileName) {
        String content = null;

        try (FileInputStream fileIn = new FileInputStream(fileName)) {
            byte[] fileIv = new byte[16];
            fileIn.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                    InputStreamReader inputReader = new InputStreamReader(cipherIn);
                    BufferedReader reader = new BufferedReader(inputReader)
            ) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();

            } catch (IOException e) {
                System.out.println("Exception in decrypting file");
            }
        } catch (IOException | InvalidKeyException e) {
            System.out.println("Exception in decrypting file");
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        return content;
    }
}
