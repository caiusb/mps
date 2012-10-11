package chatroom.solution;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import chatroom.UserProfile;

public class ChatRoomConcurrent {

	private ConcurrentMap<String, UserProfile> occupants = new ConcurrentHashMap<String, UserProfile>();

	public boolean joinRoom(String nickname, String userName, String password) {
		return occupants.putIfAbsent(nickname, new UserProfile(userName,
				password)) == null;
	}

	public void changePassword(String nickname, String userName,
			String oldPassword, String newPassword) {
		UserProfile oldOccupant = new UserProfile(userName, oldPassword);
		UserProfile newProfile = new UserProfile(userName, newPassword);
		occupants.replace(nickname, oldOccupant, newProfile);
	}

	public void leaveRoom(String nickName) {
		occupants.remove(nickName);
	}
}
