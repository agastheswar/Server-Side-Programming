import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

class WebServer2{

   
    public static void main(String args[]) throws NumberFormatException, IOException {
        if (args.length != 1) {
            System.out.println("Usage: WebServer2 <port>");
            System.exit(1);
        }
        
        WebServer2 server = new WebServer2(Integer.parseInt(args[0]));
    
    }

    public WebServer2(int port) throws IOException {
        
        ServerSocket    server = null;
        Socket          sock = null;

        try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
        while (server.isBound() && !server.isClosed()) {
            System.out.println("Ready...");
            try {
                sock = server.accept();
                serverhelp serv = new serverhelp(sock);
                Thread servthread = new Thread(serv, "serverThread");
                servthread.start();
            } catch(Exception e){
            	e.printStackTrace();
            }
        }
        
        
    }
    
}

class serverhelp extends Thread{
	Socket sock = null;
	InputStream in = null;
    OutputStream out = null;
	public serverhelp(Socket sock){
		super("serverThread");
		this.sock = sock;
	}
	
	public void run(){
		 try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
	         byte[] response = createResponse(in);
	         System.out.println(response.toString());
	         out.write(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
				try {
					if (out != null)
					out.close();
					 if (in != null)   in.close();
			           if (sock != null) sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           
        }
         
	}
	
	 public byte[] createResponse(InputStream inStream) {

	        byte[] response = null;
	        BufferedReader in = null;
	        Boolean isCgi = false;
	        Process p = null;

	        try {

	            // Read from socket's input stream.  Must use an
	            // InputStreamReader to bridge from streams to a reader
	            in = new BufferedReader(
	                        new InputStreamReader(inStream, "UTF-8"));

	            // Get header and save the filename from the GET line:
	            //    example GET format: GET /index.html HTTP/1.1

	            String filename = null;
		    String line = in.readLine();
		    System.out.println("Received: " + line);
	            if (line != null && !line.trim().equals("")) {
			// I will use an artificial delay to test your code
//			 try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			StringTokenizer st = new StringTokenizer(line);
			if (st.nextToken().equals("GET") && st.hasMoreTokens()) {
			    filename = st.nextToken();
			    if (filename.startsWith("/")) {
				filename = filename.substring(1);
				String[] cgisplit = filename.split("\\.");
				String[] cgisplit2 = cgisplit[1].split("\\?");
					if(cgisplit2[0].equals("cgi")){
						isCgi = true;
						String[] split1 = filename.split("\\?");
						filename = split1[0];
						System.out.println(" CGI file name is " + filename);
						String[] split2 = split1[1].split("\\&");
						p = Runtime.getRuntime().exec("./"+filename, split2);
					}
			    }
			}
			
			

				
	            }
	System.out.println("FINISHED REQUEST, STARING RESPONSE\n");

	            // Generate an appropriate response to the user
	            if (filename == null) {
	                response =
	                    "<html>Illegal request: no GET</html>".getBytes();
	            } else { 
	            	System.out.println(filename);
	                File file = new File(filename);
	                if (!file.exists()) {
	                	System.out.println("Working Directory = " +
	                            System.getProperty("user.dir"));
	                    response = ("<html>File not found: " +
	                                filename + "</html>").getBytes();
	                } else {
	                	if(isCgi){
	                		System.out.println("in cgi block");
	                		response = (getProcessOutput(p)).getBytes();
	                	} else{
	                    response = readFileInBytes(file);
	                	}
	                }
	            }
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	            response = ("<html>ERROR: " +
	                        e.getMessage() + "</html").getBytes();
	        }
		System.out.println("RESPONSE GENERATED!");
	        return response;
	    }

	    /** Read bytes from a file and return them in the byte array.
	        We read in blocks of 512 bytes for efficiency.
	    */
	    public byte[] readFileInBytes(File f)
	        throws IOException {

	        FileInputStream file = new FileInputStream(f);
	        ByteArrayOutputStream data = new ByteArrayOutputStream(file.available());

	        byte buffer[] = new byte[512];
	        int numRead = file.read(buffer);
	        while (numRead > 0) {
	            data.write(buffer, 0, numRead);
	            numRead = file.read(buffer);
	        }
	        file.close();

	        byte[] result =  data.toByteArray();
	        data.close();

	        return result;
	    }
	    
	    public String getProcessOutput(Process p){
	    	String result = null;
	    	BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	StringBuilder builder = new StringBuilder();
	    	String line = null;
	    	try {
				while ( (line = br.readLine()) != null) {
				   builder.append(line);
				   builder.append(System.getProperty("line.separator"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	result = builder.toString();
	    	System.out.println("result is "+ result);
	    	return result;
	    }
	    
	  
}