package kr.gdu.shop3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor 
public class ItemSetDto {
	private ItemDto item;
	private Integer quantity;
}
