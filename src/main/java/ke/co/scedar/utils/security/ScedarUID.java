package ke.co.scedar.utils.security;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author dmutende
 * Courtesy of bobj (dhis2)
 */
public class ScedarUID {

    public static final String letters = "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String ALLOWED_CHARS = "0123456789" + letters;

    public static final int NUMBER_OF_CODE_POINTS = ALLOWED_CHARS.length();
    public static final int UID_SIZE = 15;

    private static final Pattern UID_PATTERN = Pattern.compile( "^[a-zA-Z]{1}[a-zA-Z0-9]{10}$" );

    /**
     * Generates a pseudo random string using the allowed characters. Code is
     * 15 characters long.
     *
     * @return the code.
     */
    public static String generateUid()
    {
        return generateUid(UID_SIZE);
    }

    /**
     * Generates a pseudo random string using the allowed characters.
     *
     * @param codeSize the number of characters in the code.
     * @return the code.
     */
    public static String generateUid( int codeSize )
    {
        Random sr = new Random();

        char[] randomChars = new char[codeSize];

        // First char should be a letter
        randomChars[0] = letters.charAt( sr.nextInt( letters.length() ) );

        for ( int i = 1; i < codeSize; ++i )
        {
            randomChars[i] = ALLOWED_CHARS.charAt(sr.nextInt(NUMBER_OF_CODE_POINTS));
        }

        return new String( randomChars );
    }

    /**
     * Tests whether the given code is valid.
     *
     * @param uid the code to validate.
     * @return true if the code is valid.
     */
    public static boolean isValidUid(String uid)
    {
        return uid != null && UID_PATTERN.matcher( uid ).matches();
    }

}
