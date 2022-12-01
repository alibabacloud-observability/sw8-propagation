package skywalking;

/**
 * @author carpela <luhao.wh@alibaba-inc.com>
 * @date 2022/12/1
 */

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link ContextCarrier} is a data carrier of {@link TracingContext}. It holds the snapshot (current state) of {@link
 * TracingContext}.
 * <p>
 */
@Data
public class ContextCarrier implements Serializable {
    private String traceId;
    /**
     * The segment id of the parent.
     */
    private String traceSegmentId;
    /**
     * The span id in the parent segment.
     */
    private int spanId = -1;

    private String parentService = Constants.EMPTY_STRING;

    private String parentServiceInstance = Constants.EMPTY_STRING;
    /**
     * The endpoint(entrance URI/method signature) of the parent service.
     */

    private String parentEndpoint;
    /**
     * The network address(ip:port, hostname:port) used in the parent service to access the current service.
     */

    private String addressUsedAtClient;

    /**
     * Serialize this {@link ContextCarrier} to a {@link String}, with '|' split.
     *
     * @return the serialization string.
     */
    public String serialize(HeaderVersion version) {
        if (this.isValid(version)) {
            return StringUtil.join(
                    '-',
                    "1",
                    Base64.encode(this.getTraceId()),
                    Base64.encode(this.getTraceSegmentId()),
                    this.getSpanId() + "",
                    Base64.encode(this.getParentService()),
                    Base64.encode(this.getParentServiceInstance()),
                    Base64.encode(this.getParentEndpoint()),
                    Base64.encode(this.getAddressUsedAtClient())
            );
        }
        return "";
    }

    /**
     * Initialize fields with the given text.
     *
     * @param text carries {@link #traceSegmentId} and {@link #spanId}, with '|' split.
     */
    ContextCarrier deserialize(String text, HeaderVersion version) {
        if (text == null) {
            return this;
        }
        if (HeaderVersion.v3.equals(version)) {
            String[] parts = text.split("-", 8);
            if (parts.length == 8) {
                try {
                    // parts[0] is sample flag, always trace if header exists.
                    this.traceId = Base64.decode2UTFString(parts[1]);
                    this.traceSegmentId = Base64.decode2UTFString(parts[2]);
                    this.spanId = Integer.parseInt(parts[3]);
                    this.parentService = Base64.decode2UTFString(parts[4]);
                    this.parentServiceInstance = Base64.decode2UTFString(parts[5]);
                    this.parentEndpoint = Base64.decode2UTFString(parts[6]);
                    this.addressUsedAtClient = Base64.decode2UTFString(parts[7]);
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
        return this;
    }

    public boolean isValid() {
        return isValid(HeaderVersion.v3);
    }

    /**
     * Make sure this {@link ContextCarrier} has been initialized.
     *
     * @return true for unbroken {@link ContextCarrier} or no-initialized. Otherwise, false;
     */
    boolean isValid(HeaderVersion version) {
        if (HeaderVersion.v3 == version) {
            return StringUtil.isNotEmpty(traceId)
                    && StringUtil.isNotEmpty(traceSegmentId)
                    && getSpanId() > -1
                    && StringUtil.isNotEmpty(parentService)
                    && StringUtil.isNotEmpty(parentServiceInstance)
                    && StringUtil.isNotEmpty(parentEndpoint)
                    && StringUtil.isNotEmpty(addressUsedAtClient);
        }
        return false;
    }

    public enum HeaderVersion {
        v3
    }
}
