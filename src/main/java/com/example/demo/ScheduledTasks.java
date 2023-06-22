package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	RestTemplate restTemplate; 
	
	
	
	
	public static void main(String[] args) {
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();
			// SOAP Envelope
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("acme", "http://samples.saaj.jms");
			// SOAP Body
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("employee", "acme");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("id", "acme");
			soapBodyElem1.addTextNode("10");
			soapMessage.saveChanges();
			System.out.println("Request SOAP Message = ");
			soapMessage.writeTo(System.out);
			
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			soapMessage.writeTo(outstream);
			String strMsg = new String(outstream.toByteArray());
			
			System.out.println("MENSAJE");
			System.out.println(strMsg);
			
		} catch (SOAPException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.TEXT_XML);
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n"
				+ "  <soap12:Body>\n"
				+ "    <ListOfCountryNamesByName xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n"
				+ "    </ListOfCountryNamesByName>\n"
				+ "  </soap12:Body>\n"
				+ "</soap12:Envelope>";
		HttpEntity<String> entity = new HttpEntity<>(xml, header);
		ResponseEntity<String> response = restTemplate.exchange("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso", HttpMethod.POST, entity, String.class);
		String responseString =response.getBody(); 
	}
}