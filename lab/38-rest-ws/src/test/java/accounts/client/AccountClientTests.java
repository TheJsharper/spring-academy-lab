package accounts.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import common.money.Percentage;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

public class AccountClientTests {

	private static final String BASE_URL = "http://localhost:8080";

	private RestTemplate restTemplate = new RestTemplate();
	private Random random = new Random();

	@Test
	public void listAccounts() {
		// TODO-03: Run this test
		// - Remove the @Disabled on this test method.
		// - Then, use the restTemplate to retrieve an array containing all Account
		// instances.
		// - Use BASE_URL to help define the URL you need: BASE_URL + "/..."
		// - Run the test and ensure that it passes.
		String url = BASE_URL.concat("/accounts");
		Account[] accounts = restTemplate.getForObject(url, Account[].class); // Modify this line to use the
																				// restTemplate

		assertNotNull(accounts);
		assertTrue(accounts.length >= 21);
		assertEquals("Keith and Keri Donald", accounts[0].getName());
		assertEquals(2, accounts[0].getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), accounts[0].getBeneficiary("Annabelle").getAllocationPercentage());
	}

	@Test
	public void getAccount() {
		// TODO-05: Run this test
		// - Remove the @Disabled on this test method.
		// - Then, use the restTemplate to retrieve the Account with id 0 using a URI
		// template
		// - Run the test and ensure that it passes.
		String url = BASE_URL.concat("/accounts/0");
		Account account = restTemplate.getForObject(url, Account.class); // Modify this line to use the restTemplate

		assertNotNull(account);
		assertEquals("Keith and Keri Donald", account.getName());
		assertEquals(2, account.getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), account.getBeneficiary("Annabelle").getAllocationPercentage());
	}

	@Test
	public void createAccount() throws URISyntaxException {
		// Use a unique number to avoid conflicts
		String number = String.format("12345%4d", random.nextInt(10000));
		Account account = new Account(number, "John Doe");
		account.addBeneficiary("Jane Doe");
		// TODO-08: Create a new Account
		// - Remove the @Disabled on this test method.
		// - Create a new Account by POSTing to the right URL and
		// store its location in a variable
		// - Note that 'RestTemplate' has two methods for this.
		// - Use the one that returns the location of the newly created
		// resource and assign that to a variable.
		// URI newAccountLocation = new URI("http", "", "localhost",8080, "account", "",
		// "");
		// Modify this line to use the restTemplate
		ResponseEntity<Void> postForEntity = restTemplate.postForEntity(BASE_URL + "/accounts", account, Void.class);

		HttpHeaders headers = postForEntity.getHeaders();
		assertTrue(postForEntity.getStatusCode().is2xxSuccessful());

		// TODO-09: Retrieve the Account you just created from
		// the location that was returned.
		// - Run this test, then. Make sure the test succeeds.
		// String url
		// =String.format(BASE_URL+"/accounts/%s",account.getEntityId().toString());
		URI location = headers.getLocation();
		Account retrievedAccount = restTemplate.getForObject(location, Account.class); // Modify this line to use the
																						// restTemplate

		assertEquals(account.getNumber(), retrievedAccount.getNumber());

		Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
		Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();

		assertEquals(accountBeneficiary.getName(), retrievedAccountBeneficiary.getName());
		assertNotNull(retrievedAccount.getEntityId());
	}

	@Test
	public void createAccountConflictDataIntegratration() throws URISyntaxException {
		// Use a unique number to avoid conflicts
		Account account = new Account("123", "John Doe");
		account.addBeneficiary("Jane Doe");
		// TODO-08: Create a new Account
		// - Remove the @Disabled on this test method.
		// - Create a new Account by POSTing to the right URL and
		// store its location in a variable
		// - Note that 'RestTemplate' has two methods for this.
		// - Use the one that returns the location of the newly created
		// resource and assign that to a variable.
		// URI newAccountLocation = new URI("http", "", "localhost",8080, "account", "",
		// "");
		// Modify this line to use the restTemplate
		ResponseEntity<Void> postForEntity = restTemplate.postForEntity(BASE_URL + "/accounts", account, Void.class);

		assertTrue(postForEntity.getStatusCode().is2xxSuccessful());

		HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
			System.out.println(
					"You SHOULD get the exception \"No such beneficiary with name 'David'\" in the server!!!!!!!!!!!!!!!!!!!.");

			// TODO-16: Try to retrieve the newly created Beneficiary again.
			// - Run this test, then. It should pass because we expect a 404 Not Found
			// If not, it is likely your delete in the previous step
			// was not successful.
			restTemplate.postForEntity(BASE_URL + "/accounts", account, Void.class);

			/// System.out.print("======>"+statusCode);

		});
		assertEquals(HttpStatus.CONFLICT, httpClientErrorException.getStatusCode());
	}

	@Test
	public void addAndDeleteBeneficiary() {
		// perform both add and delete to avoid issues with side effects

		// TODO-13: Create a new Beneficiary
		// - Remove the @Disabled on this test method.
		// - Create a new Beneficiary called "David" for the account with id 1
		// (POST the String "David" to the "/accounts/{accountId}/beneficiaries" URL).
		// - Store the returned location URI in a variable.
		URI postForLocation = restTemplate.postForLocation(BASE_URL + "/accounts/{accountId}/beneficiaries", "David",
				1);

		// TODO-14: Retrieve the Beneficiary you just created from the location that was
		// returned
		Beneficiary newBeneficiary = restTemplate.getForObject(postForLocation, Beneficiary.class);
		// Modify this line to use the restTemplate

		assertNotNull(newBeneficiary);
		assertEquals("David", newBeneficiary.getName());

		// TODO-15: Delete the newly created Beneficiary
		restTemplate.delete(postForLocation);
		HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
			System.out.println("You SHOULD get the exception \"No such beneficiary with name 'David'\" in the server.");

			// TODO-16: Try to retrieve the newly created Beneficiary again.
			// - Run this test, then. It should pass because we expect a 404 Not Found
			// If not, it is likely your delete in the previous step
			// was not successful.
			restTemplate.getForObject(postForLocation, Beneficiary.class);

		});
		assertEquals(HttpStatus.NOT_FOUND, httpClientErrorException.getStatusCode());
	}

}
