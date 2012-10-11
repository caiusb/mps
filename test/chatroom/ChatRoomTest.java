package chatroom;

import static org.junit.Assert.*;

import org.junit.Test;

/*
 * Write sleep-based tests revealing the atomicity violations in the ChatRoom class. 
 * Test each method of the class.
 */

public class ChatRoomTest {

	@Test
	public void testJoin() throws Exception {
		Thread[] thread = new JoinThread[2];
		ChatRoom chat = new ChatRoom();
		for (int i = 0; i < 2; i++) {
			thread[i] = new JoinThread(chat);
			thread[i].start();
		}

		for (int i = 0; i < 2; i++)
			thread[i].join();
		
		assertEquals(1000, chat.onlineUsers());
	}
	
	@Test
	public void testChangePassword() throws Exception {
		Thread[] thread = new ChangePasswordThread[2];
		ChatRoom chat = new ChatRoom();
		
		for (int i=0; i<1000; i++)
			chat.joinRoom("user"+i, "user"+i, "pass"+i);
		
		for (int i = 0; i < 2; i++) {
			thread[i] = new ChangePasswordThread(chat);
			thread[i].start();
		}

		for (int i = 0; i < 2; i++)
			thread[i].join();
		
		assertEquals(1000, chat.onlineUsers());
	}

	private class JoinThread extends Thread {

		private ChatRoom chat;

		private JoinThread(ChatRoom chat) {
			this.chat = chat;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++)
				chat.joinRoom("user"+i, "user" + i, "pass" + i);
		}
	}
	
	private class ChangePasswordThread extends Thread {
		
		private ChatRoom chat;
		
		private ChangePasswordThread(ChatRoom chat) {
			this.chat = chat;
		}
		
		@Override
		public void run() {
			for (int i=0; i<1000; i++)
				chat.changePassword("user"+i, "user"+i, "pass"+i, "mumu"+i);
		}
	}
}
