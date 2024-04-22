package config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

@Configuration
public class RewardsConfig {

	@Autowired
	DataSource dataSource;

	@Bean
	RewardNetwork rewardNetwork(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
			RewardRepository rewardRepository) {
		return new RewardNetworkImpl(accountRepository, restaurantRepository, rewardRepository);
	}

	@Bean
	AccountRepository accountRepository(JdbcTemplate jdbcTemplate) {
		JdbcAccountRepository repository = new JdbcAccountRepository(jdbcTemplate);
		return repository;
	}

	@Bean
	RestaurantRepository restaurantRepository(JdbcTemplate jdbcTemplate) {
		JdbcRestaurantRepository repository = new JdbcRestaurantRepository(jdbcTemplate);
		return repository;
	}

	@Bean
	RewardRepository rewardRepository(JdbcTemplate jdbcTemplate) {
		JdbcRewardRepository repository = new JdbcRewardRepository(jdbcTemplate);
		return repository;
	}

	@Bean
	JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

}
