package chatroom;

import java.util.HashMap;
import java.util.Map;

/*
 This is a copy of the ChatRoom class. Identify the races/atomicity 
 violations in the code below and then fix them by using synchronization.  
 */

public class ChatRoomSync {

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
