package rocks.magical.camunda.database.utils;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;

public class HashUtil {
    public static String sha3(final String input) {
        final SHA3.DigestSHA3 sha3 = new SHA3.Digest256();

        sha3.update(input.getBytes());

        return hashToString(sha3);
    }

    public static String hashToString(MessageDigest hash) {
        return hashToString(hash.digest());
    }

    public static String hashToString(byte[] hash) {
        StringBuilder buff = new StringBuilder();

        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }

        return buff.toString();
    }
}
