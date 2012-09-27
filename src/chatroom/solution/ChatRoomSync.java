package chatroom.solution;

import java.util.HashMap;
import java.util.Map;

import chatroom.UserProfile;

public class ChatRoomSync {

	private Map<String, UserProfile> occupants = new HashMap<String, UserProfile>();

	public synchronized boolean joinRoom(String nickname, String userName,
			String password) {
		UserProfile userProfile = new UserProfile(userName, password);
		boolean nicknameExists = occupants.containsKey(nickname);
		if (!nicknameExists) {
			return occupants.put(nickname, userProfile) == null;
		}
		else return false;
	}

	public synchronized void changePassword(String nickname, String userName,
			String oldPassword, String newPassword) {
		UserProfile occupant;
		occupant = occupants.get(nickname);
		if (occupant != null) {
			if (occupant.getPassword().equals(oldPassword)
					&& occupant.getUserName().equals(userName)) {
				occupants.put(nickname, new UserProfile(userName, newPassword));
			}
		}
	}

	public synchronized void leaveRoom(String nickName) {
		occupants.remove(nickName);
	}
}
