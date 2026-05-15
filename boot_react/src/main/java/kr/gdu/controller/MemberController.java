package kr.gdu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.gdu.dto.MemberDto;
import kr.gdu.entity.MemberEntity;
import kr.gdu.service.MemberService;

@RestController
@RequestMapping("/member/")
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
public class MemberController {
	@Autowired
	MemberService service;
	
	@PostMapping("joinPro")
	public void joinPro(MemberDto dto) {
		service.memberInsert(new MemberEntity(dto));
	}	
}
