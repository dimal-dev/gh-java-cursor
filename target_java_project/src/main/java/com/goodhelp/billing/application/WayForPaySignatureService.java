package com.goodhelp.billing.application;

import com.goodhelp.config.GoodHelpProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Generates WayForPay signatures using HMAC-MD5.
 */
@Service
public class WayForPaySignatureService {

    private static final String HMAC_MD5 = "HmacMD5";

    private final GoodHelpProperties properties;

    public WayForPaySignatureService(GoodHelpProperties properties) {
        this.properties = properties;
    }

    public String sign(List<String> fields) {
        String secret = Objects.requireNonNull(
            properties.getWayforpay().getMerchantSecretKey(),
            "WayForPay secret key is not configured"
        );
        String payload = String.join(";", fields);
        return hmacMd5(payload, secret);
    }

    private String hmacMd5(String data, String secret) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_MD5);
            Mac mac = Mac.getInstance(HMAC_MD5);
            mac.init(keySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate WayForPay signature", e);
        }
    }
}

