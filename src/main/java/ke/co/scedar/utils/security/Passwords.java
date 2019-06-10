package ke.co.scedar.utils.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * misc (misc)
 * Created by: elon
 * On: 02 Jul, 2018 7/2/18 7:18 PM
 **/
public class Passwords {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String _PUNCTUATION = "!@#%&*()_+-=[]|,./?><";
    private static final String PUNCTUATION = "!@#&+?";
    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean usePunctuation;

    // The higher the number of iterations the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    private Passwords() {
        throw new UnsupportedOperationException("Empty constructor is not supported.");
    }

    private Passwords(PasswordBuilder builder) {
        this.useLower = builder.useLower;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.usePunctuation = builder.usePunctuation;
    }

    public static class PasswordBuilder {

        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean usePunctuation;

        public PasswordBuilder() {
            this.useLower = true;
            this.useUpper = true;
            this.useDigits = false;
            this.usePunctuation = false;
        }

        /**
         * Set true in case you would like to include lower characters
         * (abc...xyz). ScedarHttpHandler false.
         *
         * @param useLower true in case you would like to include lower
         * characters (abc...xyz). ScedarHttpHandler false.
         * @return the builder for chaining.
         */
        public PasswordBuilder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        /**
         * Set true in case you would like to include upper characters
         * (ABC...XYZ). ScedarHttpHandler false.
         *
         * @param useUpper true in case you would like to include upper
         * characters (ABC...XYZ). ScedarHttpHandler false.
         * @return the builder for chaining.
         */
        public PasswordBuilder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        /**
         * Set true in case you would like to include digit characters (123..).
         * ScedarHttpHandler false.
         *
         * @param useDigits true in case you would like to include digit
         * characters (123..). ScedarHttpHandler false.
         * @return the builder for chaining.
         */
        public PasswordBuilder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        /**
         * Set true in case you would like to include punctuation characters
         * (!@#..). ScedarHttpHandler false.
         *
         * @param usePunctuation true in case you would like to include
         * punctuation characters (!@#..). ScedarHttpHandler false.
         * @return the builder for chaining.
         */
        public PasswordBuilder usePunctuation(boolean usePunctuation) {
            this.usePunctuation = usePunctuation;
            return this;
        }

        /**
         * Get an object to use.
         *
         * @return the {@link}
         * object.
         */
        public Passwords build() {
            return new Passwords(this);
        }
    }
    /**
     * This method will generate a password depending the use* properties you
     * define. It will use the categories with a probability. It is not sure
     * that all of the defined categories will be used.
     *
     * @param length the length of the password you would like to generate.
     * @return a password that uses the categories you define when constructing
     * the object with a probability.
     */
    public String generate(int length) {
        // Argument Validation.
        if (length <= 0) {
            return "";
        }

        // Variables.
        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        // Collect the categories to use.
        List<String> charCategories = new ArrayList<>(4);
        if (useLower) {
            charCategories.add(LOWER);
        }
        if (useUpper) {
            charCategories.add(UPPER);
        }
        if (useDigits) {
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION);
        }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }

    /** Computes a salted PBKDF2 hash of given plaintext password
     suitable for storing in a database.
     Empty passwords are not supported. */
    private static String getHash(String password) {
        try{
            byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
            // store the salt with the password
            return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
        }catch (Exception e){
            return "";
        }
    }

    public static String make(String password){
        return getHash(password);
    }

    /** Checks whether given plaintext password corresponds
     to a stored salted hash of the password. */
    public static boolean check(String password, String hashed){
        String[] saltAndPass = hashed.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        try{
            String hashOfInput = hash(password, Base64.getDecoder().decode(saltAndPass[0]));
            return hashOfInput.equals(saltAndPass[1]);
        }catch (Exception e){
            return false;
        }
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private static String hash(String password, byte[] salt) {
        try{
            if (password == null || password.length() == 0)
                throw new IllegalArgumentException("Empty passwords are not supported.");
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = f.generateSecret(new PBEKeySpec(
                    password.toCharArray(), salt, iterations, desiredKeyLen)
            );
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }catch (Exception e){
            return "";
        }
    }

}
