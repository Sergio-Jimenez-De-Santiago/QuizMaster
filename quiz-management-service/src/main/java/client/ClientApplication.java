package client;

import java.text.NumberFormat;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.core.ClientInfo;
import service.core.Offer;
import service.core.Quotation;

public class ClientApplication {
    /**
	 * This is the starting point for the application. Here, we must
	 * get a reference to the Broker Service and then invoke the
	 * getQuotations() method on that service.
	 * 
	 * Finally, you should print out all quotations returned
	 * by the service.
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        HttpClient httpClient = HttpClients.createDefault();
        ObjectMapper mapper = new ObjectMapper();

        for (ClientInfo client : clients) {
            displayProfile(client);
            try {
                String jsonPayload = mapper.writeValueAsString(client);
                HttpPost postRequest = new HttpPost("http://localhost:8080/offers");
                postRequest.setHeader("Content-Type", "application/json");
                postRequest.setHeader("Accept", "application/json");
                postRequest.setEntity(new StringEntity(jsonPayload));
                
                String responseBody = httpClient.execute(postRequest, response -> {
                    int statusCode = response.getCode();
                    if (statusCode == 201) {
                        String location = response.getHeader("Location").getValue();
                        System.out.println("Offer created at: " + location);
                        return EntityUtils.toString(response.getEntity());
                    } else {
                        throw new RuntimeException("Failed to create offer. Status code: " + statusCode);
                    }
                });
                
                Offer offer = mapper.readValue(responseBody, Offer.class);
                for (Quotation quotation : offer.quotations) {
                    displayQuotation(quotation);
                }
            } catch (Exception e) {
                System.out.println("Error with client " + client.name);
                e.printStackTrace();
            }
        }
    }

    /**
	 * Display the client info nicely.
	 * 
	 * @param info
	 */
	public static void displayProfile(ClientInfo info) {
		System.out.println("|=================================================================================================================|");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println(
				"| Name: " + String.format("%1$-29s", info.name) + 
				" | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
				" | Age: " + String.format("%1$-30s", info.age)+" |");
		System.out.println(
				"| Weight/Height: " + String.format("%1$-20s", info.weight+"kg/"+info.height+"m") + 
				" | Smoker: " + String.format("%1$-27s", info.smoker?"YES":"NO") +
				" | Medical Problems: " + String.format("%1$-17s", info.medicalIssues?"YES":"NO")+" |");
		System.out.println("|                                     |                                     |                                     |");
		System.out.println("|=================================================================================================================|");
	}

    /**
	 * Display a quotation nicely - note that the assumption is that the quotation will follow
	 * immediately after the profile (so the top of the quotation box is missing).
	 * 
	 * @param quotation
	 */
	public static void displayQuotation(Quotation quotation) {
		System.out.println(
				"| Company: " + String.format("%1$-26s", quotation.company) + 
				" | Reference: " + String.format("%1$-24s", quotation.reference) +
				" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
		System.out.println("|=================================================================================================================|");
	}

    /**
	 * Test Data
	 */
    public static final ClientInfo[] clients = {
        new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
        new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
        new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
        new ClientInfo("Rem Collier", ClientInfo.MALE, 49, 1.8, 120, false, true),
        new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 1.9, 75, true, false),
        new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };
}
