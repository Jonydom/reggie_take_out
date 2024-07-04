package com.itheima.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

public class PatternTest {
    @Test
    public void testPattern() {
        String pattern = "/backend/**";
        String url = "/backend/index.html";
        AntPathMatcher pathMatcher = new AntPathMatcher();
        System.out.println(pathMatcher.match(pattern, url));
    }
}
