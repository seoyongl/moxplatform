package me.polynom.moxplatform_android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashedFileOutputStream extends FileOutputStream {
    public MessageDigest digest;

    public HashedFileOutputStream(String name, String hashSpec) throws FileNotFoundException, NoSuchAlgorithmException {
        super(name);

        digest = MessageDigest.getInstance(hashSpec);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);

        digest.update(b, off, len);
    }

    public String getHexHash() {
        StringBuffer result = new StringBuffer();
        for (byte b : digest.digest()) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public byte[] getHash() {
        return digest.digest();
    }
}
