package com.menghen.controller;

import com.menghen.entity.UserCookies;
import com.menghen.service.IUserCookiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class JDCookieController {

    final private IUserCookiesService cookieService;

    @PostMapping("/saveCookie")
    public void saveCookie(@RequestBody @Validated UserCookies cookieDTO) {
        log.info("saveCookie");
        cookieService.saveCookie(cookieDTO);
    }

}
