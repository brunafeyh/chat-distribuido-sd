package common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String CHARSET = "UTF-8";

    private static SecretKey encryptionKey;
    private static final String KEY_FILE_PATH = "encryptionKey.key";

    // Gerar a chave AES de 128 bits e salvar no arquivo
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);  // 128 bits
        SecretKey key = keyGenerator.generateKey();

        // Salvar a chave no disco
        try (ObjectOutputStream keyOut = new ObjectOutputStream(new FileOutputStream(KEY_FILE_PATH))) {
            keyOut.writeObject(key);
        }

        return key;
    }

    // Recuperar a chave do arquivo, se existir
    public static SecretKey getEncryptionKey() throws Exception {
        if (encryptionKey == null) {
            File keyFile = new File(KEY_FILE_PATH);
            if (keyFile.exists()) {
                // Recuperar a chave salva no disco
                try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(KEY_FILE_PATH))) {
                    encryptionKey = (SecretKey) keyIn.readObject();
                }
            } else {
                encryptionKey = generateKey();  // Se não encontrar a chave, gera uma nova e salva
            }
        }
        return encryptionKey;
    }

    // Criptografar uma mensagem com a chave secreta
    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes(CHARSET));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Descriptografar uma mensagem com a chave secreta
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData, CHARSET);
    }

    // Converter a chave secreta para um formato legível (base64)
    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Converter uma chave secreta de formato legível (base64) para a chave real
    public static SecretKey decodeKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
}
