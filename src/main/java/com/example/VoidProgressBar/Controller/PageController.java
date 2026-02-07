package com.example.VoidProgressBar.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  // ブラウザで http://localhost:8080/void にアクセスした時の処理
  @GetMapping("/")
  public String showVoidPage() {
    // resources/templates/void.html を表示しなさい、という意味
    return "void";
  }
}
