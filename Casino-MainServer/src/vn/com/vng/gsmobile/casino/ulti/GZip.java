package vn.com.vng.gsmobile.casino.ulti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {

	/**
	 * @param args
	 */
	public static byte[] compress(byte[] str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(str.length);
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        try {
            gzip.write(str);
        } finally {
            gzip.close();
        }
        out.close();
        byte ret[] = out.toByteArray();
        return ret;
     }

    public static byte[] decompress(byte[] str) throws IOException {
        byte[] buf = new byte[str.length*10];
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
        int len;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((len = gis.read(buf)) > 0) { 
            out.write(buf, 0, len);
        }
        return out.toByteArray();
     }
 }
