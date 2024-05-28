package com.example.shorturl.service;

import com.example.shorturl.model.Url;
import com.example.shorturl.repository.UrlRepository;
import io.seruco.encoding.base62.Base62;
import jakarta.persistence.EntityExistsException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final Base62 base62Instance = Base62.createInstance();
    private final UrlRepository urlRepository;

    @Transactional
    public String generateShortenUrl(String fullUrl){
        String uuid = generateShortUuid();
        Url build = Url.builder().id(uuid).url(fullUrl).build();
        urlRepository.save(build);
        return encodeDirectionId(uuid);
    }
    @Cacheable(value = "url", key = "#encodedDirectionId", cacheManager = "contentCacheManager")
    public String redirectUrl(String encodedDirectionId){
        String directionId = decodeDirectionId(encodedDirectionId);
        Url url = urlRepository.findById(directionId).orElseThrow(EntityExistsException::new);
        return url.getUrl();
    }

    public String encodeDirectionId(String directionId){
        return new String(base62Instance.encode(directionId.getBytes()));
    }

    public String decodeDirectionId(String encodedDirectionId){
        return new String(base62Instance.decode(encodedDirectionId.getBytes()));
    }

    public String generateShortUuid(){
        final String uuidString = UUID.randomUUID().toString();
        byte[] uuidStringBytes = uuidString.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes;

        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hashBytes = messageDigest.digest(uuidStringBytes);
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();
        final int BYTE_LENGTH = 4;
        IntStream.range(0,BYTE_LENGTH)
            .mapToObj(i -> String.format("%02x",hashBytes[i]))
            .forEach(sb::append);

        return sb.toString();
    }
}
