package fr.edf.distribution.linkylog.testing.ihm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.edf.distribution.linkylog.ihm.jaxb.menuitems.Ihm;
import fr.edf.distribution.linkylog.testing.utils.User;
import fr.edf.distribution.linkylog.testing.utils.XMLUtils;

/**
 * linkylog ihm fonctionelle testing pour chargement du page par click le boutton de menuItems
 */
public class TestPageLoadFromMenus extends AbstractIhmTest {

	private static final Logger logger = Logger.getLogger(TestPageLoadFromMenus.class.getName());

	@Before
	public void setUp() throws Exception {
		super.setUp();
		login(User.ADMIN);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test_menus() throws Exception {
		Assertions.assertThat(driver.getTitle()).isEqualTo(TITLE_LINKYLOG);
		assertionsLoadPageFor("SyntheseMenuTest.xml");
		assertionsLoadPageFor("LogsMenuTest.xml");
		assertionsLoadPageFor("MetriquesMenuTest.xml");
		assertionsLoadPageFor("BatchsMenuTest.xml");
		assertionsLoadPageFor("AdminMenuTest.xml");
	}

	private void assertionsLoadPageFor(String fileName) throws Exception {
		Ihm.Menu menu = ((Ihm) XMLUtils.unmarshal(getClass().getResourceAsStream(TESTSET_PATH + "/" + fileName), Ihm.class)).getMenu();
		for (Ihm.Menu.MenuItem menuItem : menu.getMenuItem()) {
			logger.log(Level.INFO, "Test le click du boutton " + menuItem.getName() + " pour le menu " + menu.getName());
			findElementById(menu.getId()).click();
			findElementById(menuItem.getId()).click();
			String expectedUrl = base_url + "/sup/" + menuItem.getValue();
			waitForPageLoaded(expectedUrl);
			Assertions.assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
			logger.log(Level.INFO, " ---- PASS ----");
		}
	}
}
