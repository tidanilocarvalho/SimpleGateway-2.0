package com.ericsson.appiot.examples.gateway.simplegateway;

import com.ericsson.appiot.gateway.AppIoTGateway;
import com.ericsson.appiot.gateway.AppIoTListener;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleGateway {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private AppIoTListener appIoTListener;
	private AppIoTGateway appIoTGateway;

	public static void main(String[] args) {
		SimpleGateway application = new SimpleGateway();
		application.start();
	}
	
	private void start() {
		logger.log(Level.INFO, "Simple Gateway starting up.");
        appIoTListener = new MyAppIoTListener();
		appIoTGateway = new AppIoTGateway(appIoTListener);
		appIoTGateway.start();
		logger.log(Level.INFO, "Simple Gateway started. Type quit to shut down.");
		Scanner scanner = new Scanner(System.in);
		while(!scanner.nextLine().equalsIgnoreCase("quit")) {
		}
		scanner.close();
		logger.log(Level.INFO, "Simple Gateway shut down.");
	}
}
