/**
 * Installer-Bootstrap 1.0.0-SNAPSHOT
 * Copyright (C) 2014 CodingBadgers <plugins@mcbadgercraft.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.codingbadgers.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumGenerator {

    private static final int BUFFER = 128 << 6; // 8192

    private static final String SHA_1 = "SHA-1";
    private static final String MD_5 = "MD5";

    public static String createSha1(File file) {
	    return createHash(file, SHA_1);
	}

	public static String createMD5(File file) {
	    return createHash(file, MD_5);
	}
	
	private static String createHash(File file, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return createHash(file, digest);
        } catch (IOException ex){
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
	}
	
	private static String createHash(File file, MessageDigest digest) throws IOException {
		InputStream fis = null;
	    try {
		    fis = new FileInputStream(file);
		    int n = 0;
		    byte[] buffer = new byte[BUFFER];
		    while (n != -1) {
		        n = fis.read(buffer);
		        if (n > 0) {
		            digest.update(buffer, 0, n);
		        }
		    }
	    } finally {
	    	if (fis != null) {
	    		fis.close();
	    	}
	    }
	    return bytesToHex(digest.digest());
	}
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
