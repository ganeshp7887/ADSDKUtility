package API;

public class giftProcessor {
	
	public String blackHawkUpsFinder(String cardNumber) {
		switch (cardNumber) {
			case "9840000061933746424":
				return "71373309045";
			case "9840000060454102009":
				return "71373309038";
			case "9840000079000000046":
				return "71373309079";
			case "9840000067600001688":
				return "71373309053";
			case "9840000067000004787":
				return "71373309077";
			case "9840000070000000391":
				return "04125010012";
			case "9840000065000009699":
				return "71373309057";
			case "9840000069000001474":
				return "71373309078";
			case "9840000070000001391":
				return "04125010012";
			case "4358361000067716":
				return "07675022645";
			case "4143521000074208":
				return "07675023072";
			case "5311050209356771":
				return "07675017832";
			default:
				return "0000000000";
		}
	}

}
