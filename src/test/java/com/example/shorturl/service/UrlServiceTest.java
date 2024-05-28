package com.example.shorturl.service;

import io.seruco.encoding.base62.Base62;
import org.junit.jupiter.api.Test;

class UrlServiceTest {

    private final Base62 base62 = Base62.createInstance();

    @Test
    void urlEncoding() {
        String a = "https://recordsoflife.tistory.com/331";
        String s = new String(base62.encode(a.getBytes()));
        System.out.println(s);
    }
}