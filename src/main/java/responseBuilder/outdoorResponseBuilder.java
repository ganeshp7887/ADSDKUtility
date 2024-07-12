package responseBuilder;
import java.io.IOException;
import java.net.UnknownHostException;

import org.json.JSONObject;
import requestBuilder.outdoorRequestBuilder;
import Utility.socketConnection;
import Utility.readProperties;
import org.json.XML;

public class outdoorResponseBuilder {
	
	outdoorRequestBuilder outdoorRequestBuilder = new outdoorRequestBuilder();
	socketConnection socketConnection = new socketConnection();
	readProperties readProperties = new readProperties();
	String requestFormat = readProperties.getPropertyValues("Outdoor_request_Format");
	
	
	public JSONObject convertRequest(String req, String res) {
		JSONObject request = null;
		JSONObject response = null;
		if(requestFormat.equalsIgnoreCase("XML")) {
			request = XML.toJSONObject(req);
			response = XML.toJSONObject(res);
			
		}else if(requestFormat.equalsIgnoreCase("JSON")) {
			request = new JSONObject(req);
			response = new JSONObject(res);
		}
		JSONObject convertedJSON = new JSONObject();
		convertedJSON.append("Request", request);
		convertedJSON.append("Response", response);
		return convertedJSON;
	}
	
	public String transactionDetails(String requestType, String parenttransactionType, String childTransactionType, String POSID, String TrackData, String EncryptionMode, String CardDataSource, String EMVDetailsData, String PINBlock, String KSNBlock, String PinBlockMode, int productCount,
			String TransactionSequenceNumber, String partialAuthAmountIndicator, String crmTokenRefund, String randomNumber, String ExpectedCardType, String TransactionTotal, String CRMToken) throws NumberFormatException, UnknownHostException, IOException, InterruptedException {
		String parenttransactionRequest = "";
		String parenttransactionResponse = "";
		JSONObject convertedData;
		socketConnection.connectSocket(Integer.parseInt(readProperties.getPropertyValues("Outdoor_Port")));
		outdoorRequestBuilder.userData(POSID, TrackData, EncryptionMode, CardDataSource, EMVDetailsData, PINBlock, KSNBlock, PinBlockMode, productCount, TransactionSequenceNumber, partialAuthAmountIndicator, crmTokenRefund, randomNumber);
		String getCardBinRequest = outdoorRequestBuilder.GetCardBINRequest();
		socketConnection.sendRequestToAESDK(getCardBinRequest);
		String getCardBinResponse = socketConnection.receiveResponseFromAESDK();
		convertedData = convertRequest(getCardBinRequest, getCardBinResponse);
		JSONObject GCBRequest = convertedData.getJSONArray("Request").getJSONObject(0);
		JSONObject GCBResponse = convertedData.getJSONArray("Response").getJSONObject(0);
		String CardType = GCBResponse.getJSONObject("GetCardBINResponse").getString("CardType");
		System.out.println(getCardBinRequest);
		System.out.println(getCardBinResponse);
		if ((GCBResponse.getJSONObject("GetCardBINResponse").get("ResponseCode").toString().startsWith("0") && !requestType.equalsIgnoreCase("00")) || !crmTokenRefund.equalsIgnoreCase("00")) {
			parenttransactionRequest = outdoorRequestBuilder.parentTransaction(parenttransactionType, CardType, TransactionTotal);
			socketConnection.sendRequestToAESDK(parenttransactionRequest);
			parenttransactionResponse = socketConnection.receiveResponseFromAESDK();
			convertedData = convertRequest(parenttransactionRequest, parenttransactionResponse);
			JSONObject transRequest = convertedData.getJSONArray("Request").getJSONObject(0);
			JSONObject transResponse = convertedData.getJSONArray("Response").getJSONObject(0);
			System.out.println(parenttransactionRequest);
			System.out.println(parenttransactionResponse);
			JSONObject transactionDetailData = requestFormat.equalsIgnoreCase("XML") ? transResponse.getJSONObject("TransResponse").getJSONObject("TransDetailsData").getJSONObject("TransDetailData") : transResponse.getJSONObject("TransResponse").getJSONObject("TransDetailsData").getJSONArray("TransDetailData").getJSONObject(0);
			System.out.println(requestType);
			System.err.println(transactionDetailData.get("ResponseCode").toString().startsWith("0"));
			if(transactionDetailData.get("ResponseCode").toString().startsWith("0") && (requestType.equals("05") || requestType.equals("06") || requestType.equals("08")|| requestType.equals("09"))) {
				TransactionTotal = childTransactionType.equalsIgnoreCase("VOID") ? transactionDetailData.getString("TransactionAmount") : TransactionTotal;
				String parentTransactionID = String.valueOf(transactionDetailData.getLong("TransactionIdentifier"));
				System.out.println(transResponse);
				String parentAurusPayTicketNumber = String.valueOf(transResponse.getJSONObject("TransResponse").getLong("AurusPayTicketNum"));
				String childtransactionRequest = outdoorRequestBuilder.childTransaction(childTransactionType, parentTransactionID, parentAurusPayTicketNumber, CardType, TransactionTotal, CRMToken);
				socketConnection.sendRequestToAESDK(childtransactionRequest);
				String childTransactionResponse = socketConnection.receiveResponseFromAESDK();
				System.out.println(childtransactionRequest);
				System.out.println(childTransactionResponse);
			}

		}
		return getCardBinResponse;
	}

}
