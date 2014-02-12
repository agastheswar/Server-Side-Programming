import java.net.*;
import java.util.Hashtable;
import java.io.*;

class SockServer3 {
    public static void main (String args[]) throws Exception {

        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket sock = null;
        int sum = 0;
        Hashtable<String, Integer> hash = new Hashtable<String, Integer>();
        
        try {
            serv = new ServerSocket(8888);
        } catch(Exception e) {
	    e.printStackTrace();
	}
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
            try {
                sock = serv.accept();
                in = sock.getInputStream();
                out = sock.getOutputStream();
                byte[] data = new byte[100];
                in.read(data);
                int value = 0;
                String input_string = new String(data, "UTF-8");
                String[] id_value = input_string.split("\0");
                String decoded_value = id_value[1];
                String id = id_value[0];
                if(decoded_value.trim().equals("reset")){
                	sum =0;
                } else{
                	value = Integer.parseInt(decoded_value.trim()); 
                	System.out.println("Server received " + value );
                }
                
                if(hash.containsKey(id.trim())){
                	sum = value + (hash.get(id.trim())).intValue();
                	System.out.println("value is "+ value);
                	hash.put(id.trim(), new Integer(sum));
                } else{
                	sum = value;
                	hash.put(id.trim(), new Integer(value));
                }
                System.out.println("Server received " + value + " from id " + id );
                String send_sum = Integer.toString(sum);
                byte[] server_sum = send_sum.getBytes("UTF-8");
                out.write(server_sum);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (sock != null) sock.close();
            }
        }
    }
}

