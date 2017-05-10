package com.ericsson.appiot.examples.gateway.simplegateway;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.appiot.gateway.AppIoTGateway;
import com.ericsson.appiot.gateway.BaseAppIoTListener;
import com.ericsson.appiot.gateway.GatewayException;
import com.ericsson.appiot.gateway.InitializationException;
import com.ericsson.appiot.gateway.deviceregistry.DeviceRegistration;
import com.ericsson.appiot.gateway.dto.DeviceDiscoveryRequest;
import com.ericsson.appiot.gateway.dto.DeviceDiscoveryResponse;
import com.ericsson.appiot.gateway.dto.DeviceRegisterRequest;
import com.ericsson.appiot.gateway.dto.Operation;
import com.ericsson.appiot.gateway.dto.ResourceDiscoveryResponse;
import com.ericsson.appiot.gateway.dto.ResourceLink;
import com.ericsson.appiot.gateway.dto.ResponseCode;
import com.ericsson.appiot.gateway.dto.Setting;
import com.ericsson.appiot.gateway.dto.SettingCategory;
import com.ericsson.appiot.gateway.model.Path;
import com.ericsson.appiot.gateway.senml.SenMlEntry;
import com.ericsson.appiot.gateway.senml.SenMlObject;

public class MyAppIoTListener extends BaseAppIoTListener {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public MyAppIoTListener() {
	}

	@Override
	public void init(AppIoTGateway gateway) throws InitializationException {
		super.init(gateway);
	}

	@Override
	public void onGatewayUpdateSettingsRequest(List<SettingCategory> settingCategories) {

		for (SettingCategory settingCategory : settingCategories) {
			logger.log(Level.INFO, String.format("### %s ###", settingCategory.getName()));
			for (Setting setting : settingCategory.getSettings()) {
				logger.log(Level.INFO, String.format("%s = %s", setting.getKey(), setting.getValue()));
			}
		}
	}

	@Override
	public void onDeviceRegisterRequest(String correlationId, String endpoint,
			DeviceRegisterRequest deviceRegisterRequest) {
		ResponseCode responseCode = null;

		String operation = deviceRegisterRequest.getOperation();

		switch (operation) {

		case Operation.POST:
			// Device registered to gateway
			break;
		case Operation.PUT:
			// Device registered to gateway is updated
			break;

		case Operation.DELETE:
			// Device removed from gateway
			break;

		default:
			String message = String.format("Operation not supported. %s", operation);
			logger.log(Level.WARNING, message);
			responseCode = new ResponseCode(message, ResponseCode.BAD_OPTION);
		}

		DeviceRegistration deviceRegistration = getGateway().getDeviceRegistry().getRegistrationByEndpoint(endpoint);
		for (SettingCategory settingCategory : deviceRegistration.getSettingCategories()) {
			logger.log(Level.INFO, String.format("### %s ###", settingCategory.getName()));
			for (Setting setting : settingCategory.getSettings()) {
				logger.log(Level.INFO, String.format("%s = %s", setting.getKey(), setting.getValue()));
			}
		}

		try {
			getGateway().sendDeviceRegisterResponse(correlationId, endpoint, responseCode);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public void onDeviceDiscoveryRequest(String correlationId, DeviceDiscoveryRequest deviceDiscoveryRequest) {

		String endpoint = "abcdef"; // A unique identifier of the device. E.g.
									// MAC Address for a bluetooth device
		int signalStrength = -54; // A number indicating the quality of the
									// connection between the gateway and the
									// device. This is used by AppIoT to list
									// gateways where best connectivity is
									// first.
		long observeTime = System.currentTimeMillis();// unix timestamp in
														// milliseconds UTC

		DeviceDiscoveryResponse deviceDiscoveryResponse = new DeviceDiscoveryResponse();
		deviceDiscoveryResponse.setLastObserved(observeTime);
		deviceDiscoveryResponse.setSignalStrength(signalStrength);

		try {
			getGateway().sendDeviceDiscoveryResponse(correlationId, endpoint, deviceDiscoveryResponse);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
		}
	}

	@Override
	public void onResourceDiscoveryRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and do the following
		Path path = new Path(resourceLink.getUrl());
		if(path.getObjectId() == 3303 && path.getObjectInstanceId() == 0) {
			List<ResourceLink> resourceLinks = new ArrayList<>();
			resourceLinks.add(new ResourceLink("/3303/0/5700"));
			resourceLinks.add(new ResourceLink("/3303/0/5701"));
			resourceLinks.add(new ResourceLink("/3303/0/5601"));
			resourceLinks.add(new ResourceLink("/3303/0/5602"));
			resourceLinks.add(new ResourceLink("/3303/0/5605"));
			
			ResourceDiscoveryResponse resourceDiscoveryResponse = new ResourceDiscoveryResponse();
			resourceDiscoveryResponse.setResourceLinks(resourceLinks);
			resourceDiscoveryResponse.setResponseCode(new ResponseCode(ResponseCode.CONTENT, ""));
			
			try {
				getGateway().sendResourceDiscoveryResponse(correlationId, endpoint, resourceDiscoveryResponse);
			} catch (GatewayException e) {
				logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
			}
		}
	}
	
	

	@Override
	public void onResourceObserveRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and do the following
		Path path = new Path(resourceLink.getUrl());
		if(path.isResource()) {
			// Subscribe to the resource of the device
			try {
				getGateway().sendResourceObserveResponse(correlationId, endpoint, new ResponseCode(ResponseCode.CHANGED, "Resource is observed."));
			} catch (GatewayException e) {
				logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
			}
		}
	}

	@Override
	public void onResourceCancelObserveRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and do the following
		Path path = new Path(resourceLink.getUrl());
		if(path.isResource()) {
			// Unsubscribe to the resource of the device
			try {
				getGateway().sendResourceObserveResponse(correlationId, endpoint, new ResponseCode(ResponseCode.CHANGED, "Resource no longer observed."));
			} catch (GatewayException e) {
				logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
			}
		}
	}

	@Override
	public void onResourceReadRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and read the resource
		ResponseCode responseCode = new ResponseCode(ResponseCode.CONTENT, "Reading resource was easy.");
		try {
			getGateway().sendResourceReadResponse(correlationId, endpoint, responseCode);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
		}

		SenMlObject senMlObject = new SenMlObject();
		senMlObject.setBaseName(endpoint);
		senMlObject.setBaseTime(System.currentTimeMillis() / 1000);

		SenMlEntry senMlEntry = new SenMlEntry();
		senMlEntry.setName(resourceLink.getUrl());
		senMlEntry.setStringValue("Hello!");
		senMlObject.addEntry(senMlEntry);

		try {
			getGateway().sendResourceObservationValue(senMlObject);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send measurement to AppIoT", e);
		}
	}

	@Override
	public void onResourceWriteRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and write to the resource
		ResponseCode responseCode = new ResponseCode(ResponseCode.CONTENT, "Writing resource was easy.");
		try {
			getGateway().sendResourceWriteResponse(correlationId, endpoint, responseCode);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
		}
	}

	@Override
	public void onResourceExecuteRequest(String correlationId, String endpoint, ResourceLink resourceLink) {
		// Get the device by the endpoint value and execute the resource
		ResponseCode responseCode = new ResponseCode(ResponseCode.CONTENT, "Executing resource was easy.");
		try {
			getGateway().sendResourceExecuteResponse(correlationId, endpoint, responseCode);
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send response to AppIoT", e);
		}	
	}
	
	
	public void someMethod() {
		// Device is disconnected or deregistering
		try {
			getGateway().sendDeviceDeregisterRequest("abcdef");
		} catch (GatewayException e) {
			logger.log(Level.WARNING, "Failed to send request to AppIoT", e);
		}
	}
	
	
}
