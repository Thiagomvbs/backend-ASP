package backend.ASP;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requer banco PostgreSQL e variáveis de ambiente")
@SpringBootTest
class AspApplicationTests {

	// Precisa de PostgreSQL e das variáveis de ambiente (DB_URL, JWT_SECRET, MAIL_TO...).
	// Reativar quando houver um banco de teste (por exemplo, no CI).
	@Test
	void contextLoads() {
	}

}