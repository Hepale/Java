package com.healthlx.sftp;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 *
 * @author Alejandro Hdz
 */
public class Connector {

	private static String host, sftpuser, sftppassword, remotedir, localdir;
	private static int sftpport;

	/**
	 * Read Properties File
	 * 
	 */
	private static void readProperties() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			host = prop.getProperty("host");
			sftpuser = prop.getProperty("sftpuser");
			sftppassword = prop.getProperty("sftppassword");
			sftpport = Integer.valueOf(prop.getProperty("sftpport"));
			remotedir = prop.getProperty("remotedir");
			localdir = prop.getProperty("localdir");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Main method to start process
	 * @param args
	 */
	public static void main(String[] args) {
		
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		
		if (args.length != 1) {
			System.err.println("Shall run the program with this format:\n"
					+ "$> java com.healthlx.sftp.Connector <filename>");
			return;
		}
		
		String fileName = args[0];
		
		readProperties();
		
		/*
		 *  Sending file
		 */
		try {
			JSch jsch = new JSch();
			
			// Setting session
			session = jsch.getSession(sftpuser, host, sftpport);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(sftppassword);
			session.connect();

			// Open channel
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(remotedir);

			//Sending file
			channelSftp.put(localdir+fileName, remotedir+fileName);
			
			// Closing connection
			channel.disconnect();
			session.disconnect();
		} catch (JSchException | SftpException ex) {
			ex.printStackTrace();
		}
	}

}