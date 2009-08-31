package org.agilos.zendesk_jira_plugin.integrationtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class AttachmentComparator {
	private final static int BUFFSIZE = 1024;
	private static byte buff1[] = new byte[BUFFSIZE];
	private static byte buff2[] = new byte[BUFFSIZE];

	public static boolean inputStreamEquals(InputStream is1, InputStream is2) {
		if(is1 == is2) return true;
		if(is1 == null && is2 == null) return true;
		if(is1 == null || is2 == null) return false;
		try {
			int read1 = -1;
			int read2 = -1;

			do {
				int offset1 = 0;
				while (offset1 < BUFFSIZE
               				&& (read1 = is1.read(buff1, offset1, BUFFSIZE-offset1)) >= 0) {
            				offset1 += read1;
        			}

				int offset2 = 0;
				while (offset2 < BUFFSIZE
               				&& (read2 = is2.read(buff2, offset2, BUFFSIZE-offset2)) >= 0) {
            				offset2 += read2;
        			}
				if(offset1 != offset2) return false;
				if(offset1 != BUFFSIZE) {
					Arrays.fill(buff1, offset1, BUFFSIZE, (byte)0);
					Arrays.fill(buff2, offset2, BUFFSIZE, (byte)0);
				}
				if(!Arrays.equals(buff1, buff2)) return false;
			} while(read1 >= 0 && read2 >= 0);
			if(read1 < 0 && read2 < 0) return true;	// both at EOF
			return false;

		} catch (Exception ei) {
			return false;
		}
	}

	public static boolean fileContentsEquals(File file1, File file2) {
		InputStream is1 = null;
		InputStream is2 = null;
		if(file1.length() != file2.length()) return false;

		try {
			is1 = new FileInputStream(file1);
			is2 = new FileInputStream(file2);

			return inputStreamEquals(is1, is2);

		} catch (Exception ei) {
			return false;
		} finally {
			try {
				if(is1 != null) is1.close();
				if(is2 != null) is2.close();
			} catch (Exception ei2) {}
		}
	}

	public static boolean fileContentsEquals(String fn1, String fn2) {
		return fileContentsEquals(new File(fn1), new File(fn2));
	}

}
