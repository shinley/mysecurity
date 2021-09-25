package com.shinley.mysecurity.utils;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TotpUtil {
    private static final long TIME_STEP = 60 * 5L;
    private static final int PASSWORD_LENGTH = 6;

    private KeyGenerator keyGenerator;
    private TimeBasedOneTimePasswordGenerator totp;

    /**
     * 初始代码块， 这种初始化代码声的执行在构造函数之前
     * 准确说应该 是Java编译器会把代码块拷贝到构造函数的最开始
     */
    {
        try {
            totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(TIME_STEP), PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
            // SHA-1 and SHA-256 需要 64字节（512位）的key
            // SHA512 需要128字节（1024位）的key
            keyGenerator.init(512);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("没有找到算法{}", e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param key
     * @param time 用于生成TOTP的时间
     * @return 一次性验证码
     * @throws InvalidKeyException 非法key抛出异常
     */
    public String createTotp(Key key, Instant time) throws InvalidKeyException {
        val password  = totp.generateOneTimePassword(key, time);
        val format = "%0" + PASSWORD_LENGTH + "d";
        return String.format(format, password);
    }

    public Optional<String> createTotp(String strKey) {
        try {
            return Optional.of(createTotp(decodeKeyFromString(strKey), Instant.now()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 验证totp
     *
     * @param key
     * @param code 要验班上的totp
     * @return 是否一致
     * @throws InvalidKeyException 非法 key 抛出异常
     */
    public boolean verifyTotp(Key key, String code) throws InvalidKeyException {
        val now = Instant.now();
        return code.equals(createTotp(key, now));
    }

    /**
     * 生成 Key
     * @return
     */
    private Key generateKey() {
        return keyGenerator.generateKey();
    }

    /**
     * 把key 转成字符串
     * @param key
     * @return
     */
    private String encodeKeyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 把key转成字符串
     * 每个用户使用不同的 key, 不然相同的用户在5分钟内生成的验证码都是一样的
     * @return
     */
    public String encodeKeyToString() {
        return encodeKeyToString(generateKey());
    }

    /**
     * 把字符串转成 key
     * @param strKey
     * @return
     */
    public Key decodeKeyFromString(String strKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(strKey), totp.getAlgorithm());
    }

}
