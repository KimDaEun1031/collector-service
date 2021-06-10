package org.daeun.sboot.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "person")
public class PersonVO {
//	public static final String ID = "id";
//	public static final String NickName = "nickname";
//	public static final String Email = "email";
	
	@Id
	private String id;
	
	private String nickname;
	private String email;
	
	public PersonVO(String nickname,String email) {
		this.email = email;
		this.nickname = nickname;
	}
	

	public String getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Person [nickname=" + nickname + ", email=" + email + "]";
	}
	
	
	
}
