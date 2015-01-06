package fr.edf.distribution.linkylog.testing.ihm;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.fest.assertions.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.edf.distribution.linkylog.testing.utils.User;

/**
 * Classe parente de tous les tests IHM
 */
public class AbstractIhmTest {

	protected WebDriver driver;

	/** Chemins des fichiers vers les resources de test */
	protected static final String TESTSET_PATH = "/testSet";
	protected static final String PROPERTIES_PATH = "/properties/";

	protected static final String TITLE_LINKYLOG = "LinkyLog";

	protected String base_url;

	/** stocke les proprietes locales a l'environnement teste */
	protected Properties properties = new Properties();

	/**
	 * Charge le fichier de propriete en fonction de l'environnment choisi et initialise le driver PhantomJS
	 * @throws Exception En cas d'erreur de chargement des proprietes
	 */
	protected void setUp() throws Exception {
		String env = System.getProperty("target.test.env");
		if(env == null) {
			// si on n'a rien precise a l'execution on run en local
			env = "local";
		}
		properties.load(getClass().getResourceAsStream(PROPERTIES_PATH + env + ".properties"));
		properties.load(getClass().getResourceAsStream(PROPERTIES_PATH + "users.properties"));

		base_url = properties.getProperty("host.base.url");
		String binary_path = System.getProperty("phantomjs.binary.path");
		if(binary_path == null) {
			// Hack pour pouvoir lancer les tests depuis IntelliJ apres un mvn clean test en local
			System.setProperty("phantomjs.binary.path", System.getProperty("user.dir") + "/sup-web-test/target/phantomjs-maven-plugin/phantomjs-1.9.7-windows/phantomjs.exe");
		}

		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		driver = new PhantomJSDriver(capabilities);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	/**
	 * Ferme le driver PhantomJS
	 * @throws Exception en cas d'erreur à la fermeture
	 */
	protected void tearDown() throws Exception {
			driver.quit();
		}

	/**
	 * Se logger avec l'utilisateur et le mot de passe passés en paramètres
	 * Les valeurs choisies doivent correspondre a un utilisateur declare dans le WebLogic de l'environnement testé
	 * @param user l'utilisateur
	 */
	protected void login(User user) {
		driver.get(base_url + "/sup/login.jsp");
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(user.getLogin(properties));
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(user.getPassword(properties));
		driver.findElement(By.id("btnSubmit")).click();
		Assertions.assertThat(driver.getTitle()).isEqualTo(TITLE_LINKYLOG);
	}

	/**
	 * Permet d'attendre qu'une page soit chargée.
	 * @param expectedUrl l'URL attendue
	 * @throws InterruptedException En cas d'interruption de l'attente
	 */
	protected void waitForPageLoaded(final String expectedUrl) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver,60000L);
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return driver.getCurrentUrl().equals(expectedUrl);
			}
		};

		wait.until(expectedCondition);
	}

	/**
	 * Permet d'attendre que le contenu d'une liste soit chargée.
	 * @param listBoxName l'ID ZK de la listbox
	 * @throws InterruptedException En cas d'interruption de l'attente
	 */
	protected void waitForListLoaded(final String listBoxName) throws InterruptedException {
		WebElement listbox = findElementById(listBoxName + "_listbox");
		waitForXPath("//tbody[@id='" + listbox.getAttribute("id") + "-rows']/tr[1]/td");
	}

	/**
	 * Controle si une valeur est presente dans une cellule d'une liste, en attendant que cette cellule soit affichee
	 * @param expected la valeur attendue
	 * @param listBoxName le nom de l'objet listbox
	 * @param rowNum le numero de ligne (commence a 1, de haut en bas)
	 * @param cellNum le numero de cellule (commence a 1, de gauche a droite)
	 */
	protected void waitAndAssertCellTextValueInListbox(String expected, String listBoxName, int rowNum, int cellNum) {
		assertTextValue(expected, waitForCellInListbox(listBoxName, rowNum, cellNum));
	}

	/**
	 * Attend qu'une cellule particulière d'une listbox existe, et la retourne
	 * @param listBoxName l'ID ZK de la listbox
	 * @param rowNum le numero de ligne (commence a 1, de haut en bas)
	 * @param cellNum le numero de cellule (commence a 1, de gauche a droite)
	 * @return la cellule
	 */
	protected WebElement waitForCellInListbox(String listBoxName, int rowNum, int cellNum) {
		return waitForXPath("//*[contains(@id, '" + listBoxName + "_listbox')]//*[contains(@id, '_listitem')][" + rowNum + "]//*[contains(@id, " +
												"'_listcell')][" + cellNum + "]");
	}

	/**
	 * Attend qu'un élément sélectionné par XPath soit sélectionné
	 * @param xpath le XPath de sélection
	 * @return l'élément trouvé
	 */
	protected WebElement waitForXPath(String xpath) {
		WebDriverWait wait = new WebDriverWait(driver,10L);
		return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	}

	/**
	 * Controle qu'au bout de 10 secondes le resultat affiche dans une listbox est le message d'absence de donnees,
	 * et qu'il n'y a aucune ligne de resultat
	 * @param listBoxName l'ID ZK de la listbox
	 */
	protected void waitForListboxEmpty(final String listBoxName) {
		WebDriverWait wait = new WebDriverWait(driver,10L);
		ExpectedCondition<Boolean> styleIsEmpty = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return StringUtils.isEmpty(findElementById(listBoxName + "_listbox-empty").getAttribute("style"));
			}
		};
		wait.until(styleIsEmpty);
		// controle qu'il n'y a aucune ligne dans la listbox
		assertEquals(0, findElementById(listBoxName + "_listbox-rows").findElements(By.tagName("tr")).size());
	}

	/**
	 * Attend qu'un texte soit présent dans la valeur d'un input sélectionné via XPath
	 * @param expected la valeur attendue
	 * @param xpath le XPath de sélection
	 */
	protected void waitForValue(String expected, String xpath) {
		WebDriverWait wait = new WebDriverWait(driver,10L);
		wait.until(ExpectedConditions.textToBePresentInElementValue(By.xpath(xpath), expected));
	}

	/**
	 * Controle que le contenu textuel d'un tag (via <code>element.getAttribute("textContent")</code>) est celui attendu
	 * @param expected le contenu attendu
	 * @param element l'element a tester
	 */
	protected static void assertTextValue(String expected, WebElement element) {
		assertEquals(expected, element.getAttribute("textContent"));
	}

	/**
	 * trouve un tag a partir de son ID ZK. Attention : depend de l'implement de {@code ZKComponentIdGenerator}.
	 * Cette methode s'attend a trouver des id de la forme "indice_idZK".
	 * Elle implemente un equivalent XPath 1.0 de la fonction XPath 2.0 "ends-with"
	 * @param id l'ID a trouver
	 * @return l'element trouve s'il existe
	 */
	protected WebElement findElementById(String id) {
		// equivalent XPath 1.0 de la fonction XPath 2.0 "ends-with"
		return driver.findElement(By.xpath("//*['" + id + "'=substring(@id, string-length(@id)-string-length('"+ id +"')+1)]"));
	}

	/**
	 * Ecrit une valeur dans une textbox et declenche l'evenement blur nécessaire pour la prise en compte par ZK
	 * @param id l'ID ZK de la textbox
	 * @param value La valeur a indiquer dans la textbox
	 */
	protected void writeValueInTextbox(String id, String value) {
		WebElement textBox = findElementById(id + "_textbox");
		textBox.clear();
		textBox.sendKeys(value);
		((JavascriptExecutor)driver).executeScript("return document.getElementById('" + textBox.getAttribute("id") + "').blur()");
	}

	/**
	 * Vide le contenu d'une textbox
	 * @param id l'ID ZK de la textbox
	 */
	protected void clearTextBox(String id) {
		findElementById(id + "_textbox").clear();
	}

	/**
	 * Recherche un bouton a partir de son ID ZK et clique
	 * @param id l'ID ZK du bouton
	 */
	protected void clickButton(String id) {
		findElementById(id + "_button").click();
	}

}
