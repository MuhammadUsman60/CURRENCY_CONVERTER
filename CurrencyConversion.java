import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConversion {
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter the base currency: ");
        String baseCurrency = reader.readLine().toUpperCase();

        System.out.print("Enter the target currency: ");
        String targetCurrency = reader.readLine().toUpperCase();

        double exchangeRate = fetchExchangeRate(baseCurrency,targetCurrency);

        if (exchangeRate == -1) {
            System.out.println("Failed to fetch exchang rate");
            return;
        }

        System.out.println("Enter a amount to convert");
        double amount = Double.parseDouble(reader.readLine());

        double convertedAmount = amount * exchangeRate;

        System.out.println("Converter Amount: " + convertedAmount);
    }


    public static double fetchExchangeRate(String baseCurrency, String targetCurrency) throws IOException {

        String apiKey = "fca_live_bwnTuWQhcqbdhcWwMwgpZS9FDEJz8EfoDCKIprxH";
        String apiUrl = "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonRespone = response.toString();
            Map<String,Double> rates = extractRates(jsonRespone);

            double baseCurrencyrate = rates.get(baseCurrency);
            double targetCurrencyrate = rates.get(targetCurrency);

            if (baseCurrencyrate == 0.0 || targetCurrencyrate == 0.0) {
                return -1;
            }
            return targetCurrencyrate / baseCurrencyrate;
        } else {
            return -1;
        }
    }
    public static Map<String, Double> extractRates(String jsonResponse){
        Map<String,Double> rates = new HashMap<>();
        int start = jsonResponse.indexOf("\"rates\":{")+9;
        int end = jsonResponse.indexOf("}",start);
        String ratesJson = jsonResponse.substring(start, end);

        String[] ratePairs = ratesJson.split(",");
        for (String ratePair: ratePairs){
            String[] keyValue = ratePair.split(":");
            String currency = keyValue[0].replaceAll("\"","");
            double rate = Double.parseDouble(keyValue[1]);
            rates.put(currency,rate);
        }
return  rates;
    }
}