package chatroom;

import java.util.HashMap;
import java.util.Map;

/**
 * This example is a part of a chat room application. The example only covers
 * two scenarios, "join chat room" and "change password".
 * 
 * The current implementation is not thread safe.
 */

public class ChatRoom {

	private Map<String, UserProfile> occupants = new HashMap<String, UserProfile>();

	public boolean joinRoom(String nickname, String userName, String password) {
		UserProfile userProfile = new UserProfile(userName, password);
		if (!occupants.containsKey(nickname)) {
			// //.....
			occupants.put(nickname, userProfile);
			return true;
		}
		return false;
	}

	public void changePassword(String nickname, String userName,
			String oldPassword, String newPassword) {
		UserProfile occupant = occupants.get(nickname);

		if (occupant != null) {
			if (occupant.getPassword().equals(oldPassword)
					&& occupant.getUserName().equals(userName)) {
				occupants.put(nickname, new UserProfile(userName, newPassword));
			}
		}
	}

	public int onlineUsers() {
		return occupants.size();
	}

	public void leaveRoom(String nickName) {
		occupants.remove(nickName);
	}
}
