package pl.finances.finances_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.finances.finances_app.dto.ExchangeRateDTO;

@Service
public class ExchangeRateService {
    private final RestTemplate restTemplate;

    @Autowired
    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getEuroExchangeRate() {
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/eur/?format=json";

        return findRate(url);
    }

    public double getUSDExchangeRate() {
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/usd/?format=json";

        return findRate(url);
    }

    private double findRate(String url){
        try {
            ResponseEntity<ExchangeRateDTO> response = restTemplate.getForEntity(url, ExchangeRateDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ExchangeRateDTO body = response.getBody();
                if (body.rates() != null && !body.rates().isEmpty()) {
                    return body.rates().getFirst().mid();
                }
            }

            throw new RuntimeException("Invalid or empty response from NBP API");

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch exchange rate from NBP API", e);
        }
    }
}
