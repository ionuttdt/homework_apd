// Duta Viorel-Ionut, 331CB \\

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.nio.charset.StandardCharsets;

import java.util.concurrent.locks.ReentrantLock; 
/**
 * Class for a space explorer.
 */
public class SpaceExplorer extends Thread {

	Integer hashCount;
	Set<Integer> discovered;
	CommunicationChannel channel;
//    ReentrantLock re = channel.rel;

	/**
	 * Creates a {@code SpaceExplorer} object.
	 * 
	 * @param hashCount
	 *            number of times that a space explorer repeats the hash operation
	 *            when decoding
	 * @param discovered
	 *            set containing the IDs of the discovered solar systems
	 * @param channel
	 *            communication channel between the space explorers and the
	 *            headquarters
	 */
	public SpaceExplorer(Integer hashCount, Set<Integer> discovered, 
		CommunicationChannel channel) {
		this.hashCount = hashCount;
		this.discovered = discovered;
		this.channel = channel;
	}


	@Override
	public void run() {

		while(true) {

			Message m1;				// pairnte
			Message m2;  			// copil

			// blochez canalul pentru a lua corect cele doua mesaje
			synchronized(channel) {
				m1 = channel.getMessageHeadQuarterChannel();
				m2 = channel.getMessageHeadQuarterChannel();
			}

			// daca nodul nu a fost inca descoperit:
			if(!discovered.contains(m2.getCurrentSolarSystem() )) {

				// decriptarea mesajului de la nodul copil
				String decrypt2 = encryptMultipleTimes(m2.getData(), hashCount);
				
				// raspunsul de ls SE pentru HQ
				Message ans = new Message(m1.getCurrentSolarSystem(), 
					m2.getCurrentSolarSystem(), decrypt2);
				channel.seChannel.add(ans);						// raspunsul de la SE
				discovered.add(m2.getCurrentSolarSystem());		// marchez nodul ca descoperit

			}
		}
	}
	
	/**
	 * Applies a hash function to a string for a given number of times (i.e.,
	 * decodes a frequency).
	 * 
	 * @param input
	 *            string to he hashed multiple times
	 * @param count
	 *            number of times that the string is hashed
	 * @return hashed string (i.e., decoded frequency)
	 */
	private String encryptMultipleTimes(String input, Integer count) {
		String hashed = input;
		for (int i = 0; i < count; ++i) {
			hashed = encryptThisString(hashed);
		}

		return hashed;
	}

	/**
	 * Applies a hash function to a string (to be used multiple times when decoding
	 * a frequency).
	 * 
	 * @param input
	 *            string to be hashed
	 * @return hashed string
	 */
	private static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// convert to string
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
