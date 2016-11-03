package com.ihelp.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;

public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public UploadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");  
        PrintWriter out = response.getWriter();  
        DiskFileItemFactory factory = new DiskFileItemFactory(); 
        //String upload = this.getServletContext().getRealPath("/image/"); 
        String upload = this.getServletContext().getRealPath("/image/"); 
        String temp = System.getProperty("java.io.tmpdir"); 
        factory.setSizeThreshold(1024 * 1024 * 20);  
        factory.setRepository(new File(temp));  
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);  
        try  
        {  
            @SuppressWarnings("unchecked")
			List<FileItem> list = servletFileUpload.parseRequest(request);  
  
            for (FileItem item : list)  
            {  
                String name = item.getFieldName();  
                InputStream is = item.getInputStream();  
                    try  
                    {  
                        inputStream2File(is, upload + "\\" + item.getName()); 
                        System.out.println("inputStream2File");
                    } catch (Exception e)  
                    {  
                        e.printStackTrace();  
                    } 
            }  
            System.out.println("success");
        } catch (FileUploadException e)  
        {  
            e.printStackTrace();  
            out.write("failure");  
        }  
        out.flush();  
        out.close();  
    }  
  
    // 流转化成字符串  
    public static String inputStream2String(InputStream is) throws IOException  
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int i = -1;  
        while ((i = is.read()) != -1)  
        {  
            baos.write(i);  
        }  
        return baos.toString();  
    }  
  
    // 流转化成文件  
    public static void inputStream2File(InputStream is, String savePath)  
            throws Exception  
    {  
        System.out.println("文件保存路径为:" + savePath);  
        File file = new File(savePath);  
        FileOutputStream fos = null;    
        BufferedInputStream bis = null;  
        
        int BUFFER_SIZE = 1024*1024*20; 
        byte[] buf = new byte[BUFFER_SIZE];    
        int size = 0; 
        bis = new BufferedInputStream(is); 
        fos = new FileOutputStream(file);  
        
        while ( (size = bis.read(buf)) != -1)     
            fos.write(buf, 0, size);
          fos.close();    
          bis.close(); 
          
    }

	
	
	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
