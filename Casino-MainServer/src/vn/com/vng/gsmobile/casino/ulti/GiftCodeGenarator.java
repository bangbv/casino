package vn.com.vng.gsmobile.casino.ulti;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import vn.com.vng.gsmobile.casino.entries.Gift;

public class GiftCodeGenarator {

	public static void main(String[] args) throws Exception {
		String fileName = "giftCode.txt";
		String PREFIX = "PR";
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String giftName = "test";
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		for (int i = 0; i < 10; i++) {
			StringBuilder salt = new StringBuilder();
			salt.append(PREFIX);
			Random rnd = new Random();
			while (salt.length() < 8) {
				int index = (int) (rnd.nextFloat() * SALTCHARS.length());
				salt.append(SALTCHARS.charAt(index));
			}
			String gc = salt.toString();
			// save gift code to file
			br.write(gc);
			br.write("\n");
			JsonObject j = JsonObject.create();
			j.put(Gift.GIFT_ID, gc);
			j.put(Gift.GIFT_NAME, giftName);
			j.put(Gift.GIFT_TYPE, 1);
			j.put(Gift.GIFT_VALUE, 10000);
			j.put(Gift.DAY, 0);
			j.put(Gift.EXPIRED_TIME, 0);
			JsonDocument nj = JsonDocument.create(Gift.GIFTCODE_TABLENAME + gc, j);
			Lib.getDBGame(false).getCBConnection().upsert(nj);
		}
		br.close();
		System.out.println("finish !");
	}
}
