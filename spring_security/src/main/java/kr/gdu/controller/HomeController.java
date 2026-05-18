package kr.gdu.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import kr.gdu.dto.JoinDto;
import kr.gdu.service.LoginService;

@Controller
public class HomeController {
	@Autowired
	private LoginService loginService;
	@GetMapping("/")
	public String root(Model model) {
		model.addAttribute("msg","/로 요청");
		return "home";
	}
	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("msg","/home로 요청");
		return "home";
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@GetMapping("/join")
	public String join () {
		return "join";
	}
	@PostMapping("/joinProc")
	public String joinProc(JoinDto joinDto) {
		System.out.println(joinDto);
		loginService.joinProcess(joinDto);
		return "redirect:/login";
	}		
	@GetMapping("/my")
	public String my(Model model) {
	    String id = 
	   		SecurityContextHolder.getContext().getAuthentication().getName();
	    Authentication authentication = 
	    		SecurityContextHolder.getContext().getAuthentication();
	    Collection<? extends GrantedAuthority> authorities = 
	    		authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iter = authorities.iterator();
		GrantedAuthority auth = iter.next();
		String role = auth.getAuthority();
		model.addAttribute("msg","/my로 접근: id=" + id + ",role="+role);
		return "home";
	}
}
