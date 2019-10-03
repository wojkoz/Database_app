
package model;


public class UserData {
    private final String username;
    private final String password;
    private final String ip;
    private final int port;

    public UserData(String username, String password, String ip, int port) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
    
    
}
