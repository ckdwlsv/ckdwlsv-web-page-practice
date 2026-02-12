package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 이 클래스가 Spring mvc의 컨트롤러임을 나타냄(요청을 처리하고 뷰를 반환)
public class MainController {
    @GetMapping("/")//루트 경로("/")에 대한 GET요청을 처리
    public String main(){
        return "index";//"index.html" 뷰 페이지를 반환(src/main/resources/templates/index.html)
    }
}