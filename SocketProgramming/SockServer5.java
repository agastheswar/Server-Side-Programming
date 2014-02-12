import java.net.*;
import java.util.Hashtable;
import java.io.*;

class serverHelper implements Runnable{
    protected String id;
    protected String decoded_value;
    Hashtable<String, Integer> hash;
    Socket sock = null;
    InputStream in = null;
    OutputStream out = null;
    
    int sum = 0;
    int millisec = 0;

    public serverHelper (String id, String value, Hashtable<String,Integer>hash, Socket sock, int millisec) {
        this.id = id;
        decoded_value = value;
        this.hash = hash;
        this.sock = sock;
        this.millisec = millisec;
    }

    public void run(){
    	int value = 0;
    	try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
    	
    	if(decoded_value.trim().equals("reset")){
        	sum = getTotal(0,0, millisec, id, hash);
        	hash.put(id, new Integer(sum));
        } else{
        	value = Integer.parseInt(decoded_value.trim()); 
        	sum = getTotal(value,(hash.get(id)).intValue(), millisec, id, hash);
        	System.out.println("Server received " + value );
        }
    	
    	String send_sum = Integer.toString(sum);
        byte[] server_sum = null;
		try {
			server_sum = send_sum.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	try {
			out.write(server_sum);
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
				try {
					if(in!=null)
						in.close();
					if (out != null)
						out.close();
					if (sock!=null)
						sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
		}
    	
    }
    
    public synchronized int getTotal(int value1, int value2, int millisec, String id, Hashtable<String, Integer> hash ){
    	try {
			sum = value1 + value2;
        	hash.put(id, new Integer(sum));
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return sum;
    }
}

class SockServer5 {
    public static void main(String args[]) throws Exception{


        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket sock = null;
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
                String input_string = new String(data, "UTF-8");
                String[] id_value = input_string.split("\0");
                String decoded_value = id_value[1];
                String id = id_value[0];
                String sec = id_value[2];
                int millisec = Integer.parseInt(sec);
                if(!hash.containsKey(id.trim())){
                	hash.put(id.trim(), new Integer(0));
                }
                serverHelper servhelp = new serverHelper(id, decoded_value, hash, sock, millisec);
                Thread servThread = new Thread(servhelp, "serverThread");
                servThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
    }
}

