package com.neusoft.FastDFS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FileManager implements FileManagerconfig {
	private static final long serialVersionUID = 1L;
	private static TrackerClient trackerClient;
	private static TrackerServer trackerServer;
	private static StorageServer storageServer;
	private static StorageClient storageClient;
	
	static{
		try {
			String classPath = new File(FileManager.class.getResource("/").getFile()).getCanonicalPath();
			
			String fdfsClientConfigFilePath = classPath + File.separator + CLIENT_CONFIG_FILE;
			ClientGlobal.init(fdfsClientConfigFilePath);
			
			trackerClient = new TrackerClient();
			//trackerServer = trackerClient.getConnection();
			
			//storageServer= trackerClient.getStoreStorage(trackerServer);
			//storageClient = new StorageClient(trackerServer,storageServer);
			//System.out.println("storageClient:"+storageClient.hashCode());
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
		
	}
	public static StorageClient getStorageClient(){
		try {
			TrackerServer ts = trackerClient.getConnection();
			StorageServer ss= trackerClient.getStoreStorage(ts);
			StorageClient sc = new StorageClient(ts,ss);
			System.out.println("sc:"+sc.hashCode());
			return sc;	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;			
	}
	public static String upload(FastDFSFile file,NameValuePair[] valuePairs){
		String[] uploadResults = null;
		try {
			storageClient = getStorageClient();
			if(storageClient!=null){
				uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), valuePairs);
			}else{
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MyException e) {
			e.printStackTrace();
		}
		String groupName = uploadResults[0];
		String remoteFileName = uploadResults[1];
		
		String fileAbsolutePath = SEPARATOR + groupName + SEPARATOR + remoteFileName; //PROTOCOL +TRACKER_NGNIX_ADDR+ TRACKER_NGNIX_PORT + 
		return fileAbsolutePath;
	}
	public static String upload(byte[] bytes ,String filename) throws IOException{
		String[] split = filename.split("\\.");		
		String ext =split[split.length-1];		
		FastDFSFile file = new FastDFSFile(bytes, ext);
		return  upload(file,null);
	}
	public static String uploadFile(InputStream inputStream,String filename) throws IOException{
		byte[] bytes = new byte[5000000];
		int length = inputStream.read(bytes);		
		String[] split = filename.split("\\.");		
		String ext =split[split.length-1];		
		FastDFSFile file = new FastDFSFile(bytes, ext);
		/*NameValuePair[] meta_list = new NameValuePair[3];
		meta_list[0] = new NameValuePair("filename",filename);
		meta_list[1] = new NameValuePair("fileLength",String.valueOf(length));
		meta_list[2] = new NameValuePair("fileExt",ext);*/
		//meta_list[3] = new NameValuePair("fileAuthor","wangchao");
		return  upload(file,null);
	}
	
	public static Boolean DeleteFile(String DelFilename) throws IOException, MyException{
		String[] split2 = DelFilename.split("/", 3);
		String groupName = split2[1];
		String fileName = split2[2];
		int num = storageClient.delete_file(groupName, fileName);
		return num==0;		
	}
}
