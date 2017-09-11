package org.opensource.demo.iot.server.auth;

/**
 * 用户名和密码认证
 *
 * Created by zchen@idelan.cn on 2017/9/11.
 */
public class AuthStrategy {

    public static boolean auth(String username, byte[] password) {
        String dbUsername = "username";
        String dbPassword = "password";

        if(!username.equals(dbUsername) ||  !new String(password).equals(dbPassword)) {
            return false;
        }

        return true;
    }

}
