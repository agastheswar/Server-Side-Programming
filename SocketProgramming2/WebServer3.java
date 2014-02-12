import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

class WebServer3{

   
    public static void main(String args[]) throws NumberFormatException, IOException {
        if (args.length != 1) {
            System.out.println("Usage: WebServer3 <port>");
            System.exit(1);
        }
        
        WebServer3 server = new WebServer3(Integer.parseInt(args[0]));
    
    }

    public WebServer3(int port) throws IOException {
        
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
	        Boolean isSSi = false;
	        StringBuilder sb_text = new StringBuilder();
	        File  file_text = new File("file_text");
	        FileWriter fw = null;
			try {
				fw = new FileWriter(file_text);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        try {

	            in = new BufferedReader(
	                        new InputStreamReader(inStream, "UTF-8"));
	            String filename = null;
	            String line = in.readLine();
	            
	            System.out.println("Received: " + line);
	            
	            if (line != null && !line.trim().equals("")) {
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
	            							} else if(cgisplit2[0].equals("ssi")){
	            								isSSi = true;
	            								String[] split1 = filename.split("\\?");
	            								filename = split1[0];
	            								System.out.println(filename);
	            								String[] split2 = split1[1].split("\\&");
	            								Map<String, String> hash = new HashMap<String,String>();
	            								
	            								for(int i =0; i<split2.length; i++){
	            									String[] split_args = split2[i].split("\\=");
	            									hash.put(split_args[0], split_args[1]);
	            								}
	            								
	            								BufferedReader br = new BufferedReader(new FileReader(filename));
	            								String file_lines;
	            								String newLine = System.getProperty("line.separator");
	            								while ((file_lines = br.readLine()) != null) {
	            									
	            									if(file_lines.startsWith("$$$$$", 0)){
	            										String[] split_line = file_lines.split(" ");
	            										StringBuilder sb = new StringBuilder();
	            										System.out.println("command is " + split_line[1]);
	            										System.out.println("no of parameters is "+ split_line.length);
	            										sb.append(split_line[1]);
	            										
	            										for(int i = 2; i< split_line.length; i++){
	            											if(split_line[i].startsWith("$", 0)){
	            												String[] split_params = split_line[i].split("\\$");
	            												sb.append(" ");
	            												sb.append(hash.get(split_params[1]));
	            											}
	            										}
	            										Process command = Runtime.getRuntime().exec(sb.toString());
	            										String result_command = getProcessOutput(command);
	            										sb_text.append(result_command + newLine);
							   
	            									} else{
	            										sb_text.append(file_lines + newLine);
	            									}
	            								}
	            								br.close();
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
	                	} else if(isSSi){
	                		response = sb_text.toString().getBytes();
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