package com.billing.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
	@NotBlank(message = "이메일을 입력해주세요.")
	@Email(message = "이메일 형식에 맞게 입력해주세요.")
	private String email;

	@NotBlank(message = "비밀번호를 입력해 주세요")
	@Size(min = 10, message = "비밀번호는 최소 10글자 이상이어야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).+$", message = "영어 대소문자와 특수문자를 최소 1글자씩 포함해야 합니다.")
	private String password;

	@NotBlank
	@Pattern(regexp = "^(시청자|판매자)$", message = "유효하지 않은 사용자 유형입니다.")
	private String Usertype;
}
