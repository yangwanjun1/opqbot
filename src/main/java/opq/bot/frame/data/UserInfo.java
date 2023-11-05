package opq.bot.frame.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
    private final long userId;
    private final String username;

    public UserInfo(long userId,String username) {
        this.userId =userId;
        this.username = username;
    }
}
