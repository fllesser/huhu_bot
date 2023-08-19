package tech.chowyijiu.huhubot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author flless
 * @date 16/8/2023
 */

@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, String> data) {
        System.out.println(data.get("text"));
        return ResponseEntity.status(HttpStatus.CREATED).body("test");
    }

}
