package mblog.extend.email;

import java.util.Map;

/**
 * @author langhsu on 2015/8/14.
 */
public interface EmailSender {

    void to(String template, String address, String subject, Map<String, Object> data);
}
