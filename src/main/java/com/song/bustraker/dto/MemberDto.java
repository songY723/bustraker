package com.song.bustraker.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "members")
public class MemberDto {

    @Id
    private String id;

    private String username;   // 아이디
    private String password;   // 비밀번호
    private String phone;      // 휴대폰 번호
    private String telegramId; // 선택사항 (알림용)

    public MemberDto() {}

    public MemberDto(String username, String password, String phone, String telegramId) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.telegramId = telegramId;
    }

    // Getter / Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTelegramId() { return telegramId; }
    public void setTelegramId(String telegramId) { this.telegramId = telegramId; }
}
