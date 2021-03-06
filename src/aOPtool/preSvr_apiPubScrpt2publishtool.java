package aOPtool;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.attilax.fileTrans.AuthEx;
import com.attilax.fileTrans.ConnEx;
import com.attilax.fileTrans.SShFileUtilV3t33;
import com.attilax.fileTrans.createSCPClientEx;
import com.attilax.fileTrans.uploadFileEx;
import com.attilax.util.PrettyUtil;
import com.attilax.util.shellUtilV2t33;
import com.google.common.base.Joiner;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class preSvr_apiPubScrpt2publishtool {
	static int confirmKey=1;
	final static Logger logger = Logger.getLogger(preSvr_apiPubScrpt2publishtool.class);

	public static void main(String[] args) throws Exception {
	String string =  FileUtils.readFileToString(new File("H:\\0db\\pre11.txt")); ;  ;
		
		URI url = new URI (string.trim());
		System.out.println(url.getHost());
		System.out.println("****************"+string);
		 //第一步：注册热键，第一个参数表示该热键的标识，第二个参数表示组合键，如果没有则为0，第三个参数为定义的主要热键
	      
	       JIntellitype.getInstance().registerHotKey(confirmKey, JIntellitype.MOD_ALT, (int)'C');  
	    //   JIntellitype.getInstance().registerHotKey(EXIT_KEY_MARK, JIntellitype.MOD_ALT, (int)'Q'); 
	       

			HotkeyListener	hotkeyListener = new HotkeyListener(){
				public void onHotKey(int code) {
					switch(code){
					case 1:
	                    //这里写想实现的功能
						System.out.println("alt+enter");
						 try {
							main2(string);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 2:
						System.out.println("alt+s");
						break;
					case 3:
						System.out.println("alt+q");
						break;
						
					}
				}};
			JIntellitype.getInstance().addHotKeyListener(hotkeyListener);

		// http://101.132.148.11:9000/admin

	
		
		 

		

	}

	private static void main2(String string) throws Exception, IOException {
		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		SShFileUtilV3t33 c = new SShFileUtilV3t33().setcfg(string);

		Connection connection = c.conn();
		logger.info(" conned ok");
		// Session session = connection.openSession();

		//  uploadWar(c, connection);
		 try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//	  rebootTomcat(connection);

		String kewword_forkillpid = "api-tomcat9";
		showGrepProcessList(connection, kewword_forkillpid);

		String cmd3 = "ps -ef|grep  tomcat";
		logger.info(cmd3);	 
		System.out.println(Joiner.on("\r\n").join(SShFileUtilV3t33.exec(cmd3, connection)));

		System.out.println("--f");
	}

	private static void uploadWar(SShFileUtilV3t33 c, Connection connection)
			throws IOException, ConnEx, AuthEx, createSCPClientEx, uploadFileEx {
		// bek
		String cmd_bek = "mv  /tt/www/api-tomcat9/webapps/api.war    /tt/www/api-tomcat9/backup/api.war."
				+ new SimpleDateFormat("yyyy-MM-dd.HHmmss").format(new java.util.Date());
		logger.info(cmd_bek);
		List<String> result2 = SShFileUtilV3t33.exec(cmd_bek, connection);
		System.out.println(Joiner.on("\r\n").join(result2));

		// upload

		String localFIle = "G:\\0ttapi\\tt-api\\com-tt-yxt\\target\\tt-yxt-0.0.1-SNAPSHOT.war";
		String scppath = "/tt/www/api-tomcat9/webapps/";
		logger.info("upload file:" + localFIle + " " + scppath);
		c.scpClient = c.getScpclient(connection);
		c.upload(connection, localFIle, scppath);

		// rename
		String cmd = " mv  /tt/www/api-tomcat9/webapps/tt-yxt-0.0.1-SNAPSHOT.war  /tt/www/api-tomcat9/webapps/api.war ";
		logger.info(cmd);
		List<String> result =SShFileUtilV3t33. exec(cmd, connection);
		System.out.println(Joiner.on("\r\n").join(result));
	}

	private static void rebootTomcat(Connection connection) throws IOException {
		String kewword_forkillpid = "api-tomcat9";
		try {
			killTomcat(connection, kewword_forkillpid);
		} catch (Exception e) {
			logger.info("", e);
		}

		// restart

		/// usr/local/jenkins-tomcat8 /usr/local/jenkins-tomcat8/bin/startup.sh
		String JAVA_HOME = "export JAVA_HOME=/tt/www/jdk1.8.0_191";
		String cmd_startTomcat = JAVA_HOME + ";" + " /tt/www/api-tomcat9/bin/startup.sh   ";
		logger.info(cmd_startTomcat);
		System.out.println(SShFileUtilV3t33.exec(cmd_startTomcat, connection.openSession(), 3));

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String cmd3 = "ps -ef|grep  tomcat";
		logger.info(cmd3);
		System.out.println(SShFileUtilV3t33.exec(cmd3, connection));

	}

	private static void killTomcat(Connection connection, String kewword_forkillpid) throws IOException {
		List<Integer> pids = showGrepProcessList(connection, kewword_forkillpid);

		// kill pids
		for (int pid : pids) {
			killPid(pid, connection);
		}

	}

	private static List<Integer> showGrepProcessList(Connection connection, String kewword_forkillpid)
			throws IOException {
		// get
		String cmd3 = "ps -ef|grep  tomcat";
		logger.info(cmd3);
		List<String> result3 = SShFileUtilV3t33.exec(cmd3, connection);
		String ps_rzt_csv = Joiner.on("\r\n").join(result3);
		System.out.println(ps_rzt_csv);
		// readAsCsv(ps_rzt_csv);
		logger.info("------------------");

		System.out.println("\r\n");
		List<Map> tab = shellUtilV2t33.toTableNoHeadMode_ByMultiSpace(ps_rzt_csv);
		System.out.println(PrettyUtil.showListMap(tab));

		List<Integer> pids = shellUtilV2t33.getPids(tab, kewword_forkillpid, 1);
		logger.info("---getpid:" + pids);
		return pids;
	}

	private static void killPid(int pid, Connection connection) throws IOException {
		String cmd4 = "kill " + String.valueOf(pid);
		logger.info(cmd4);
		logger.info("kill ret:" + SShFileUtilV3t33.exec(cmd4, connection));
	}

}
