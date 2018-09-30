package com.neusoft.tools.speechRecognition;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;
 
public class SpeechRecog {
 
    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";
    private String path = "F:\\";
    //put your own params here
    private static final String apiKey = "z7DTZv1tynlyRG0mIWFCRxMo";//这里的apiKey就是前面申请在应用卡片中的apiKey
    private static final String secretKey = "tXvhU3ZyWGzKGkcnzehWWKPqdUlHpfDR";//这里的secretKey就是前面申请在应用卡片中的secretKey
    private static final String cuid = "baidu-speech";//cuid是设备的唯一标示，因为我用的是PC，所以这里用的是网卡Mac地址
 
    public static void main(String[] args) throws Exception {
    	SpeechRecog sr = new SpeechRecog();
        sr.getToken();
        sr.getText("150b368a-5e05-44d1-a18a-37ebc66c5c46.wav");
        //method2();
    }
 
    private void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +
            "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");
    }
 
    private byte[] getText(String filename) throws Exception {
        File pcmFile = new File(filename);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();
 
        // construct params
        JSONObject params = new JSONObject();
        //params.put("dev_pid", 1536);
        params.put("format", "wav");
        params.put("rate", 16000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("len", pcmFile.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));
 
        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
 
        conn.setDoInput(true);
        conn.setDoOutput(true);
 
        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();
 
        JSONArray result = new JSONObject(printResponse(conn)).getJSONArray("result");
        StringBuilder text = new StringBuilder(result.get(0).toString());
//        
//        String text2 = new String(text.toString().getBytes(),"UTF-8");
//        if(text.substring(text.length()-1).toString() == ",")
//        	text2 = new String(text.substring(0, text.length()-1).getBytes(),"UTF-8");
        String text2 = new String(text.substring(0, text.length()-1).getBytes(),"UTF-8");
        //System.out.println("text2:" + text2);
        byte[] text3 = text2.getBytes("UTF-8");
        System.out.println("!"+text2);
        return text3;
    }

    public byte[] recognize(String filename) throws Exception {
    	getToken();
    	return getText(filename);
    }
    
    private String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }
 
    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
 
        long length = file.length();
        byte[] bytes = new byte[(int) length];
 
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
 
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
 
        is.close();
        return bytes;
    }
}
