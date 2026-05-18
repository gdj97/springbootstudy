package kr.gdu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import kr.gdu.dto.JoinDto;
import kr.gdu.entity.UserEntity;
import kr.gdu.repository.UserRepository;

@Service
public class LoginService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	public void joinProcess(JoinDto joinDto) {
		UserEntity data = new UserEntity();
		data.setUsername(joinDto.getUsername());
		data.setPassword(bcrypt.encode(joinDto.getPassword()));
		if(joinDto.getUsername().equals("admin")) {
			data.setRole("ROLE_ADMIN");
		} else {
			data.setRole("ROLE_USER");
		}
		userRepository.save(data);
	}
}
