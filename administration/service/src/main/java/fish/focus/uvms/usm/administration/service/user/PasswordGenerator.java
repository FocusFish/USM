package fish.focus.uvms.usm.administration.service.user;

import java.util.Random;

/**
 * Generates random passwords having a limited length, specific number of
 * digits, capitals and special characters.
 */
public class PasswordGenerator {

    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUM = "0123456789";
    private static final String SPL_CHARS = "!@#$%^&*_=+-/";

    public static String generatePswd(int minLen, int maxLen, int noOfCAPSAlpha,
                                      int noOfAlpha, int noOfDigits, int noOfSplChars) {
        if (minLen > maxLen)
            throw new IllegalArgumentException("Min. Length > Max. Length!");
        if ((noOfCAPSAlpha + noOfAlpha + noOfDigits + noOfSplChars) > minLen)
            throw new IllegalArgumentException
                    ("Min. Length should be at least sum of (CAPS, ALPHAS, DIGITS, SPL CHARS) Length!");
        Random rnd = new Random();
        int len = rnd.nextInt(maxLen - minLen + 1) + minLen;
        char[] pswd = new char[len];
        int index = 0;
        for (int i = 0; i < noOfCAPSAlpha; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
        }
        for (int i = 0; i < noOfAlpha; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
        }
        for (int i = 0; i < noOfDigits; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
        }
        for (int i = 0; i < noOfSplChars; i++) {
            index = getNextIndex(rnd, len, pswd);
            pswd[index] = SPL_CHARS.charAt(rnd.nextInt(SPL_CHARS.length()));
        }
        for (int i = 0; i < len; i++) {
            if (pswd[i] == 0) {
                pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
            }
        }

        return String.valueOf(pswd);
    }

    private static int getNextIndex(Random rnd, int len, char[] pswd) {
        int index = rnd.nextInt(len);
        while (pswd[index = rnd.nextInt(len)] != 0) ;
        return index;
    }

}
