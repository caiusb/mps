package chatroom;

public class UserProfile {

	private final String userName;
	private final String password;

	public UserProfile(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof UserProfile))
			return false;
		UserProfile otherUser = (UserProfile) other;
		return userName.equals(otherUser.getUserName())
				&& password.equals(otherUser.getPassword());
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + userName.hashCode();
		hash = hash * 31 + password.hashCode();
		return hash;
	}
}
