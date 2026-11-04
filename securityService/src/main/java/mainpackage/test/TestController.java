package mainpackage.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String get() {
        return "Test greeting!";//всегда доступно
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured method";//требуется авторизация
    }

}
