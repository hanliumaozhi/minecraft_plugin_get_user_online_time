package liusong.minecraftPlugin;

/**
 * Created by han on 14-10-12.
 */

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Hashtable;
import java.text.DateFormat;
import java.util.Date;


public class PlayerListener implements Listener {
    private final pluginMain plugin;
    private Hashtable<String, String> player_dict = new Hashtable<String, String>();
    private DateFormat d1 = DateFormat.getDateTimeInstance();

    public static final String url = "http://www.liusong.me/update_mc_online_time";

    public PlayerListener(pluginMain instance) {
        plugin = instance;
    }

    public static String sendPost(String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String player_name = event.getPlayer().getName();
        plugin.getLogger().info(player_name + " joined the server! :D");
        String tmp = player_dict.get(player_name);
        if (tmp == null){
            Date now = new Date();
            String time_str = d1.format(now);
            player_dict.put(player_name, time_str);

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws ParseException, IOException {
        String player_name = event.getPlayer().getName();
        String tmp = player_dict.get(player_name);
        if (tmp != null){
            Date now = new Date();
            Date begin = d1.parse(tmp);
            long diff = (now.getTime() - begin.getTime())/(1000 * 60);
            sendPost(("user_name=" + player_name + "&online_time=" + String.valueOf(diff)));
            plugin.getLogger().info(diff + " min");
            player_dict.remove(player_name);

        }else{
            plugin.getLogger().info(player_name + " 0 min");
        }
        plugin.getLogger().info(player_name + " left the server! :'(");
    }

}
