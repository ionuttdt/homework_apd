// Duta Viorel-Ionut, 331CB \\

/**
 * Class that implements the channel used by headquarters 
 and space explorers to communicate.
 */
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.*;

public class CommunicationChannel {

/* 
	Variabila folosita pentru a asigura punerea corecta a 
mesajelor de la HeadQuarter (parinte urmat de copil).
*/
	//boolean child = false;
	ReentrantLock lock = new ReentrantLock();
	ReentrantLock lockSE = new ReentrantLock();	

	// spatiu de stocare pentru mesajele de la SpaceExplorer
	static ArrayBlockingQueue<Message> seChannel 
            = new ArrayBlockingQueue<Message>(5000);

	// spatiu de stocare pentru mesajele de la HeadQuarter
	static ArrayBlockingQueue<Message> hqChannel 
            = new ArrayBlockingQueue<Message>(5000);


    Semaphore semaphore1 = new Semaphore(1);

	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	public CommunicationChannel() {
	}

	/**
	 * Puts a message on the space explorer channel 
	 (i.e., where space explorers write to and 
	 * headquarters read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageSpaceExplorerChannel(Message message) {
		try {
			seChannel.put(message);
		}
		catch (Exception e) {

		}
	//	lockSE.unlock();

	}

	/**
	 * Gets a message from the space explorer channel 
	 (i.e., where space explorers write to and
	 * headquarters read from).
	 * 
	 * @return message from the space explorer channel
	 */
	public Message getMessageSpaceExplorerChannel() {
		Message ans = null;

		try {
			ans = seChannel.take();
		}
		catch (Exception e) {
			
		}
		return ans;
	}

	/**
	 * Puts a message on the headquarters channel (i.e.,
	  where headquarters write to and 
	 * space explorers read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageHeadQuarterChannel(Message message) {

		boolean child = false;

		if(message.getData().equals("END")) {
		}
		else {
			try {
				if(!child) {
					lock.lock();		
					hqChannel.put(message);
					child = true;
				}
				else { 
					hqChannel.put(message);
					lock.unlock();		
					child = false;
				}
			}
			catch (Exception e) {
			}
		}

	}

	/**
	 * Gets a message from the headquarters channel (i.e.,
	  where headquarters write to and
	 * space explorer read from).
	 * 
	 * @return message from the header quarter channel
	 */
	public Message getMessageHeadQuarterChannel() {

		try {
			return hqChannel.take();
		}
		catch (Exception e) {
		}

		return null;
	}

}

