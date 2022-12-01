package skywalking;

/**
 * @author carpela <luhao.wh@alibaba-inc.com>
 * @date 2022/12/1
 */

import java.nio.charset.StandardCharsets;

/**
 * A wrapper of {@link java.util.Base64} with convenient conversion methods between {@code byte[]} and {@code String}
 */
public final class Base64 {
    private static final java.util.Base64.Decoder DECODER = java.util.Base64.getDecoder();
    private static final java.util.Base64.Encoder ENCODER = java.util.Base64.getEncoder();

    private Base64() {
    }

    public static String decode2UTFString(String in) {
        return new String(DECODER.decode(in), StandardCharsets.UTF_8);
    }

    public static String encode(String text) {
        return ENCODER.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

}

