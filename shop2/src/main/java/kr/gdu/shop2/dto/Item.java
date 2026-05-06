package kr.gdu.shop2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Item {
	private int id;
	//null 또는 공백인 경우 오류 검증
	@NotEmpty(message="상품명을 입력하세요")	
	private String name;
	@Min(value=10,message="10원 이상 가능합니다.")
	@Max(value=100000,message="10만원 이하만 가능합니다.")
	private int price;
	@NotEmpty(message="상품설명을 입력하세요")	
	private String description;
	private String pictureUrl;
	 //<input type="file" name="picture"> 에서 선택된 파일 정보 저장
	private MultipartFile picture;
}
