package fr.edf.distribution.linkylog.testing.ihm;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.edf.distribution.linkylog.ihm.jaxb.menuitems.Ihm;
import fr.edf.distribution.linkylog.testing.utils.XMLUtils;

/**
 * linkylog ihm fonctionelle testing pour chargement du page par click le boutton de menuItems
 */
public class TestPageLoadFromMenus {

	private static final Logger logger = Logger.getLogger(TestPageLoadFromMenus.class.getName());

	private static final String TITLE_LINKYLOG = "LinkyLog";

	private String base_url;

	private WebDriver driver;

	@Before
	public void setUp() throws Exception {
		base_url = System.getProperty("host.base.url");

		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		driver = new PhantomJSDriver(capabilities);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		login();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void test_login() throws Exception {
		Assertions.assertThat(driver.getTitle()).isEqualTo(TITLE_LINKYLOG);
	}

	@Test
	public void test_Synthese() throws Exception {
		Ihm syntheseIhm = (Ihm) XMLUtils.unmarshal(new File("src/test/resources/testSet/SyntheseMenuTest.xml"), Ihm.class);
		assertionsLoadPageFor(syntheseIhm.getMenu());
	}

	@Test
	public void test_Logs() throws Exception {
		Ihm syntheseIhm = (Ihm) XMLUtils.unmarshal(new File("src/test/resources/testSet/LogsMenuTest.xml"), Ihm.class);
		assertionsLoadPageFor(syntheseIhm.getMenu());
	}

	@Test
	public void test_Metriques() throws Exception {
		Ihm syntheseIhm = (Ihm) XMLUtils.unmarshal(new File("src/test/resources/testSet/MetriquesMenuTest.xml"), Ihm.class);
		assertionsLoadPageFor(syntheseIhm.getMenu());
	}

	@Test
	public void test_Batchs() throws Exception {
		Ihm syntheseIhm = (Ihm) XMLUtils.unmarshal(new File("src/test/resources/testSet/BatchsMenuTest.xml"), Ihm.class);
		assertionsLoadPageFor(syntheseIhm.getMenu());
	}

	@Test
	public void test_Admin() throws Exception {
		Ihm syntheseIhm = (Ihm) XMLUtils.unmarshal(new File("src/test/resources/testSet/AdminMenuTest.xml"), Ihm.class);
		assertionsLoadPageFor(syntheseIhm.getMenu());
	}

	//Helper
	private void login() {
		driver.get(base_url + "/sup/login.jsp");
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(System.getProperty("linkylog.ihm.username"));
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(System.getProperty("linkylog.ihm.password"));
		driver.findElement(By.id("btnSubmit")).click();
	}

	private void assertionsLoadPageFor(Ihm.Menu menu) throws InterruptedException {
		for (Ihm.Menu.MenuItem menuItem : menu.getMenuItem()) {
			logger.log(Level.INFO, "Test le click du boutton " + menuItem.getName() + " pour le menu " + menu.getName());
			driver.findElement(By.id(menu.getId())).click();
			driver.findElement(By.id(menuItem.getId())).click();
			String expectedUrl = base_url + "/sup/" + menuItem.getValue();
			waitForPageLoaded(expectedUrl);
			Assertions.assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
			logger.log(Level.INFO, " ---- PASS ----");
		}
	}

	private void waitForPageLoaded(final String expectedUrl) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver,60000L);
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return driver.getCurrentUrl().equals(expectedUrl);
			}
		};

		wait.until(expectedCondition);
	}
}
