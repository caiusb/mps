package chatroom;

import java.util.HashMap;
import java.util.Map;

/*
 Steps:

 1.	Remove the synchronization and replace the HashMap with a ConcurrentHashMap.
 2. Is this enough? Make the other necessary modifications to make it correct.
 */

public class ChatRoomConcurrent {

	private Map<String, UserProfile> occupants = new HashMap<String, UserProfile>();

	public boolean joinRoom(String nickname, String userName, String password) {
		UserProfile userProfile = new UserProfile(userName, password);
		if (!occupants.containsKey(nickname)) {
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
