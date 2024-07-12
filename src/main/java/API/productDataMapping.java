package API;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class productDataMapping {
	
	public static JSONObject ProductDataMapping(String product_Total_Amount, String requestType, String productType, int productCount) {
		BigDecimal productTotalAmount = new BigDecimal(product_Total_Amount);
        List<JSONObject> products = new ArrayList<>();

         	products.add(new JSONObject()
                    .put("ProductCode", "001")
                    .put("ProductName", "Unleaded")
                    .put("UnitOfMeasure", "G")
                    .put("UnitPrice", "1.000"));

            products.add(new JSONObject()
                    .put("ProductCode", "042")
                    .put("ProductName", "Bread")
                    .put("UnitOfMeasure", "U")
                    .put("UnitPrice", "5.000"));

            products.add(new JSONObject()
                    .put("ProductCode", "002")
                    .put("ProductName", "Diesel")
                    .put("UnitOfMeasure", "G")
                    .put("UnitPrice", "2.000"));

        

        List<JSONObject> product_list = new ArrayList<>();
        BigDecimal productTotalAmt = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        if ("Pre_auth".equals(requestType)) {
            productCount = 1;
        }

        for (int i = 0; i < productCount; i++) {
            BigDecimal productUnitPrice;
            BigDecimal productQuantity;

            if ("Pre_auth".equals(requestType)) {
                productUnitPrice = productTotalAmount.setScale(3, RoundingMode.HALF_UP);
                productQuantity = BigDecimal.ONE.setScale(3, RoundingMode.HALF_UP);
            } else {
                BigDecimal unitPrice = new BigDecimal(products.get(i).getString("UnitPrice"));
                productUnitPrice = unitPrice.setScale(3, RoundingMode.HALF_UP);

                // Simulated random product quantity (commented out)
                // productQuantity = BigDecimal.valueOf(Math.random() * (9.0 - 3.0) + 3.0).setScale(3, RoundingMode.HALF_UP);

                productQuantity = BigDecimal.TEN.setScale(3, RoundingMode.HALF_UP); // Simulated product quantity
            }

            BigDecimal productPrice = productUnitPrice.multiply(productQuantity).setScale(2, RoundingMode.HALF_UP);
            String productPriceInDec = productPrice.toString(); // Format as needed

            JSONObject productsMapped = new JSONObject();
            if ("l3productdata".equals(productType)) {
                productsMapped.put("L3ProductSeqNo", String.valueOf(i + 1))
                        .put("L3ProductCode", products.get(i).getString("ProductCode"))
                        .put("L3ProductName", products.get(i).getString("ProductName"))
                        .put("L3UnitOfMeasure", products.get(i).getString("UnitOfMeasure"))
                        .put("L3ProductQuantity", productQuantity.toString())
                        .put("L3ProductUnitPrice", productUnitPrice.toString())
                        .put("L3ProductTotalAmount", productPriceInDec);
            } else if ("fleetproductdata".equals(productType)) {
                productsMapped
                		.put("FleetProductSeqNo", String.valueOf(i + 1))
                        .put("FleetNACSCode", products.get(i).getString("ProductCode"))
                        .put("FleetProductName", products.get(i).getString("ProductName"))
                        .put("FleetUnitOfMeasure", products.get(i).getString("UnitOfMeasure"))
                        .put("FleetProductDataType", "102")
                        .put("FleetServiceLevel", "S")
                        .put("FleetProductQuantity", productQuantity.toString())
                        .put("FleetProductUnitPrice", productUnitPrice.toString())
                        .put("FleetProductTotalAmount", productPriceInDec);
            }

            product_list.add(productsMapped);
            productTotalAmt = productTotalAmt.add(productPrice);
        }

        JSONObject result = new JSONObject()
                .put("Product_count", String.valueOf(productCount))
                .put("Product_list", new JSONArray(product_list))
                .put("ProductTotalAmt", productTotalAmt.toString());
        return result;
    }
	
}