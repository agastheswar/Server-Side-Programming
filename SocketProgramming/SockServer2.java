import java.net.*;
import java.io.*;

class SockServer2 {
    public static void main (String args[]) throws Exception {

        int count = 0;
        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket sock = null;
        int sum = 0;
        
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
                int x =0;
                String decoded_string = new String(data, "UTF-8");
                if(decoded_string.trim().equals("reset")){
                	sum =0;
                } else{
                	x = Integer.parseInt(decoded_string.trim()); 
                	System.out.println("Server received " + x );
                }
                
                sum = sum + x;
                System.out.println(sum);
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

