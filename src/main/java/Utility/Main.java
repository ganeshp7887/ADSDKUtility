package Utility;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

import requestBuilder.outdoorRequestBuilder;
import responseBuilder.outdoorResponseBuilder;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        // Get the location of the current running JAR file
    	String TrackData = "%B4485536666666663^VISA FLEET/CPS TEST^28121019206100000000011?~;4485536666666663=28121019206100000011?";
    	String EncrytionMode = "00";
    	String CardDataSource = "4";
    	String blank = "";
    	String parenttransactionType = "Pre_auth";
    	String childTransactionType = "Post_auth";
    	outdoorResponseBuilder outdoorResponseBuilder = new outdoorResponseBuilder();
    	outdoorResponseBuilder.transactionDetails("05", parenttransactionType, childTransactionType, "10", TrackData, EncrytionMode, CardDataSource, blank, blank, blank, blank, 1, "123456", blank, blank, "123456", blank, blank, blank);
    }
}