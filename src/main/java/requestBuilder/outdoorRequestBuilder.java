package requestBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import API.fleetProcessor;
import API.fleetProcessor.FleetDataAppender;
import API.giftProcessor;
import API.productDataMapping;
import Utility.readProperties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.io.IOException;


public class outdoorRequestBuilder {

	private readProperties properties = new readProperties();
	private String RequestFormat = properties.getPropertyValues("Outdoor_request_Format");
	private String outdoorPath = properties.getPropertyValues("Outdoor_file_path");
	private String requestPath = outdoorPath + RequestFormat + "\\";
	private String processor = properties.getPropertyValues("Processor");
	private JSONObject fileContent = null;

	// Request Paramters only
	private String POSID = "";
	private String TrackData = "";
	private String EncryptionMode = "";
	private String CardDataSource = "";
	private String EMVDetailsData = "";
	private String PINBlock = "";
	private String KSNBlock = "";
	private String PinBlockMode = "";
	private String TransactionSequenceNumber = "";
	private String BlackHawkUpc = "";

	// Needed changes
	private String productTotalAmount = "1.00";
	private String crmTokenRefund = "";
	private int productCount;
	private String partialAuthAmountIndicator = "";
	private String trackDataCardnumber = "";
	private String randomNumber = "";
	private String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")).replace("/", "");
	private String dateYMD = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	private String time = LocalTime.now().format(DateTimeFormatter.ofPattern(":SSS")).replace(":", "");

	public void userData(String POSID, String TrackData, String EncryptionMode, String CardDataSource,
			String EMVDetailsData, String PINBlock, String KSNBlock, String PinBlockMode, int productCount,
			String TransactionSequenceNumber, String partialAuthAmountIndicator, String crmTokenRefund,
			String randomNumber) {
		this.POSID = POSID;
		this.TrackData = TrackData;
		this.EncryptionMode = EncryptionMode;
		this.CardDataSource = CardDataSource;
		this.EMVDetailsData = EMVDetailsData;
		this.PINBlock = PINBlock;
		this.KSNBlock = KSNBlock;
		this.PinBlockMode = PinBlockMode;
		this.productCount = productCount;
		this.TransactionSequenceNumber = TransactionSequenceNumber;
		this.partialAuthAmountIndicator = partialAuthAmountIndicator;
		this.crmTokenRefund = crmTokenRefund;
		this.randomNumber = randomNumber;
	}

	public String GetCardBINRequest() {

		try {
			String content;
			if (RequestFormat.equalsIgnoreCase("XML")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + "GetCardBINRequest.xml")));
				fileContent = XML.toJSONObject(content);
				
			} else if (RequestFormat.equalsIgnoreCase("JSON")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + "GetCardBINRequest.json")));
				fileContent = new JSONObject(content);
			} else {
				throw new IllegalArgumentException("Unsupported request format: " + RequestFormat);
			}
			
			JSONObject GetCardBINRequest = fileContent.getJSONObject("GetCardBINRequest");
			JSONObject cardDataInfo = GetCardBINRequest.getJSONObject("CardDataInfo");
			GetCardBINRequest.put("POSID", POSID);
			cardDataInfo.put("TrackData", TrackData);
			cardDataInfo.put("EncryptionMode", EncryptionMode);
			cardDataInfo.put("CardDataSource", CardDataSource);
			cardDataInfo.put("EMVDetailsData", EMVDetailsData);
			cardDataInfo.put("PINBlock", PINBlock);
			cardDataInfo.put("KSNBlock", KSNBlock);
			cardDataInfo.put("PinBlockMode", PinBlockMode);
			if (RequestFormat.equalsIgnoreCase("XML")) {
				return XML.toString(fileContent); // Convert back to XML string
			} else { // JSON
				return fileContent.toString(); // Convert back to JSON string
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null; // Handle error gracefully based on your application's needs
		}
	}

	public String parentTransaction(String transactionType, String CardType, String TransactionTotal) {
		try {
			String content;
			if (RequestFormat.equalsIgnoreCase("XML")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + transactionType+".xml")));
				fileContent = XML.toJSONObject(content);
				
			} else if (RequestFormat.equalsIgnoreCase("JSON")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + transactionType+".json")));
				fileContent = new JSONObject(content);
			} else {
				throw new IllegalArgumentException("Unsupported request format: " + RequestFormat);
			}
			JSONObject Parent_Transaction = fileContent.getJSONObject("TransRequest");
			JSONObject TransAmountDetails = Parent_Transaction.getJSONObject("TransAmountDetails");
			Parent_Transaction.put("TransactionSequenceNumber", String.format("%06d", Integer.parseInt(TransactionSequenceNumber)));
				Parent_Transaction.put("POSID", POSID);
				Parent_Transaction.put("BlackHawkUpc", BlackHawkUpc);
				if (!transactionType.equalsIgnoreCase("CRM_refund")) {
					JSONObject CardDataInfo = Parent_Transaction.getJSONObject("CardDataInfo"); 
					Parent_Transaction.put("CardType", CardType);
					CardDataInfo.put("TrackData", TrackData);
					CardDataInfo.put("EncryptionMode", EncryptionMode);
					CardDataInfo.put("CardDataSource", CardDataSource);
					CardDataInfo.put("EMVDetailsData", EMVDetailsData);
					CardDataInfo.put("PINBlock", PINBlock);
					CardDataInfo.put("KSNBlock", KSNBlock);
					CardDataInfo.put("PinBlockMode", PinBlockMode);
				}

				Parent_Transaction.put("TransactionDate", date);
				Parent_Transaction.put("ReferenceNumber", date + (Integer.parseInt(randomNumber)) + time);
				Parent_Transaction.put("InvoiceNumber", date + (Integer.parseInt(randomNumber)) + time);
				TransAmountDetails.put("TransactionTotal", productTotalAmount);
				
				if (CardType.equalsIgnoreCase("GCC")) {
					giftProcessor giftProcessor = new giftProcessor();
					trackDataCardnumber = fleetProcessor.cardNumberFinder(TrackData, CardDataSource); 
					BlackHawkUpc = giftProcessor.blackHawkUpsFinder(trackDataCardnumber); //
				}

				if (partialAuthAmountIndicator.equalsIgnoreCase("1")) {
					productTotalAmount = TransactionTotal;
				}

				if (transactionType.toUpperCase().equalsIgnoreCase("REVERSAL")) {
					productCount = 0;
				}
				if (!crmTokenRefund.equalsIgnoreCase("00")) {
					Parent_Transaction.put("CRMToken", crmTokenRefund);
				}
				if (productCount != 0) {
					if (processor.equalsIgnoreCase("CHASE")
							&& (CardType.toUpperCase().endsWith("C") || CardType.toUpperCase().endsWith("D"))) {
						JSONObject products = productDataMapping.ProductDataMapping(productTotalAmount, transactionType,
								"l3productdata", productCount);
						TransAmountDetails.put("TransactionTotal", products.getString("ProductTotalAmt"));
						JSONObject Level3ProductsData = new JSONObject();
						Level3ProductsData.put("Level3ProductCount", products.getInt("Product_count"));
						Level3ProductsData.put("Level3Products",
								new JSONObject().put("Level3Product", products.getJSONArray("Product_list")));
						Parent_Transaction.put("Level3ProductsData", Level3ProductsData);
					}

					if (processor.equalsIgnoreCase("FD") || CardType.toUpperCase().endsWith("F")) {
						JSONObject products = productDataMapping.ProductDataMapping(productTotalAmount, transactionType,
								"fleetproductdata", productCount);
						TransAmountDetails.put("TransactionTotal", products.getString("ProductTotalAmt"));
						JSONObject FleetData = new JSONObject();
						FleetData.put("FleetProductCount", products.getInt("Product_count"));
						FleetData.put("FleetProducts",
								new JSONObject().put("FleetProduct", products.getJSONArray("Product_list")));
						Parent_Transaction.put("FleetData", FleetData);
					}
				}

				if (CardType.toUpperCase().endsWith("F")) {
					String prompts = fleetProcessor.trackDataPromptFinder(TrackData, CardDataSource, CardType);
					Map<String, String> prompts_appender = FleetDataAppender.promptFinderByValue(prompts, CardType);
					if (prompts_appender != null) {
						Parent_Transaction.put("FleetPromptsData", prompts_appender);
					}
				}
				
				return RequestFormat.equalsIgnoreCase("XML") ? XML.toString(fileContent) : fileContent.toString();
			
		}catch(IOException e){
			e.printStackTrace();
			return null;
	}
}

	public String childTransaction(String transactionType, String parentTransactionID, String parentAurusPayTicketNum, String CardType, String TransactionTotal, String CRMToken) {
		try {
			String content;
			if (RequestFormat.equalsIgnoreCase("XML")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + transactionType+".xml")));
				fileContent = XML.toJSONObject(content);
				
			} else if (RequestFormat.equalsIgnoreCase("JSON")) {
				content = new String(Files.readAllBytes(Paths.get(requestPath + transactionType+".json")));
				fileContent = new JSONObject(content);
			} else {
				throw new IllegalArgumentException("Unsupported request format: " + RequestFormat);
			}
			JSONObject childTransaction = fileContent.getJSONObject("TransRequest");
			JSONObject TransAmountDetails = childTransaction.getJSONObject("TransAmountDetails");
			childTransaction.put("TransactionSequenceNumber", String.format("%06d", Integer.parseInt(TransactionSequenceNumber)));
			childTransaction.put("POSID", POSID);
			childTransaction.put("TransactionDate", date);
			childTransaction.put("ReferenceNumber", date + (Integer.parseInt(randomNumber) + 1) + time);
			childTransaction.put("InvoiceNumber", date + (Integer.parseInt(randomNumber) + 1) + time);
			childTransaction.put("CRMToken", CRMToken);
			TransAmountDetails.put("TransactionTotal", transactionType.equalsIgnoreCase("VOID") ? TransactionTotal : productTotalAmount);
			if (transactionType.equalsIgnoreCase("REVERSAL")) {
				JSONObject CardDataInfo = fileContent.getJSONObject("CardDataInfo");
				CardDataInfo.put("TrackData", TrackData);
				CardDataInfo.put("EncryptionMode", EncryptionMode);
				CardDataInfo.put("CardDataSource", CardDataSource);
				childTransaction.put("CardType", CardType);
				CardDataInfo.put("EMVDetailsData", EMVDetailsData);
				CardDataInfo.put("PINBlock", PINBlock);
				CardDataInfo.put("KSNBlock", KSNBlock);
				CardDataInfo.put("PinBlockMode", PinBlockMode);
			}
			if (CRMToken.isEmpty()) {
				childTransaction.put("OrigTransactionIdentifier", parentTransactionID);
				childTransaction.put("OrigAurusPayTicketNum", parentAurusPayTicketNum);
			}
			if (transactionType.equalsIgnoreCase("REFUND_RETRY")) {
				childTransaction.put("DuplicateTransCheck", "1");
				childTransaction.put("OfflineTicketNumber", "O" + dateYMD + "12" + randomNumber);
				childTransaction.put("TransactionTotal", TransactionTotal);
			}
			if (productCount != 0 && transactionType.equalsIgnoreCase("POST-AUTH")) {
				if (processor.equalsIgnoreCase("CHASE") && (CardType.toUpperCase().endsWith("D") || CardType.toUpperCase().endsWith("C"))) {
					JSONObject products = productDataMapping.ProductDataMapping(productTotalAmount, transactionType, "l3productdata", productCount);
					TransAmountDetails.getJSONObject("TransAmountDetails").put("TransactionTotal", products.getString("ProductTotalAmt"));
					childTransaction.put("Level3ProductsData", products);
					JSONObject Level3ProductsData = new JSONObject();
					Level3ProductsData.put("Level3ProductCount", products.getInt("Product_count"));
					Level3ProductsData.put("Level3Products", new JSONObject().put("Level3Product", products.getJSONArray("Product_list")));
					childTransaction.put("Level3ProductsData", Level3ProductsData);
				}
				if (processor.equalsIgnoreCase("FD") || CardType.toUpperCase().endsWith("F")) {
					JSONObject products = productDataMapping.ProductDataMapping(productTotalAmount, transactionType, "fleetproductdata", productCount);
					TransAmountDetails.getJSONObject("TransAmountDetails").put("TransactionTotal", products.getString("ProductTotalAmt"));
					JSONObject FleetData = new JSONObject();
					FleetData.put("FleetProductCount", products.getInt("Product_count"));
					FleetData.put("FleetProducts", new JSONObject().put("FleetProduct", products.getJSONArray("Product_list")));
					childTransaction.put("FleetData", FleetData);
				}
			}
			return RequestFormat.equalsIgnoreCase("XML") ? XML.toString(fileContent) : fileContent.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}